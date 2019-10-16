package instruction;

public class AddInstruction extends Instruction {

  public AddInstruction(Opcode opcode, Operand... operands) {
    super(opcode, operands);
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}