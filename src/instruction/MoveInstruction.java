package instruction;

import java.util.Optional;

public class MoveInstruction extends Instruction {

  public MoveInstruction(RegisterOperand dest, ValueOperand val) {
    super(Opcode.MOVE, dest, val);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get());
  }

}