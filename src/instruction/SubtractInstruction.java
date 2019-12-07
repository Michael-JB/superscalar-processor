package instruction;

import java.util.Optional;

public class SubtractInstruction extends Instruction {

  public SubtractInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.SUB, dest, srcA, srcB);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() - operands[2].getExecutionValue().get());
  }

}