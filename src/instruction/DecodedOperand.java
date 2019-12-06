package instruction;

import java.util.Optional;

import core.Processor;

public abstract class DecodedOperand {

  protected final boolean requiresExecutionValue;
  protected final Operand encodedOperand;

  protected Optional<Integer> executionValue = Optional.empty();

  public DecodedOperand(Operand encodedOperand, boolean requiresExecutionValue) {
    this.encodedOperand = encodedOperand;
    this.requiresExecutionValue = requiresExecutionValue;
  }

  public Operand getEncodedOperand() {
    return encodedOperand;
  }

  public void setExecutionValue(int value) {
    this.executionValue = Optional.of(value);
  }

  public Optional<Integer> getExecutionValue() {
    return executionValue;
  }

  public boolean requiresExecutionValue() {
    return requiresExecutionValue;
  }

  public boolean isReady() {
    return !requiresExecutionValue || executionValue.isPresent();
  }

  public abstract void tryRetrieveValue(Processor processor);

}