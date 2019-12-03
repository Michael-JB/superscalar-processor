package instruction;

public class BranchLessThanInstruction extends Instruction {

  public BranchLessThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BLT, srcA, srcB, off);
  }

  @Override
  public int eval() {
    return operands[0].getExecutionValue().get() < operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}