package instruction;

public class BranchGreaterThanOrEqualInstruction extends Instruction {

  public BranchGreaterThanOrEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGE, srcA, srcB, off);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[0].getExecutionValue().get() >= operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}