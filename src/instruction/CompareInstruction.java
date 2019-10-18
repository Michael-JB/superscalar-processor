package instruction;

public class CompareInstruction extends Instruction {

  public CompareInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.CMP, new Operand[] { dest, srcA, srcB });
  }

  @Override
  public int perform(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}