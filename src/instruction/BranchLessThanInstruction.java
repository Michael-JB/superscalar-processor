package instruction;

public class BranchLessThanInstruction extends Instruction {

  public BranchLessThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BLT, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() < values[1].getValue() ? values[2].getValue() : 0;
  }

}