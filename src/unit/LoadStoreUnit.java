package unit;

import core.Processor;
import instruction.Instruction;
import instruction.Opcode;
import instruction.ValueOperand;

public class LoadStoreUnit extends Unit {

  private final int memorySize = 100;
  private final int[] memory = new int[memorySize];

  public LoadStoreUnit(Processor processor) {
    super(processor);
  }

  private boolean isInMemoryBounds(int address) {
    return address >= 0 && address < memorySize;
  }

  private int readFromMemory(int address) {
    if (isInMemoryBounds(address)) {
      return memory[address];
    } else {
      throw new ArrayIndexOutOfBoundsException("Memory address out of bounds: " + address);
    }
  }

  private void storeToMemory(int address, int value) {
    if (isInMemoryBounds(address)) {
      memory[address] = value;
    } else {
      throw new ArrayIndexOutOfBoundsException("Memory address out of bounds: " + address);
    }
  }

  @Override
  public void tick() {
    if (hasBufferedInstruction()) {
      /* Get instruction from queue */
      Instruction toExecute = getCurrentInstruction();
      Opcode opcode = toExecute.getOpcode();

      if (getDelayCounter() == 0) {
        /* Retrieve operand values from registers */
        ValueOperand[] inputValues = getValuesFromRegisters(toExecute);
        /* Execute instruction */
        int targetAddress = toExecute.execute(inputValues);

        if (opcode == Opcode.LA || opcode == Opcode.LAI) {
          /* Load  instructions */
          toExecute.setResult(readFromMemory(targetAddress));
        } else {
          /* Store instructions */
          storeToMemory(targetAddress, inputValues[0].getValue());
        }
      }

      incrementDelayCounter();

      if (getDelayCounter() >= opcode.getLatency()) {
        /* Instruction has now been completed */
        completeCurrentInstruction();
      }
    }
  }

}