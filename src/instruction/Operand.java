package instruction;

import java.util.Optional;

public abstract class Operand {

  protected final int value;
  protected Optional<Integer> executionValue = Optional.empty();
  protected boolean requiresExecutionValue = true;

  public Operand(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void setRequiresExecutionValue(boolean requiresExecutionValue) {
    this.requiresExecutionValue = requiresExecutionValue;
  }

  public void setExecutionValue(int value) {
    this.executionValue = Optional.of(value);
  }

  public Optional<Integer> getExecutionValue() {
    return executionValue;
  }

  public boolean isReady() {
    return !requiresExecutionValue || executionValue.isPresent();
  }

}