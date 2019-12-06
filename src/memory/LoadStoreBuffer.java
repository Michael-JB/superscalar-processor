package memory;

import java.util.LinkedList;

import instruction.DecodedInstruction;
import instruction.Opcode;

public class LoadStoreBuffer {

  private final int capacity;
  private final LinkedList<DecodedInstruction> loadStoreBuffer = new LinkedList<DecodedInstruction>();

  public LoadStoreBuffer(int capacity) {
    this.capacity = capacity;
  }

  public boolean hasEntries() {
    return !loadStoreBuffer.isEmpty();
  }

  public boolean isFull() {
    return loadStoreBuffer.size() >= capacity;
  }

  public boolean pushToTail(DecodedInstruction instruction) {
    if (!isFull()) {
      loadStoreBuffer.addLast(instruction);
      return true;
    }
    return false;
  }

  public void retireInstruction(DecodedInstruction instruction) {
    if (loadStoreBuffer.contains(instruction)) {
      loadStoreBuffer.remove(instruction);
    }
  }

  public boolean previousStoreExistsForInstruction(DecodedInstruction instruction) {
    if (loadStoreBuffer.contains(instruction) && instruction.isReady()) {
      int targetAddress = instruction.evaluate();
      int index = loadStoreBuffer.indexOf(instruction);
      for (int i = 0; i < index; i++) {
        DecodedInstruction previous = loadStoreBuffer.get(i);
        Opcode previousOpcode = previous.getInstruction().getOpcode();
        if (previousOpcode.equals(Opcode.SA) || previousOpcode.equals(Opcode.SAI)) {
          if (previous.isReady()) {
            int previousAddress = previous.evaluate();
            if (previousAddress == targetAddress) {
              return true;
            }
          } else {
            return true;
          }
        }
      }
    } else {
      throw new IllegalArgumentException("Cannot check previous stores for unready/unscheduled instruction");
    }
    return false;
  }

  public void flush() {
    loadStoreBuffer.clear();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (!loadStoreBuffer.isEmpty()) {
      loadStoreBuffer.stream().forEachOrdered(i -> sb.append(i.toString() + System.lineSeparator()));
    } else {
      sb.append("Empty" + System.lineSeparator());
    }
    return sb.toString();
  }

}