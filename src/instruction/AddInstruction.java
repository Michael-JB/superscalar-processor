package instruction;

import java.util.Optional;

public class AddInstruction extends Instruction {

  public AddInstruction(RegisterOperand dest, RegisterOperand srcA, RegisterOperand srcB) {
    super(Opcode.ADD, dest, srcA, srcB);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get());
  }

}