package instruction;

public class SubtractImmediateInstruction extends Instruction {

  public SubtractImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.SUBI, dest, src, val);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() - values[2].getValue();
  }

}