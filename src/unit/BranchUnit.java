package unit;

import java.util.Optional;

import control.BranchError;
import core.Processor;
import instruction.DecodedInstruction;
import instruction.Opcode;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor, int reservationStationCapacity) {
    super(processor, reservationStationCapacity);
  }

  @Override
  public void process(DecodedInstruction instruction) {
    Opcode opcode = instruction.getInstruction().getOpcode();

    /* Execute instruction */
    Optional<Integer> executionResult = instruction.evaluate();
    if (executionResult.isPresent()) {
      if (opcode == Opcode.NOP) {
        /* Do nothing */
      } else if (opcode == Opcode.JMP) {
        if (executionResult.get() == instruction.getLineNumber()) {
          instruction.raiseRuntimeError(new BranchError("Cannot jump to same line"));
        } else {
          /* Jump instruction, so update program counter absolutely */
          instruction.setExecutionResult(executionResult.get());
          instruction.setBranchTarget(executionResult.get());
          instruction.setBranchTaken(true);
        }
      } else {
        if (executionResult.get() == 0) {
          instruction.raiseRuntimeError(new BranchError("Cannot branch to same line"));
        } else {
          /* Branch instruction, so update program counter relatively */
          instruction.setExecutionResult(executionResult.get());
          instruction.setBranchTarget(instruction.getLineNumber() + executionResult.get());
          instruction.setBranchTaken(executionResult.get() != 1);
        }
      }
    } else {
      instruction.raiseRuntimeError(new BranchError("Cannot execute " + instruction.getInstruction().toString()));
    }
  }

  @Override
  public String toString() {
    return "Branch Unit";
  }
}