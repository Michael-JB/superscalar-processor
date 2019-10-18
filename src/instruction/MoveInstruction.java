package instruction;

public class MoveInstruction extends Instruction {

  public MoveInstruction(RegisterOperand dest, ValueOperand val) {
    super(Opcode.MOVE, new Operand[] { dest, val });
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[1].getValue();
  }

}