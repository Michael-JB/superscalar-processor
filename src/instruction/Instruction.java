package instruction;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Instruction {

  protected final Opcode opcode;
  protected final Operand[] operands;

  public Instruction(Opcode opcode, Operand... operands) {
    this.opcode = opcode;
    this.operands = operands;
  }

  public Opcode getOpcode() {
    return opcode;
  }

  public Operand[] getOperands() {
    return operands;
  }

  public abstract void execute();

  @Override
  public String toString() {
    return opcode.getInstructionPrefix() + " " + Arrays.stream(operands)
      .map(Operand::toString)
      .collect(Collectors.joining(" "));
  }

}