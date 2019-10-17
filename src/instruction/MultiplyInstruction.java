package instruction;

public class MultiplyInstruction extends Instruction {

  public MultiplyInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.MUL, new Operand[] { dest, srcA, srcB });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}