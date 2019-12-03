package instruction;

import java.util.Optional;

import memory.Register;
import memory.RegisterFile;
import memory.RegisterFlag;

public class RegisterOperand extends Operand {

  private Optional<Tag> blockingTag = Optional.empty();

  public RegisterOperand(int value) {
    super(value);
  }

  public void tryRetrieveValue(RegisterFile registerFile) {
    Register register = registerFile.getRegister(getValue());
    if (register.getFlag().equals(RegisterFlag.VALID)) {
      setExecutionValue(register.getValue());
    } else if (register.getFlag().equals(RegisterFlag.INVALID)) {
      blockingTag = register.getReservingTag();
    }
  }

  public Optional<Tag> getBlockingTag() {
    return blockingTag;
  }

  @Override
  public String toString() {
    return "r" + value;
  }

}