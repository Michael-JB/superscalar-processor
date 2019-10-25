package instruction;

public class StoreInstruction extends Instruction {

  public StoreInstruction(RegisterOperand val, RegisterOperand base, ValueOperand off) {
    super(Opcode.SA, val, base, off);
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[1].getValue() + values[2].getValue();
  }

}