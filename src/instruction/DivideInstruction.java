package instruction;

public class DivideInstruction extends Instruction {

  public DivideInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.DIV, dest, srcA, srcB);
  }

  @Override
  public int perform(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}