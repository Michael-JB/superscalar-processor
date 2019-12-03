package instruction;

public class CompareInstruction extends Instruction {

  public CompareInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.CMP, dest, srcA, srcB);
  }

  @Override
  public int eval() {
    if (operands[1].getExecutionValue().get() < operands[2].getExecutionValue().get()) {
      return -1;
    } else if (operands[1].getExecutionValue().get() > operands[2].getExecutionValue().get()) {
      return 1;
    } else {
      return 0;
    }
  }

}