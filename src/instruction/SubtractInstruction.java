package instruction;

public class SubtractInstruction extends Instruction {

  public SubtractInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.SUB, new Operand[] { dest, srcA, srcB });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}