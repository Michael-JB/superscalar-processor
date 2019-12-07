package control;

import java.util.Arrays;
import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.ValueOperand;

public class DynamicBranchPredictor {

  private final BranchTargetAddressCache branchTargetAddressCache;
  private final int saturatingCounterBits;

  public DynamicBranchPredictor(BranchTargetAddressCache branchTargetAddressCache, int saturatingCounterBits) {
    this.branchTargetAddressCache = branchTargetAddressCache;
    this.saturatingCounterBits = saturatingCounterBits;
  }

  public int predict(FetchedInstruction fetchedInstruction) {
    Opcode opcode = fetchedInstruction.getInstruction().getOpcode();
    int nextLine = fetchedInstruction.getLineNumber() + 1;
    int targetLine = nextLine;
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
          if (branchTargetAddressCache.getEntryForLine(fetchedInstruction.getLineNumber()).isPresent()) {
            BranchTargetAddressCacheEntry entry = branchTargetAddressCache.getEntryForLine(fetchedInstruction.getLineNumber()).get();
            targetLine = entry.getTargetLine();
            predictTake = entry.getSaturatingCounter().get().shouldTakeBranch();
            predictedLine = predictTake ? targetLine : nextLine;
            entry.setPredictedTaken(predictTake);
          } else {
            SaturatingCounter saturatingCounter = new SaturatingCounter(saturatingCounterBits);
            targetLine = fetchedInstruction.getLineNumber() + valueOperand.get().getValue();
            predictTake = saturatingCounter.shouldTakeBranch();
            predictedLine = predictTake ? targetLine : nextLine;
            BranchTargetAddressCacheEntry entry = new BranchTargetAddressCacheEntry(targetLine, saturatingCounter);
            entry.setPredictedTaken(predictTake);
            branchTargetAddressCache.addEntry(fetchedInstruction.getLineNumber(), entry);
          }
        }
      }
    }
    return predictedLine;
  }

  @Override
  public String toString() {
    return "hi";
  }

}