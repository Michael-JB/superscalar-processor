package instruction;

public class JumpInstruction extends Instruction {

  public JumpInstruction(ValueOperand dest) {
    super(Opcode.JMP, dest);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[0].getExecutionValue().get();
  }

}