package exceptions;

public class CommentUpdateException extends RuntimeException {
  public CommentUpdateException(String message) {
    super(message);
  }
}
