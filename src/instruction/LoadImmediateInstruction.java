package instruction;

public class LoadImmediateInstruction extends Instruction {

  public LoadImmediateInstruction(RegisterOperand dest, ValueOperand addr) {
    super(Opcode.LAI, dest, addr);
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[1].getValue();
  }

}