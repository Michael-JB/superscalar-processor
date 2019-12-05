package instruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Instruction {

  protected final Opcode opcode;
  protected final Operand[] operands;
  protected Tag tag;
  protected Optional<Integer> executionResult = Optional.empty();
  protected InstructionStatus instructionStatus = InstructionStatus.PENDING;

  public Instruction(Opcode opcode, Operand... operands) {
    this.opcode = opcode;
    this.operands = operands;

    Optional<RegisterOperand> destinationRegister = getDestinationRegister();
    destinationRegister.ifPresent(r -> r.setRequiresExecutionValue(false));

    if (operands.length != opcode.getOperandCount()) {
      throw new IllegalArgumentException(
        "Invalid operand count. " + operands.length + " provided, " + opcode.getOperandCount() + " required.");
    }
  }

  public void setInstructionStatus(InstructionStatus newStatus) {
    this.instructionStatus = newStatus;
  }

  public InstructionStatus getInstructionStatus() {
    return instructionStatus;
  }

  public Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public Opcode getOpcode() {
    return opcode;
  }

  public Operand[] getOperands() {
    return operands;
  }

  public Optional<Integer> getExecutionResult() {
    return executionResult;
  }

  public void setExecutionResult(int result) {
    this.executionResult = Optional.of(result);
  }

  public boolean isReady() {
    return Arrays.stream(operands).allMatch(o -> o.isReady());
  }

  public Optional<RegisterOperand> getDestinationRegister() {
    if (operands.length > 0 && !opcode.getCategory().equals(OpcodeCategory.CONTROL) && !opcode.equals(Opcode.SA) && !opcode.equals(Opcode.SAI)) {
      Operand firstOperand = operands[0];
      if (firstOperand instanceof RegisterOperand) {
        return Optional.of((RegisterOperand) firstOperand);
      }
    }
    return Optional.empty();
  }

  public List<RegisterOperand> getSourceOperands() {
    List<RegisterOperand> sourceOperands = new ArrayList<RegisterOperand>();
    int sourceStart = 0;
    if (operands.length > 0) {
      if (!opcode.getCategory().equals(OpcodeCategory.CONTROL) && !opcode.equals(Opcode.SA) && !opcode.equals(Opcode.SAI)) {
        sourceStart = 1;
      } else {
        sourceStart = 0;
      }
      for (int i = sourceStart; i < operands.length; i++) {
        if (operands[i] instanceof RegisterOperand) {
          sourceOperands.add((RegisterOperand) operands[i]);
        }
      }
    }
    return sourceOperands;
  }

  public int evaluate() {
    if (isReady()) {
      return eval();
    } else {
      throw new IllegalArgumentException("Cannot eval :(");
    }
  }

  protected abstract int eval();

  @Override
  public String toString() {
    return opcode.getInstructionPrefix() + " " + Arrays.stream(operands)
      .map(Operand::toString)
      .collect(Collectors.joining(" ")) + (tag != null ? " (" + tag + ")" : "");
  }

}