package memory;

import java.util.ArrayList;
import java.util.List;

public class RegisterFile {

  private final int capacity;
  private final List<Register> registers;

  public RegisterFile(int capacity) {
    this.capacity = capacity;

    /* Initialise registers */
    registers = new ArrayList<Register>();
    for(int i = 0; i < capacity; i++) {
      registers.add(new Register(i));
    }
  }

  public int getRegisterFileCapacity() {
    return capacity;
  }

  private boolean isRegisterInFile(int index) {
    return index >= 0 && index < capacity;
  }

  public Register getRegister(int index) {
    if (isRegisterInFile(index)) {
      return registers.get(index);
    } else {
      throw new IllegalArgumentException("Cannot access register: index out of bounds");
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    registers.stream().forEachOrdered(r -> sb.append(r.toString() + System.lineSeparator()));
    return sb.toString();
  }

}