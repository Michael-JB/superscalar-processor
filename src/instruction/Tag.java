package instruction;

public class Tag {

  private final int value;

  public Tag(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public boolean matches(Tag other) {
    return other.getValue() == this.value;
  }

  @Override
  public String toString() {
    return "t" + value;
  }

}