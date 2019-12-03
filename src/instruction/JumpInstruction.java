package instruction;

public class JumpInstruction extends Instruction {

  public JumpInstruction(ValueOperand dest) {
    super(Opcode.JMP, dest);
  }

  @Override
  public int eval() {
    return operands[0].getExecutionValue().get();
  }

}