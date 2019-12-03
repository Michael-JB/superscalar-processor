package instruction;

public class LoadImmediateInstruction extends Instruction {

  public LoadImmediateInstruction(RegisterOperand dest, ValueOperand addr) {
    super(Opcode.LAI, dest, addr);
  }

  @Override
  public int eval() {
    return operands[1].getExecutionValue().get();
  }

}