package instruction;

public class SubtractInstruction extends Instruction {

  public SubtractInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.SUB, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}