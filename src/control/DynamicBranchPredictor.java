package control;

import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.ValueOperand;

public abstract class DynamicBranchPredictor extends BranchPredictor {

  protected abstract DynamicBranchMetric createMetricForEntry();

  @Override
  protected Optional<Integer> predictBranchInstruction(FetchedInstruction fetchedBranchInstruction, ValueOperand deltaOperand) {
    int nextLine = fetchedBranchInstruction.getLineNumber() + 1;
    int predictedLine = nextLine;
    boolean predictTake = true;

    if (getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber()).isPresent()) {
      BranchTargetAddressCacheEntry entry = getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber()).get();
      if (entry.getPredictionMade()) {
        return Optional.empty();
      }
      predictTake = entry.getDynamicBranchMetric().get().shouldTakeBranch();
      predictedLine = predictTake ? entry.getTargetLine() : nextLine;
      entry.setPrediction(predictTake);
    } else {
      DynamicBranchMetric branchMetric = createMetricForEntry();
      int targetLine = fetchedBranchInstruction.getLineNumber() + deltaOperand.getValue();
      predictTake = branchMetric.shouldTakeBranch();
      predictedLine = predictTake ? targetLine : nextLine;
      BranchTargetAddressCacheEntry entry = new BranchTargetAddressCacheEntry(targetLine, branchMetric);
      entry.setPrediction(predictTake);
      getBranchTargetAddressCache().addEntry(fetchedBranchInstruction.getLineNumber(), entry);
    }
    return Optional.of(predictedLine);
  }

}