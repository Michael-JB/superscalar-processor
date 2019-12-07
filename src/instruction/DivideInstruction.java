package instruction;

import java.util.Optional;

public class DivideInstruction extends Instruction {

  public DivideInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.DIV, dest, srcA, srcB);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    if (operands[2].getExecutionValue().get() != 0) {
      return Optional.of(operands[1].getExecutionValue().get() / operands[2].getExecutionValue().get());
    }
    return Optional.empty();
  }

}