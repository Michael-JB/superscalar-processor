package unit;

import core.Processor;
import instruction.DecodedInstruction;
import instruction.Opcode;

public class LoadStoreUnit extends Unit {

  public LoadStoreUnit(Processor processor) {
    super(processor);
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
      // storeToMemory(targetAddress, instruction.getOperands()[0].getExecutionValue().get());
    }
  }

  @Override
  public String toString() {
    return "Load Store Unit";
  }
}