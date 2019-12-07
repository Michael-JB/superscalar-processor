package instruction;

import java.util.Optional;

public class BranchLessThanInstruction extends Instruction {

  public BranchLessThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BLT, srcA, srcB, off);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[0].getExecutionValue().get() < operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 1);
  }

}