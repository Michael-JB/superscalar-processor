package instruction;

public class MoveInstruction extends Instruction {

  public MoveInstruction(RegisterOperand dest, ValueOperand val) {
    super(Opcode.MOVE, dest, val);
  }

  @Override
  public int eval() {
    return operands[1].getExecutionValue().get();
  }

}