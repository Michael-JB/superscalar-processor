package instruction;

public class JumpInstruction extends Instruction {

  public JumpInstruction(ValueOperand dest) {
    super(Opcode.JMP, dest);
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[0].getValue();
  }

}