package memory;

import java.util.Optional;

import instruction.Instruction;

public class ReorderBufferEntry {

  private final Instruction instruction;
  private Optional<Integer> executionResult = Optional.empty();

  public ReorderBufferEntry(Instruction instruction) {
    this.instruction = instruction;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public void setExecutionResult(int result) {
    this.executionResult = Optional.of(result);
  }

  public Optional<Integer> getExecutionResult() {
    return executionResult;
  }

  public String getStatus() {
    return "ROB Entry for: " + instruction;
  }

  @Override
  public String toString() {
    return instruction.toString() + " | " + (executionResult.isPresent() ? "Result: " + executionResult.get() : "No result");
  }
}