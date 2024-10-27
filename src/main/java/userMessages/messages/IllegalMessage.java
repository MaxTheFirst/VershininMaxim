package userMessages.messages;

public class IllegalMessage extends RuntimeException {
  public IllegalMessage(String message) {
    super(message);
  }
}
