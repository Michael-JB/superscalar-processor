package unit;

import java.util.Optional;

import control.LoadStoreError;
import core.Processor;
import instruction.DecodedInstruction;
import instruction.Opcode;

public class LoadStoreUnit extends Unit {

  public LoadStoreUnit(Processor processor, int reservationStationCapacity) {
    super(processor, reservationStationCapacity);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    Opcode opcode = instruction.getInstruction().getOpcode();

    /* Execute instruction */
    Optional<Integer> targetAddress = instruction.evaluate();
    if (targetAddress.isPresent() && processor.getMemory().isInMemoryBounds(targetAddress.get())) {
      /* Update instruction result */
      if (opcode == Opcode.LA || opcode == Opcode.LAI) {
        /* Load  instructions */
        instruction.setExecutionResult(processor.getMemory().readFromMemory(targetAddress.get()));
      } else {
        /* Store instructions */
        instruction.setExecutionResult(targetAddress.get());
      }
    } else {
      instruction.raiseRuntimeError(new LoadStoreError("Cannot execute " + instruction.getInstruction().toString()));
    }
  }

  @Override
  public String toString() {
    return "Load Store Unit";
  }
}