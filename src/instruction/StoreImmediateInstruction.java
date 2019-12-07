package instruction;

import java.util.Optional;

public class StoreImmediateInstruction extends Instruction {

  public StoreImmediateInstruction(RegisterOperand val, ValueOperand addr) {
    super(Opcode.SAI, val, addr);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get());
  }

}