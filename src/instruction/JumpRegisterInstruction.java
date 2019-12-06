package instruction;

public class JumpRegisterInstruction extends Instruction {

  public JumpRegisterInstruction(RegisterOperand src) {
    super(Opcode.JMPR, src);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[0].getExecutionValue().get();
  }

}