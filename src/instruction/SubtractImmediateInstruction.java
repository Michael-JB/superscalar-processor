package instruction;

public class SubtractImmediateInstruction extends Instruction {

  public SubtractImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.SUBI, new Operand[] { dest, src, val });
  }

  @Override
  public int perform(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}