package repositories.comment;

import types.comment.Comment;

import java.util.List;

public interface CommentRepository {
  long generateId();

  List<Comment> findAll();

  Comment findById(long commentId);

  void create(Comment comment);

  void update(Comment comment);

  void delete(long commentId);
}
