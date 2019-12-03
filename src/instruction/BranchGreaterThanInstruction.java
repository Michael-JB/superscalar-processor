package instruction;

public class BranchGreaterThanInstruction extends Instruction {

  public BranchGreaterThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGT, srcA, srcB, off);
  }

  @Override
  public int eval() {
    return operands[0].getExecutionValue().get() > operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}