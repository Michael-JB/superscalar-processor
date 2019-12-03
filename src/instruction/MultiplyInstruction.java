package instruction;

public class MultiplyInstruction extends Instruction {

  public MultiplyInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.MUL, dest, srcA, srcB);
  }

  @Override
  public int eval() {
    return operands[1].getExecutionValue().get() * operands[2].getExecutionValue().get();
  }

}