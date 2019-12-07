package instruction;

import java.util.Optional;

public class SubtractImmediateInstruction extends Instruction {

  public SubtractImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.SUBI, dest, src, val);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() - operands[2].getExecutionValue().get());
  }

}