package instruction;

import java.util.Optional;

public class StoreInstruction extends Instruction {

  public StoreInstruction(RegisterOperand val, RegisterOperand base, ValueOperand off) {
    super(Opcode.SA, val, base, off);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get() + operands[2].getExecutionValue().get());
  }

}