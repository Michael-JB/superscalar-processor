package instruction;

public class RegisterOperand extends Operand {

  private final RegisterOperandCategory category;

  public RegisterOperand(int value, RegisterOperandCategory category) {
    super(value);
    this.category = category;
  }

  public RegisterOperandCategory getCategory() {
    return category;
  }

  @Override
  public DecodedOperand decode() {
    return new DecodedRegisterOperand(this, category.equals(RegisterOperandCategory.SOURCE));
  }

  @Override
  public String toString() {
    return "r" + value;
  }

}