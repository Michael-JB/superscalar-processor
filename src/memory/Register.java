package memory;

public class Register {

  private final int id;
  private int value = -1;
  private RegisterFlag flag;

  public Register(int id) {
    this.id = id;
    this.flag = RegisterFlag.VALID;
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

  public RegisterFlag getFlag() {
    return flag;
  }

  public void setFlag(RegisterFlag flag) {
    this.flag = flag;
  }

  @Override
  public String toString() {
    return "r" + id + ": " + value;
  }

}