package memory;

import java.util.ArrayList;
import java.util.List;

public class RegisterFile {

  private final int registerCount;
  private final List<Register> registers;

  public RegisterFile(int registerCount) {
    this.registerCount = registerCount;

    /* Initialise registers */
    registers = new ArrayList<Register>();
    for(int i = 0; i < registerCount; i++) {
      registers.add(new Register(i));
    }
  }

  public int getRegisterCount() {
    return registerCount;
  }

  public Register getRegister(int index) {
    if (index >= 0 && index < registerCount) {
      return registers.get(index);
    } else {
      throw new IllegalArgumentException("Cannot access register: index out of bounds");
    }
  }

}