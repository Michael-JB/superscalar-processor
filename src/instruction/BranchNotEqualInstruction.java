package instruction;

public class BranchNotEqualInstruction extends Instruction {

  public BranchNotEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BNE, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() != values[1].getValue() ? values[2].getValue() : 0;
  }

}