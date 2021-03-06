package instruction;

public class ValueOperand extends Operand {

  public ValueOperand(int value) {
    super(value);
  }

  @Override
  public DecodedOperand decode() {
    return new DecodedValueOperand(this);
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

}