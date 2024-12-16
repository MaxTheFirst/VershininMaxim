package exceptions;

public class ArticleDeleteException extends RuntimeException {
  public ArticleDeleteException(String message) {
    super(message);
  }
}
