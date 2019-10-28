package instruction;

public class AddImmediateInstruction extends Instruction {

  public AddImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.ADDI, dest, src, val);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue() + values[2].getValue();
  }

}