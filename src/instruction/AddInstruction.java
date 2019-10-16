package instruction;

public class AddInstruction extends Instruction {

  public AddInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.ADD, new Operand[] { dest, srcA, srcB });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}