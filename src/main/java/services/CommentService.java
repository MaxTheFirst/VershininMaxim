package services;

import exceptions.*;
import repositories.article.ArticleRepository;
import repositories.comment.CommentRepository;
import types.article.ArticleId;
import types.comment.Comment;
import types.comment.CommentId;

public class CommentService {
  private final ArticleRepository articleRepository;
  private final CommentRepository commentRepository;

  public CommentService(ArticleRepository articleRepository, CommentRepository commentRepository) {
    this.articleRepository = articleRepository;
    this.commentRepository = commentRepository;
  }

  public Comment findCommentById(long id) {
    try {
      return commentRepository.findById(id);
    } catch (CommentNotFoundException e) {
      throw new CommentNotFoundException("Cannot find comment with id=" + id);
    }
  }

  public long createComment(long articleId, String text) {
    try {
      articleRepository.findById(articleId);
      long commentId = commentRepository.generateId();
      Comment comment = new Comment(new CommentId(commentId), new ArticleId(articleId), text);
      commentRepository.create(comment);
      return comment.getId();
    } catch (CommentIdDuplicatedException | ArticleNotFoundException e) {
      throw new CommentCreateException("Cannot create comment");
    }
  }

  public void commentUpdate(long commentId, String text) {
    Comment comment;
    try {
      comment = commentRepository.findById(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentUpdateException("Cannot find comment with id=" + commentId);
    }
    commentRepository.update(comment.newComment(text));
  }

  public void deleteComment(long commentId) {
    try {
      commentRepository.findById(commentId);
    } catch (CommentNotFoundException e) {
      throw new CommentDeleteException("Cannot delete comment with id=" + commentId);
    }
    commentRepository.delete(commentId);
  }
}
