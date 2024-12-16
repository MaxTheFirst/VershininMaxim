package repositories.article;

import exceptions.ArticleCreateException;
import exceptions.ArticleDeleteException;
import exceptions.ArticleNotFoundException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import types.article.Article;
import types.article.ArticleId;
import types.comment.Comment;
import types.comment.CommentId;

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
              "SELECT * FROM comment")
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
            Article article = new Article(
                new ArticleId((Long) row.get("article_id")),
                (String) row.get("name"),
                parseTags((Object[]) row.get("tags"))
            ).setTrending((Boolean) row.get("trending"));

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
            parseTags((Object[]) row.get("tags"))
        ).setTrending((Boolean) row.get("trending"));

        List<Comment> comments = handle.createQuery(
                "SELECT * FROM comment WHERE article_id = :id")
            .bind("id", articleId)
            .mapToMap()
            .list()
            .stream()
            .map(commentRow -> new Comment(
                new CommentId((Long) row.get("comment_id")),
                new ArticleId((Long) row.get("article_id")),
                (String) commentRow.get("content")
            ))
            .collect(Collectors.toList());

        article.setComments(comments);

        return Optional.of(article);
      } catch (IllegalStateException e) {
        return Optional.empty();
      }
    });
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
      int rowsAffected =  handle.createUpdate(
              "INSERT INTO articles (article_id, header, tags, trending) VALUES (:id, :header, :tags, :trending)")
          .bind("id", article.getId())
          .bind("header", article.getName())
          .bind("tags", article.getTags().toArray(new String[0]))
          .bind("trending", article.isTrending())
          .execute();

      if (rowsAffected == 0) {
        throw new ArticleCreateException("Fail create");
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
          .bind("tags", article.getTags().toArray(new String[0]))
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

  private Set<String> parseTags(Object[] tagsArray) {
    if (tagsArray == null || tagsArray.length == 0) {
      return new HashSet<>();
    }
    return Arrays.stream(tagsArray)
        .map(Object::toString)
        .collect(Collectors.toSet());
  }
}
