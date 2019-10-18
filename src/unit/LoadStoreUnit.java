package unit;

import core.Processor;
import instruction.Opcode;

public class LoadStoreUnit extends Unit {

  private final int[] memory = new int[100];

  public LoadStoreUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void tick() {
    if (hasBufferedInstruction()) {
      /* Execute instruction */
      int targetAddress = getCurrentInstruction().execute();

      Opcode opcode = getCurrentInstruction().getEncodedInstruction().getOpcode();

      if (opcode == Opcode.LA || opcode == Opcode.LAI) {
        /* Load  instructions */
        setResult(memory[targetAddress]);
      } else {
        /* Store instructions */
        memory[targetAddress] = getCurrentInstruction().getDecodedOperands()[0].getValue();
      }

      /* Instruction has now been completed */
      completeCurrentInstruction();
    }
  }

}