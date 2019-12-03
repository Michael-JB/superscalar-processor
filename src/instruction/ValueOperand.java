package instruction;

public class ValueOperand extends Operand {

  public ValueOperand(int value) {
    super(value);
    setExecutionValue(value);
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

}