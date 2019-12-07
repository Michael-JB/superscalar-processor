package memory;

public class Memory {

  private final int DEFAULT_STRING_ENTRIES = 5;

  private final int memoryCapacity;
  private final int[] memory;

  public Memory(int memoryCapacity) {
    this.memoryCapacity = memoryCapacity;
    this.memory = new int[memoryCapacity];
  }

  public boolean isInMemoryBounds(int address) {
    return address >= 0 && address < memoryCapacity;
  }

  public int readFromMemory(int address) {
    if (isInMemoryBounds(address)) {
      return memory[address];
    } else {
      throw new ArrayIndexOutOfBoundsException("Memory address out of bounds: " + address);
    }
  }

  public void storeToMemory(int address, int value) {
    if (isInMemoryBounds(address)) {
      memory[address] = value;
    } else {
      throw new ArrayIndexOutOfBoundsException("Memory address out of bounds: " + address);
    }
  }

  public String toString(int entriesToShow) {
    int entryCount = Math.min(entriesToShow, memoryCapacity);
    StringBuilder sb = new StringBuilder();
    sb.append("(Showing " + entryCount + " entries of " + memoryCapacity + ")" + System.lineSeparator());
    for (int i = 0; i < entryCount; i++) {
      sb.append("[" + String.format("%-" + String.valueOf(entriesToShow - 1).length() + "d", i) + "]: " + memory[i] + System.lineSeparator());
    }
    if (entriesToShow < memoryCapacity) {
      sb.append("..." + System.lineSeparator());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return toString(DEFAULT_STRING_ENTRIES);
  }
}