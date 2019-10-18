package unit;

import core.Processor;

public class ArithmeticLogicUnit extends Unit {

  public ArithmeticLogicUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void tick() {
    if (hasBufferedInstruction()) {
      /* Execute instruction */
      int executionResult = getCurrentInstruction().execute();

      /* Update ALU result */
      setResult(executionResult);

      /* Instruction has now been completed */
      completeCurrentInstruction();
    }
  }

}