package unit;

import core.Processor;
import instruction.Instruction;
import instruction.Opcode;
import instruction.ValueOperand;
import memory.Register;

public class BranchUnit extends Unit {

  public BranchUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void process(Instruction instruction) {
    /* Retrieve operand values from registers */
    ValueOperand[] inputValues = getValuesFromRegisters(instruction);
    Opcode opcode = instruction.getOpcode();

    /* Execute instruction */
    int executionResult = instruction.evaluate(inputValues);

    Register programCounterRegister = processor.getProgramCounter();

    if (opcode == Opcode.NOP) {
      /* Do nothing */
    } else if (opcode == Opcode.JMP || opcode == Opcode.JMPR) {
      /* Jump instruction, so update program counter absolutely */
      programCounterRegister.setValue(executionResult);
    } else {
      /* Branch instruction, so update program counter relatively */
      programCounterRegister.setValue(programCounterRegister.getValue() + executionResult);
    }
  }
}