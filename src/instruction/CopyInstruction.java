package instruction;

import java.util.Optional;

public class CopyInstruction extends Instruction {

  public CopyInstruction(RegisterOperand dest, RegisterOperand srcA) {
    super(Opcode.COPY, dest, srcA);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get());
  }

}