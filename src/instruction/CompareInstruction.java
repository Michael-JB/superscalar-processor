package instruction;

import java.util.Optional;

public class CompareInstruction extends Instruction {

  public CompareInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.CMP, dest, srcA, srcB);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    if (operands[1].getExecutionValue().get() < operands[2].getExecutionValue().get()) {
      return Optional.of(-1);
    } else if (operands[1].getExecutionValue().get() > operands[2].getExecutionValue().get()) {
      return Optional.of(1);
    } else {
      return Optional.of(0);
    }
  }

}