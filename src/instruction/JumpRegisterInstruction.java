package instruction;

public class JumpRegisterInstruction extends Instruction {

  public JumpRegisterInstruction(RegisterOperand src) {
    super(Opcode.JMPR, src);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue();
  }

}