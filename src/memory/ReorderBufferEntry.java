package memory;

import java.util.Optional;

import instruction.Instruction;
import instruction.InstructionStatus;

public class ReorderBufferEntry {

  private final Instruction instruction;
  // private Optional<Integer> executionResult = Optional.empty();
  // private boolean isComplete = false;

  public ReorderBufferEntry(Instruction instruction) {
    this.instruction = instruction;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  // public void setExecutionResult(int result) {
  //   this.executionResult = Optional.of(result);
  // }

  public Optional<Integer> getExecutionResult() {
    return instruction.getExecutionResult();
  }

  public boolean isComplete() {
    return instruction.getInstructionStatus().equals(InstructionStatus.EXECUTED); // TODO: instructions without result
  }

  public String getStatus() {
    return "ROB Entry for: " + instruction;
  }

  @Override
  public String toString() {
    return instruction.toString() + " | " + (getExecutionResult().isPresent() ? "Result: " + getExecutionResult().get() : "No result");
  }
}