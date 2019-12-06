package instruction;

public class SubtractImmediateInstruction extends Instruction {

  public SubtractImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.SUBI, dest, src, val);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[1].getExecutionValue().get() - operands[2].getExecutionValue().get();
  }

}