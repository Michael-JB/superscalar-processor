package instruction;

public class CompareInstruction extends Instruction {

  public CompareInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.CMP, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}