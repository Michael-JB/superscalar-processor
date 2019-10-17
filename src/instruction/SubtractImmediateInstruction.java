package instruction;

public class SubtractImmediateInstruction extends Instruction {

  public SubtractImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.SUBI, new Operand[] { dest, src, val });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}