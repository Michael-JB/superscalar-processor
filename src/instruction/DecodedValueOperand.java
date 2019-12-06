package instruction;

import core.Processor;

public class DecodedValueOperand extends DecodedOperand {

  public DecodedValueOperand(ValueOperand encodedOperand) {
    super(encodedOperand, true);
    setExecutionValue(encodedOperand.getValue());
  }


  @Override
  public void tryRetrieveValue(Processor processor) {}

}