package instruction;

import java.util.Optional;

public class AddImmediateInstruction extends Instruction {

  public AddImmediateInstruction(RegisterOperand dest, RegisterOperand src, ValueOperand val) {
    super(Opcode.ADDI, dest, src, val);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get());
  }

}