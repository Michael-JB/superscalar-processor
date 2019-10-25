package unit;

import core.Processor;
import instruction.Instruction;
import instruction.ValueOperand;

public class ArithmeticLogicUnit extends Unit {

  public ArithmeticLogicUnit(Processor processor) {
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
  }
}