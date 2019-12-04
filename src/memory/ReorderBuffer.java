package memory;

import java.util.ArrayDeque;
import java.util.Deque;

import core.Processor;

public class ReorderBuffer {

  int REORDER_BUFFER_SIZE = 16;

  Deque<ReorderBufferEntry> reorderBuffer = new ArrayDeque<ReorderBufferEntry>();

  private final Processor processor;

  public ReorderBuffer(Processor processor) {
    this.processor = processor;
  }

  public boolean hasEntries() {
    return !reorderBuffer.isEmpty();
  }

  public boolean isFull() {
    return reorderBuffer.size() >= REORDER_BUFFER_SIZE;
  }

  public boolean pushToTail(ReorderBufferEntry entry) {
    if (reorderBuffer.size() < REORDER_BUFFER_SIZE) {
      reorderBuffer.addLast(entry);
      return true;
    }
    return false;
  }

  public String getStatus() {
    return reorderBuffer.toString();
  }

}