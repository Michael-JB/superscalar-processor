package instruction;

public class AddInstruction extends Instruction {

  public AddInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.ADD, dest, srcA, srcB);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() + values[2].getValue();
  }

}