package instruction;

public class LoadInstruction extends Instruction {

  public LoadInstruction(RegisterOperand dest, RegisterOperand base, ValueOperand off) {
    super(Opcode.LD, new Operand[] { dest, base, off });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}