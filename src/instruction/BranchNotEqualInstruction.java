package instruction;

public class BranchNotEqualInstruction extends Instruction {

  public BranchNotEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BNE, srcA, srcB, off);
  }

  @Override
  public int eval() {
    return operands[0].getExecutionValue().get() != operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}