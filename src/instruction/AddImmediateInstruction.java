package instruction;

public class AddImmediateInstruction extends Instruction {

  public AddImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.ADDI, dest, src, val);
  }

  @Override
  public int eval(ValueOperand... values) {
    /* TODO */
    return 0;
  }

}