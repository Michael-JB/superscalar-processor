package instruction;

import java.util.Optional;

public class LoadImmediateInstruction extends Instruction {

  public LoadImmediateInstruction(RegisterOperand dest, ValueOperand addr) {
    super(Opcode.LAI, dest, addr);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[1].getExecutionValue().get());
  }

}