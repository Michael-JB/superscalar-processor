package instruction;

public class BranchGreaterThanInstruction extends Instruction {

  public BranchGreaterThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGT, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() > values[1].getValue() ? values[2].getValue() : 0;
  }

}