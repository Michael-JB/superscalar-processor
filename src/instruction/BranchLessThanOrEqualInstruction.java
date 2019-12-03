package instruction;

public class BranchLessThanOrEqualInstruction extends Instruction {

  public BranchLessThanOrEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BLE, srcA, srcB, off);
  }

  @Override
  public int eval() {
    return operands[0].getExecutionValue().get() <= operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 0;
  }

}