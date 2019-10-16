package instruction;

public class RegisterOperand extends Operand {

  public RegisterOperand(int value) {
    super(value);
  }

  @Override
  public String toString() {
    return "r" + value;
  }

}