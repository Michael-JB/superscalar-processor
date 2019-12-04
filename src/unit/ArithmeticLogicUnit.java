package unit;

import core.Processor;
import instruction.Instruction;

public class ArithmeticLogicUnit extends Unit {

  public ArithmeticLogicUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void process(Instruction instruction) {
    /* Execute instruction */
    int executionResult = instruction.evaluate();
    /* Update instruction result */
    instruction.setWritebackResult(executionResult);
  }

  @Override
  public String toString() {
    return "Arithmetic Logic Unit";
  }
}