package instruction;

public class LoadImmediateInstruction extends Instruction {

  public LoadImmediateInstruction(RegisterOperand dest, ValueOperand addr) {
    super(Opcode.LDI, new Operand[] { dest, addr });
  }

  @Override
  public int perform(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}