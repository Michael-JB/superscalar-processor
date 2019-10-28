package instruction;

public class CompareInstruction extends Instruction {

  public CompareInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.CMP, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    if (values[1].getValue() < values[2].getValue()) {
      return -1;
    } else if (values[1].getValue() > values[2].getValue()) {
      return 1;
    } else {
      return 0;
    }
  }

}