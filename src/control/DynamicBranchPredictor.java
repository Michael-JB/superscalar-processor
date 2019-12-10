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
    Optional<BranchTargetAddressCacheEntry> existingEntry = getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber());

    if (existingEntry.isPresent()) {
      predictTake = existingEntry.get().getDynamicBranchMetric().get().shouldTakeBranch();
      predictedLine = predictTake ? existingEntry.get().getTargetLine() : nextLine;
    } else {
      DynamicBranchMetric branchMetric = createMetricForEntry();
      int targetLine = fetchedBranchInstruction.getLineNumber() + deltaOperand.getValue();
      predictTake = branchMetric.shouldTakeBranch();
      predictedLine = predictTake ? targetLine : nextLine;
      existingEntry = Optional.of(new BranchTargetAddressCacheEntry(targetLine, branchMetric));
      getBranchTargetAddressCache().addEntry(fetchedBranchInstruction.getLineNumber(), existingEntry.get());
    }

    if (!existingEntry.get().setPrediction(predictTake)) {
      return Optional.empty();
    }
    return Optional.of(predictedLine);
  }

}