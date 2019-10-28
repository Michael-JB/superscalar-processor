package instruction;

public class BranchGreaterThanOrEqualInstruction extends Instruction {

  public BranchGreaterThanOrEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGE, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() >= values[1].getValue() ? values[2].getValue() : 0;
  }

}