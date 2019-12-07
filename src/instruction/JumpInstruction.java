package instruction;

import java.util.Optional;

public class JumpInstruction extends Instruction {

  public JumpInstruction(ValueOperand dest) {
    super(Opcode.JMP, dest);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[0].getExecutionValue().get());
  }

}