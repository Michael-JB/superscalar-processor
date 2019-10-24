package unit;

import core.Processor;
import instruction.Instruction;
import instruction.Opcode;
import instruction.ValueOperand;

public class LoadStoreUnit extends Unit {

  private final int[] memory = new int[100];

  public LoadStoreUnit(Processor processor) {
    super(processor);
  }

  @Override
  public void tick() {
    if (hasBufferedInstruction()) {

      /* Get instruction from queue */
      Instruction toExecute = getCurrentInstruction();

      /* Retrieve operand values from registers */
      ValueOperand[] inputValues = getValuesFromRegisters(toExecute);

      /* Execute instruction */
      int targetAddress = toExecute.execute(inputValues);

      Opcode opcode = toExecute.getOpcode();

      if (opcode == Opcode.LA || opcode == Opcode.LAI) {
        /* Load  instructions */
        setResult(memory[targetAddress]);
      } else {
        /* Store instructions */
        memory[targetAddress] = inputValues[0].getValue();
        clearResult();
      }

      /* Instruction has now been completed */
      completeCurrentInstruction();
    }
  }

}