package instruction;

public class BranchEqualInstruction extends Instruction {

  public BranchEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BEQ, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() == values[1].getValue() ? values[2].getValue() : 0;
  }

}