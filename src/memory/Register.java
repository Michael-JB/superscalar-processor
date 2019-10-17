package memory;

public class Register {

  private final int id;
  private int value = -1;

  public Register(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "r" + value;
  }

}