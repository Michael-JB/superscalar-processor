package instruction;

public class DivideInstruction extends Instruction {

  public DivideInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.DIV, new Operand[] { dest, srcA, srcB });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}