package instruction;

public class SubtractInstruction extends Instruction {

  public SubtractInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.SUB, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() - values[2].getValue();
  }

}