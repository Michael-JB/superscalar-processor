package instruction;

public class LoadImmediateInstruction extends Instruction {

  public LoadImmediateInstruction(RegisterOperand dest, ValueOperand addr) {
    super(Opcode.LDI, new Operand[] { dest, addr });
  }

  @Override
  public void execute() {
    System.out.println(this.toString());
  }

}