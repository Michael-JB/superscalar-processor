package unit;

import core.Processor;
import instruction.Instruction;
import instruction.ValueOperand;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void process(Instruction instruction) {
    /* Retrieve operand values from registers */
    ValueOperand[] inputValues = getValuesFromRegisters(instruction);
    /* Execute instruction */
    int executionResult = instruction.execute(inputValues);
    /* Update instruction result */
    instruction.setResult(executionResult);

    processor.getProgramCounter().setValue(executionResult);
  }
}