package memory;

import java.util.LinkedList;
import java.util.Optional;

import instruction.DecodedInstruction;
import instruction.Opcode;
import instruction.OpcodeCategory;

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

  public Optional<Integer> loadStoreForward(DecodedInstruction loadInstruction) {
    if (loadStoreBuffer.contains(loadInstruction) && loadInstruction.isReady()) {
      if (loadInstruction.evaluate().isPresent()) {
        int targetAddress = loadInstruction.evaluate().get();
        int index = loadStoreBuffer.indexOf(loadInstruction);
        for (int i = index - 1; i >= 0; i--) {
          DecodedInstruction previous = loadStoreBuffer.get(i);
          Opcode previousOpcode = previous.getInstruction().getOpcode();
          if (previousOpcode.equals(Opcode.SA) || previousOpcode.equals(Opcode.SAI)) {
            if (previous.isReady()) {
              Optional<Integer> previousAddress = previous.evaluate();
              if (previousAddress.isPresent() && previousAddress.get() == targetAddress) {
                return Optional.of(previous.getOperands()[0].getExecutionValue().get());
              }
            } else {
              return Optional.empty();
            }
          }
        }
      }
    } else {
      throw new IllegalArgumentException("Cannot forward previous stores for unready/unscheduled instruction");
    }
    return Optional.empty();
  }

  public boolean previousMemoryAccessExistsForInstruction(DecodedInstruction instruction, boolean storeOnly) {
    if (loadStoreBuffer.contains(instruction) && instruction.isReady()) {
      if (instruction.evaluate().isPresent()) {
        int targetAddress = instruction.evaluate().get();
        int index = loadStoreBuffer.indexOf(instruction);
        for (int i = 0; i < index; i++) {
          DecodedInstruction previous = loadStoreBuffer.get(i);
          Opcode previousOpcode = previous.getInstruction().getOpcode();
          if ((!storeOnly && previousOpcode.getCategory().equals(OpcodeCategory.MEMORY))
            || (storeOnly && (previousOpcode.equals(Opcode.SA) || previousOpcode.equals(Opcode.SAI)))) {
            if (previous.isReady()) {
              Optional<Integer> previousAddress = previous.evaluate();
              if (previousAddress.isPresent() && previousAddress.get() == targetAddress) {
                return true;
              }
            } else {
              return true;
            }
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
    if (loadStoreBuffer.isEmpty()) {
      sb.append("Empty" + System.lineSeparator());
    } else {
      for (int i = 0; i < loadStoreBuffer.size(); i++) {
        String header = "      |  ";
        if (i == 0) {
          header = "HEAD --> ";
        } else if (i == loadStoreBuffer.size() - 1) {
          header = "TAIL --> ";
        }
        sb.append(header + loadStoreBuffer.get(i).toString() + System.lineSeparator());
      }
    }
    return sb.toString();
  }

}