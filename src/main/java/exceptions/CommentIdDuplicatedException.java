package exceptions;

public class CommentIdDuplicatedException extends RuntimeException {
  public CommentIdDuplicatedException(String message) {
    super(message);
  }
}
