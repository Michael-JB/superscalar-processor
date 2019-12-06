package instruction;

public class BranchEqualInstruction extends Instruction {

  public BranchEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BEQ, srcA, srcB, off);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[0].getExecutionValue().get() == operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}