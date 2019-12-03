package unit;

import core.Processor;
import instruction.Instruction;
import instruction.Opcode;
import instruction.ValueOperand;

public class LoadStoreUnit extends Unit {

  private final int memoryCapacity;
  private final int[] memory;

  public LoadStoreUnit(Processor processor, int memoryCapacity) {
    super(processor);
    this.memoryCapacity = memoryCapacity;
    this.memory = new int[memoryCapacity];
  }

  private boolean isInMemoryBounds(int address) {
    return address >= 0 && address < memoryCapacity;
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
  public void process(Instruction instruction) {
    /* Retrieve operand values from registers */
    // ValueOperand[] inputValues = getValuesFromRegisters(instruction);
    Opcode opcode = instruction.getOpcode();

    /* Execute instruction */
    int targetAddress = instruction.evaluate();

    if (opcode == Opcode.LA || opcode == Opcode.LAI) {
      /* Load  instructions */
      instruction.setWritebackResult(readFromMemory(targetAddress));
    } else {
      /* Store instructions */
      storeToMemory(targetAddress, instruction.getOperands()[0].getExecutionValue().get());
    }
  }

  @Override
  public String toString() {
    return "Load Store Unit";
  }
}