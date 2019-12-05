package instruction;

import java.util.Optional;

import core.Processor;
import memory.Register;
import memory.RegisterFlag;

public class RegisterOperand extends Operand {

  private Optional<Tag> blockingTag = Optional.empty();

  public RegisterOperand(int value) {
    super(value);
  }

  public void tryRetrieveValue(Processor processor) {
    Register register = processor.getRegisterFile().getRegister(getValue());
    if (register.getFlag().equals(RegisterFlag.VALID)) {
      setExecutionValue(register.getValue());
    } else if (register.getFlag().equals(RegisterFlag.INVALID)) {
      blockingTag = register.getReservingTag();
    } else if (register.getFlag().equals(RegisterFlag.READY)) {
      if (register.getReservingTag().isPresent()) {
        Optional<Integer> reorderBufferValue = processor.getReorderBuffer().getValueForTag(register.getReservingTag().get());
        if (reorderBufferValue.isPresent()) {
          setExecutionValue(reorderBufferValue.get());
        } else {
          throw new IllegalArgumentException("Ready flag raised, but reorder buffer value not present");
        }
      } else {
        throw new IllegalArgumentException("Ready flag raised, but reserving tag not present");
      }
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