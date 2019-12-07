package control;

import java.util.Arrays;
import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.ValueOperand;

public class StaticBranchPredictor {

  private final BranchTargetAddressCache branchTargetAddressCache;

  public StaticBranchPredictor(BranchTargetAddressCache branchTargetAddressCache) {
    this.branchTargetAddressCache = branchTargetAddressCache;
  }

  public int predict(FetchedInstruction fetchedInstruction) {
    Opcode opcode = fetchedInstruction.getInstruction().getOpcode();
    int targetLine = fetchedInstruction.getLineNumber() + 1;
    int predictedLine = targetLine;
    boolean predictTake = false;
    if (opcode.getCategory().equals(OpcodeCategory.CONTROL)) {
      Optional<ValueOperand> valueOperand = Arrays.stream(fetchedInstruction.getInstruction().getOperands())
        .filter(o -> o instanceof ValueOperand)
        .map(o -> (ValueOperand) o)
        .findAny();

      if (valueOperand.isPresent()) {
        if (opcode == Opcode.JMP) {
          targetLine = valueOperand.get().getValue();
          predictedLine = targetLine;
          predictTake = true;
        } else {
          targetLine = fetchedInstruction.getLineNumber() + valueOperand.get().getValue();
          if (valueOperand.get().getValue() < 0) {
            predictedLine = targetLine;
            predictTake = true;
          }
        }
      }
      BranchTargetAddressCacheEntry entry = new BranchTargetAddressCacheEntry(targetLine);
      entry.setPredictedTaken(predictTake);
      branchTargetAddressCache.addEntry(fetchedInstruction.getLineNumber(), entry);
    }
    return predictedLine;
  }

  @Override
  public String toString() {
    return "hi";
  }

}