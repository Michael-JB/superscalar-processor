package unit;

import java.util.Arrays;

import core.Processor;
import instruction.DecodedInstruction;
import instruction.DecodedValueOperand;
import instruction.Opcode;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor, int reservationStationCapacity) {
    super(processor, reservationStationCapacity);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    Opcode opcode = instruction.getInstruction().getOpcode();

    /* Execute instruction */
    int executionResult = instruction.evaluate();
    /* Update instruction result */
    instruction.setExecutionResult(executionResult);

    if (opcode == Opcode.NOP) {
      /* Do nothing */
    } else if (opcode == Opcode.JMP) {
      /* Jump instruction, so update program counter absolutely */
      instruction.setBranchTarget(executionResult);
      instruction.setBranchTaken(true);
    } else {
      /* Branch instruction, so update program counter relatively */
      instruction.setBranchTarget(instruction.getLineNumber() + executionResult);
      instruction.setBranchTaken(executionResult != 0);
    }
  }

  @Override
  public String toString() {
    return "Branch Unit";
  }
}