package unit;

import core.Processor;
import instruction.DecodedInstruction;

public class ArithmeticLogicUnit extends Unit {

  public ArithmeticLogicUnit(Processor processor, int reservationStationCapacity) {
    super(processor, reservationStationCapacity);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    /* Execute instruction */
    int executionResult = instruction.evaluate();
    /* Update instruction result */
    instruction.setExecutionResult(executionResult);
  }

  @Override
  public String toString() {
    return "Arithmetic Logic Unit";
  }
}