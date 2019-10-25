package unit;

import core.Processor;
import instruction.Instruction;
import instruction.ValueOperand;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void tick() {
    if (hasBufferedInstruction()) {
      /* Get instruction from queue */
      Instruction toExecute = getCurrentInstruction();

      if (getDelayCounter() == 0) {
        /* Retrieve operand values from registers */
        ValueOperand[] inputValues = getValuesFromRegisters(toExecute);
        /* Execute instruction */
        int executionResult = toExecute.execute(inputValues);
        /* Update instruction result */
        toExecute.setResult(executionResult);

        processor.getProgramCounter().setValue(executionResult);
      }

      incrementDelayCounter();

      if (getDelayCounter() >= toExecute.getOpcode().getLatency()) {
        /* Instruction has now been completed */
        completeCurrentInstruction();
      }
    }
  }
}