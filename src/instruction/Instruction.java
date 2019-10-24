package instruction;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Instruction {

  protected final Opcode opcode;
  protected final Operand[] operands;
  protected Optional<Integer> result = Optional.empty();

  public Instruction(Opcode opcode, Operand... operands) {
    this.opcode = opcode;
    this.operands = operands;

    if (operands.length != opcode.getOperandCount()) {
      throw new IllegalArgumentException(
        "Invalid operand count. " + operands.length + " provided, " + opcode.getOperandCount() + " required.");
    }
  }

  public Opcode getOpcode() {
    return opcode;
  }

  public Operand[] getOperands() {
    return operands;
  }

  public Optional<Integer> getResult() {
    return result;
  }

  public void setResult(int result) {
    this.result = Optional.of(result);
  }

  public boolean canExecute(ValueOperand... values) {
    return opcode.getOperandCount() == values.length;
  }

  public int execute(ValueOperand... values) {
    if (canExecute(values)) {
      return perform(values);
    } else {
      throw new IllegalArgumentException(
        "Invalid value operand count. " + values.length + " provided, " + opcode.getOperandCount() + " required.");
    }
  }

  protected abstract int perform(ValueOperand... values);

  @Override
  public String toString() {
    return opcode.getInstructionPrefix() + " " + Arrays.stream(operands)
      .map(Operand::toString)
      .collect(Collectors.joining(" "));
  }

}