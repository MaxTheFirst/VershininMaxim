package types.comment;

import exceptions.IllegalIdException;

import java.util.Objects;

public class CommentId {
  private final long value;

  public CommentId(Long value) {
    if (value < 0) {
      throw new IllegalIdException("The ID must be >=0");
    }
    this.value = value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommentId commentId = (CommentId) o;
    return value == commentId.value;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
