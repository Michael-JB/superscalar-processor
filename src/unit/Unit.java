package unit;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import instruction.DecodedInstruction;

public abstract class Unit {

  protected Queue<DecodedInstruction> instructionBuffer = new LinkedList<DecodedInstruction>();
  protected Optional<Integer> result = Optional.empty();

  public void bufferInstruction(DecodedInstruction instruction) {
    instructionBuffer.offer(instruction);
  }

  public Optional<Integer> getResult() {
    return result;
  }

  protected boolean hasBufferedInstruction() {
    return !instructionBuffer.isEmpty();
  }

  protected DecodedInstruction getCurrentInstruction() {
    return instructionBuffer.peek();
  }

  protected DecodedInstruction completeCurrentInstruction() {
    return instructionBuffer.poll();
  }

  protected void setResult(Integer newResult) {
    result = Optional.of(newResult);
  }

  protected void clearResult() {
    result = Optional.empty();
  }

  public abstract void tick();

}