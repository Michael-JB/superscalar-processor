package instruction;

public class StoreInstruction extends Instruction {

  public StoreInstruction(RegisterOperand val, RegisterOperand base, ValueOperand off) {
    super(Opcode.SA, val, base, off);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get();
  }

}