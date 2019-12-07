package instruction;

import java.util.Optional;

public class LoadInstruction extends Instruction {

  public LoadInstruction(RegisterOperand dest, RegisterOperand base, ValueOperand off) {
    super(Opcode.LA, dest, base, off);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get());
  }

}