package sort;

public class ElementCountLimitedException extends Exception {
  public ElementCountLimitedException(String message) {
    super(message);
  }
  public ElementCountLimitedException() {
    super("A large number of elements.");
  }
}
