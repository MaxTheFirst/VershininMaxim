package exceptions;

public class ArticleCreateException extends RuntimeException {
  public ArticleCreateException(String message) {
    super(message);
  }
}
