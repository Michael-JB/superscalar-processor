package instruction;

import java.util.Optional;

public class BranchGreaterThanInstruction extends Instruction {

  public BranchGreaterThanInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BGT, srcA, srcB, off);
  }

  @Override
  public Optional<Integer> evaluate(DecodedOperand... operands) {
    return Optional.of(operands[0].getExecutionValue().get() > operands[1].getExecutionValue().get() ? operands[2].getExecutionValue().get() : 1);
  }

}