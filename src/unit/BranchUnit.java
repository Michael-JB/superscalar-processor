package unit;

import core.Processor;
import instruction.DecodedInstruction;
import instruction.Opcode;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    Opcode opcode = instruction.getInstruction().getOpcode();

    /* Execute instruction */
    int executionResult = instruction.evaluate();

    // Register programCounterRegister = processor.getProgramCounter();

    instruction.setExecutionResult(executionResult);

    if (opcode == Opcode.NOP) {
      /* Do nothing */
    } else if (opcode == Opcode.JMP || opcode == Opcode.JMPR) {
      /* Jump instruction, so update program counter absolutely */
      // programCounterRegister.setValue(executionResult);
    } else {
      /* Branch instruction, so update program counter relatively */
      //programCounterRegister.setValue(programCounterRegister.getValue() + executionResult);
    }
  }

  @Override
  public String toString() {
    return "Branch Unit";
  }
}