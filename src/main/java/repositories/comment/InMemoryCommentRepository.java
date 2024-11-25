package repositories.comment;

import exceptions.CommentIdDuplicatedException;
import exceptions.CommentNotFoundException;
import types.comment.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCommentRepository implements CommentRepository {
  private final AtomicLong nextId = new AtomicLong(0);
  private final Map<Long, Comment> comments = new ConcurrentHashMap<>();

  @Override
  public long generateId() {
    return nextId.incrementAndGet();
  }

  @Override
  public List<Comment> findAll() {
    return new ArrayList<>(comments.values());
  }

  @Override
  public Comment findById(long commentId) {
    Comment comment = comments.get(commentId);
    if (comment == null) {
      throw new CommentNotFoundException("Cannot find comment by id=" + commentId);
    }
    return comment;
  }

  @Override
  public synchronized void create(Comment comment) {
    if (comments.get(comment.getId()) != null) {
      throw new CommentIdDuplicatedException("Comment with the given id already exists: " + comment.getId());
    }
    comments.put(comment.getId(), comment);
  }

  @Override
  public synchronized void update(Comment comment) {
    if (comments.get(comment.getId()) == null) {
      throw new CommentNotFoundException("Cannot find comment by id=" + comment.getId());
    }
    comments.put(comment.getId(), comment);
  }

  @Override
  public void delete(long commentId) {
    if (comments.remove(commentId) == null) {
      throw new CommentNotFoundException("Cannot find comment by id=" + commentId);
    }
  }
}
