package unit;

import java.util.Optional;

import control.ArithmeticError;
import core.Processor;
import instruction.DecodedInstruction;

public class ArithmeticLogicUnit extends Unit {

  public ArithmeticLogicUnit(Processor processor, int reservationStationCapacity) {
    super(processor, reservationStationCapacity);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    /* Execute instruction */
    Optional<Integer> executionResult = instruction.evaluate();
    if (executionResult.isPresent()) {
      /* Update instruction result */
      instruction.setExecutionResult(executionResult.get());
    } else {
      instruction.raiseRuntimeError(new ArithmeticError("Cannot execute " + instruction.getInstruction().toString()));
    }
  }

  @Override
  public String toString() {
    return "Arithmetic Logic Unit";
  }
}