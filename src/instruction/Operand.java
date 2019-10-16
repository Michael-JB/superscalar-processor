package instruction;

public abstract class Operand {

  protected final int value;

  public Operand(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}