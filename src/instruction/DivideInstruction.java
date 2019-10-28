package instruction;

public class DivideInstruction extends Instruction {

  public DivideInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.DIV, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() / values[2].getValue();
  }

}