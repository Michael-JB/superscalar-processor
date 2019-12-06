package unit;

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
    int targetAddress = instruction.evaluate();

    if (opcode == Opcode.LA || opcode == Opcode.LAI) {
      /* Load  instructions */
      instruction.setExecutionResult(processor.getMemory().readFromMemory(targetAddress));
    } else {
      /* Store instructions */
      instruction.setExecutionResult(targetAddress);
    }
  }

  @Override
  public String toString() {
    return "Load Store Unit";
  }
}