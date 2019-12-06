package instruction;

public class LoadInstruction extends Instruction {

  public LoadInstruction(RegisterOperand dest, RegisterOperand base, ValueOperand off) {
    super(Opcode.LA, dest, base, off);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get();
  }

}