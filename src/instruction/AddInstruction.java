package instruction;

public class AddInstruction extends Instruction {

  public AddInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.ADD, dest, srcA, srcB);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get();
  }

}