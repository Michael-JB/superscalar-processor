package instruction;

public class LoadInstruction extends Instruction {

  public LoadInstruction(RegisterOperand dest, RegisterOperand base, ValueOperand off) {
    super(Opcode.LA, dest, base, off);
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[1].getValue() + values[2].getValue();
  }

}