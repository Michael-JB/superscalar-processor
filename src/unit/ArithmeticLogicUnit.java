package unit;

public class ArithmeticLogicUnit extends Unit {

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