package exceptions;

public class ArticleIdDuplicatedException extends RuntimeException {
  public ArticleIdDuplicatedException(String message) {
    super(message);
  }
}
