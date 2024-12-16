package repositories.article;

import exceptions.ArticleCreateException;
import exceptions.ArticleDeleteException;
import exceptions.ArticleNotFoundException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.postgresql.jdbc.PgArray;
import types.article.Article;
import types.article.ArticleId;
import types.comment.Comment;
import types.comment.CommentId;

import javax.swing.text.html.Option;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryArticleRepository implements ArticleRepository {
  private final Jdbi jdbi;

  public InMemoryArticleRepository(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<Article> findAll() {
    return jdbi.inTransaction(handle -> {
      List<Map<String, Object>> articlesResult =
          handle.createQuery("SELECT * FROM articles")
              .mapToMap()
              .list();

      Map<ArticleId, List<Comment>> commentsByArticleId = handle.createQuery(
              "SELECT * FROM comments")
          .mapToMap()
          .list()
          .stream()
          .map(row -> new Comment(
              new CommentId((Long) row.get("comment_id")),
              new ArticleId((Long) row.get("article_id")),
              (String) row.get("content")
          ))
          .collect(Collectors.groupingBy(Comment::getArticleId));

      return articlesResult.stream()
          .map(row -> {
            Article article = null;
            try {
              article = new Article(
                  new ArticleId((Long) row.get("article_id")),
                  (String) row.get("name"),
                  parseTags((PgArray) row.get("tags"))
              ).setTrending((Boolean) row.get("trending"));
            } catch (SQLException e) {
              return null;
            }

            article.setComments(commentsByArticleId.getOrDefault(
                article.getId(),
                Collections.emptyList()
            ));

            return article;
          })
          .collect(Collectors.toList());
    });
  }

  @Override
  public Optional<Article> findById(long articleId) {
    try {
      return jdbi.inTransaction(handle -> {
        try {
          Map<String, Object> row =
              handle.createQuery("SELECT * FROM articles WHERE article_id = :id")
                  .bind("id", articleId)
                  .mapToMap()
                  .first();

          Article article = new Article(
              new ArticleId((Long) row.get("article_id")),
              (String) row.get("name"),
              parseTags((PgArray) row.get("tags"))
          ).setTrending((Boolean) row.get("trending"));

          List<Comment> comments = handle.createQuery(
                  "SELECT * FROM comments WHERE article_id = :id")
              .bind("id", articleId)
              .mapToMap()
              .list()
              .stream()
              .map(commentRow -> new Comment(
                  new CommentId((Long) commentRow.get("comment_id")),
                  new ArticleId((Long) commentRow.get("article_id")),
                  (String) commentRow.get("content")
              ))
              .collect(Collectors.toList());

          article.setComments(comments);
          return Optional.of(article);
        } catch (IllegalStateException e) {
          return Optional.empty();
        }
      });
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

  @Override
  public long generateId() {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT COALESCE(MAX(article_id), 0) + 1 AS next_id FROM articles")
            .mapTo(Long.class)
            .one()
    );
  }

  @Override
  public void create(Article article) {
    jdbi.inTransaction(handle -> {
      int rowsAffected = handle.createUpdate(
              "INSERT INTO articles (article_id, name, tags, trending) VALUES (:id, :name, :tags, :trending)")
          .bind("id", article.getId())
          .bind("name", article.getName())
          .bind("tags", getTags(article.getTags())) // Привязка массива
          .bind("trending", article.isTrending())
          .execute();

      if (rowsAffected == 0) {
        throw new ArticleCreateException("Failed to create article");
      }
      return null;
    });
  }

  @Override
  public void update(Article article) {
    jdbi.useTransaction(handle -> {
      int rowsAffected = handle.createUpdate(
              "UPDATE articles SET name = :name, tags = :tags, trending = :trending WHERE article_id = :id")
          .bind("id", article.getId())
          .bind("name", article.getName())
          .bind("tags", getTags(article.getTags()))
          .bind("trending", article.isTrending())
          .execute();

      if (rowsAffected == 0) {
        throw new ArticleNotFoundException("Cannot find article by id=" + article.getId());
      }
    });
  }

  @Override
  public void delete(long articleId) {
    jdbi.useTransaction(handle -> {
      handle.createUpdate("DELETE FROM comments WHERE article_id = :id")
          .bind("id", articleId)
          .execute();

      int rowsAffected = handle.createUpdate("DELETE FROM articles WHERE article_id = :id")
          .bind("id", articleId)
          .execute();

      if (rowsAffected == 0) {
        throw new ArticleDeleteException("Cannot delete article by id=" + articleId);
      }
    });
  }

  private String[] getTags(Set<String> tags) {
    return tags.toArray(new String[0]);
  }

  public static Set<String> parseTags(PgArray pgArray) throws SQLException {
    if (pgArray == null) {
      return new HashSet<>();
    }

    Object[] array = (Object[]) pgArray.getArray();
    if (array == null || array.length == 0) {
      return new HashSet<>();
    }

    return Arrays.stream(array)
        .map(Object::toString)
        .collect(HashSet::new, HashSet::add, HashSet::addAll);
  }

}
