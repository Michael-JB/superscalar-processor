package control;

import instruction.FetchedInstruction;
import instruction.ValueOperand;

public abstract class DynamicBranchPredictor extends BranchPredictor {

  protected abstract DynamicBranchMetric createMetricForEntry();

  @Override
  protected int predictBranchInstruction(FetchedInstruction fetchedBranchInstruction, ValueOperand deltaOperand) {
    int nextLine = fetchedBranchInstruction.getLineNumber() + 1;
    int predictedLine = nextLine;
    boolean predictTake = true;

    if (getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber()).isPresent()) {
      BranchTargetAddressCacheEntry entry = getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber()).get();
      predictTake = entry.getDynamicBranchMetric().get().shouldTakeBranch();
      predictedLine = predictTake ? entry.getTargetLine() : nextLine;
      entry.setPredictedTaken(predictTake);
    } else {
      DynamicBranchMetric branchMetric = createMetricForEntry();
      int targetLine = fetchedBranchInstruction.getLineNumber() + deltaOperand.getValue();
      predictTake = branchMetric.shouldTakeBranch();
      predictedLine = predictTake ? targetLine : nextLine;
      BranchTargetAddressCacheEntry entry = new BranchTargetAddressCacheEntry(targetLine, branchMetric);
      entry.setPredictedTaken(predictTake);
      getBranchTargetAddressCache().addEntry(fetchedBranchInstruction.getLineNumber(), entry);
    }
    return predictedLine;
  }

}