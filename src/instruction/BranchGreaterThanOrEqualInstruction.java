package instruction;

import java.util.Optional;

public class BranchGreaterThanOrEqualInstruction extends Instruction {

  public BranchGreaterThanOrEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGE, srcA, srcB, off);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[0].getExecutionValue().get() >= operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 1);
  }

}