package instruction;

public class MultiplyInstruction extends Instruction {

  public MultiplyInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.MUL, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() * values[2].getValue();
  }

}