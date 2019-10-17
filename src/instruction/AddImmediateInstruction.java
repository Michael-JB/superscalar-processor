package instruction;

public class AddImmediateInstruction extends Instruction {

  public AddImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.ADDI, new Operand[] { dest, src, val });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}