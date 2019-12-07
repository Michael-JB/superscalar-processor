package instruction;

import java.util.Optional;

public class NoOperationInstruction extends Instruction {

  public NoOperationInstruction() {
    super(Opcode.NOP);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(0);
  }

}