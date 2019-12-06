package memory;

import instruction.DecodedInstruction;
import instruction.InstructionStatus;

public class ReorderBufferEntry {

  private final DecodedInstruction instruction;

  public ReorderBufferEntry(DecodedInstruction instruction) {
    this.instruction = instruction;
  }

  public DecodedInstruction getDecodedInstruction() {
    return instruction;
  }

  public boolean isComplete() {
    return instruction.getInstructionStatus().equals(InstructionStatus.EXECUTED);
  }

  public String getStatus() {
    return "ROB Entry for: " + instruction;
  }

  @Override
  public String toString() {
    return instruction.toString() + " (Result: " +
      (instruction.getExecutionResult().isPresent() ? instruction.getExecutionResult().get() : "-") + ")";
  }
}