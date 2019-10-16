package instruction;

public class MoveInstruction extends Instruction {

  public MoveInstruction(RegisterOperand dest, ValueOperand val) {
    super(Opcode.MOVE, new Operand[] { dest, val });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}