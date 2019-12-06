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

  @Override
  public String toString() {
    return String.format("%-9s", instruction.getInstructionStatus().toString()) + " | " + instruction.toString() + " (Result: " +
      (instruction.getExecutionResult().isPresent() ? instruction.getExecutionResult().get() : "-") + ")";
  }
}