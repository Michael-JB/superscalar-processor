package instruction;

public class MultiplyInstruction extends Instruction {

  public MultiplyInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.MUL, dest, srcA, srcB);
  }

  @Override
  public int perform(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}