package repositories.comment;

import exceptions.CommentIdDuplicatedException;
import exceptions.CommentNotFoundException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import types.article.Article;
import types.article.ArticleId;
import types.comment.Comment;
import types.comment.CommentId;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCommentRepository implements CommentRepository {
  private final Jdbi jdbi;

  public InMemoryCommentRepository(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public List<Comment> findAll() {
    return jdbi.inTransaction((Handle handle) -> {
      List<Map<String, Object>> result =
          handle.createQuery("SELECT * FROM comments")
              .mapToMap()
              .list();
      return result.stream().map(element -> new Comment(
          new CommentId((Long) element.get("comment_id")),
          new ArticleId((Long) element.get("article_id")),
          (String) element.get("content")
      )).collect(Collectors.toList());
    });
  }

  @Override
  public Optional<Comment> findById(long commentId) {
    return jdbi.inTransaction((Handle handle) -> {
      try {
        Map<String, Object> element =
            handle.createQuery("SELECT * FROM comments WHERE comment_id = :id")
                .bind("id", commentId)
                .mapToMap()
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Cannot find comment by id=" + commentId));
        return Optional.of(new Comment(
            new CommentId((Long) element.get("comment_id")),
            new ArticleId((Long) element.get("article_id")),
            (String) element.get("content")
        ));
      } catch (IllegalStateException e) {
        return Optional.empty();
      }
    });
  }

  @Override
  public long generateId() {
    return jdbi.withHandle(handle ->
        handle.createQuery("SELECT COALESCE(MAX(comment_id), 0) + 1 AS next_id FROM comments")
            .mapTo(Long.class)
            .one()
    );
  }

  @Override
  public void create(Comment comment) {
    jdbi.useTransaction(handle -> {
      int rowsAffected = handle.createUpdate(
              "INSERT INTO comments (comment_id, article_id, content) VALUES (:id, :articleId, :content)")
          .bind("id", comment.getId())
          .bind("articleId", comment.getArticleId().getValue())
          .bind("content", comment.getText())
          .execute();

      if (rowsAffected == 0) {
        throw new CommentIdDuplicatedException("Comment with the given id already exists: " + comment.getId());
      }

      updateArticleTrendingStatus(comment.getArticleId());
    });
  }

  @Override
  public void update(Comment comment) {
    jdbi.useTransaction(handle -> {
      int rowsAffected = handle.createUpdate(
              "UPDATE comments SET article_id = :articleId, content = :content WHERE comment_id = :id")
          .bind("id", comment.getId())
          .bind("articleId", comment.getArticleId().getValue())
          .bind("content", comment.getText())
          .execute();

      if (rowsAffected == 0) {
        throw new CommentNotFoundException("Cannot find comment by id=" + comment.getId());
      }

      updateArticleTrendingStatus(comment.getArticleId());
    });
  }

  @Override
  public void delete(long commentId) {
    jdbi.useTransaction(handle -> {
      Long articleId = handle.createQuery("SELECT article_id FROM comments WHERE comment_id = :id")
          .bind("id", commentId)
          .mapTo(Long.class)
          .findFirst()
          .orElseThrow(() -> new CommentNotFoundException("Cannot find comment by id=" + commentId));

      int rowsAffected = handle.createUpdate("DELETE FROM comments WHERE comment_id = :id")
          .bind("id", commentId)
          .execute();

      if (rowsAffected == 0) {
        throw new CommentNotFoundException("Cannot find comment by id=" + commentId);
      }

      updateArticleTrendingStatus(new ArticleId(articleId));
    });
  }

  private void updateArticleTrendingStatus(ArticleId articleId) {
    jdbi.useTransaction(handle -> {
      long commentCount = handle.createQuery("SELECT COUNT(*) FROM comments WHERE article_id = :articleId")
          .bind("articleId", articleId.getValue())
          .mapTo(Long.class)
          .one();

      boolean trending = commentCount > 3;

      handle.createUpdate("UPDATE articles SET trending = :trending WHERE article_id = :id")
          .bind("trending", trending)
          .bind("id", articleId.getValue())
          .execute();
    });
  }
}
