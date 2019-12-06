package instruction;

public class StoreImmediateInstruction extends Instruction {

  public StoreImmediateInstruction(RegisterOperand val, ValueOperand addr) {
    super(Opcode.SAI, val, addr);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[1].getExecutionValue().get();
  }

}