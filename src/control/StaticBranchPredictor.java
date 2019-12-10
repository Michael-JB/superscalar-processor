package control;

import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.ValueOperand;

public abstract class StaticBranchPredictor extends BranchPredictor {

  protected abstract boolean shouldTakeBranch(int targetLine, int instructionLine);

  @Override
  protected Optional<Integer> predictBranchInstruction(FetchedInstruction fetchedBranchInstruction, ValueOperand deltaOperand) {
    int nextLine = fetchedBranchInstruction.getLineNumber() + 1;
    int predictedLine = nextLine;
    boolean predictTake = true;
    Optional<BranchTargetAddressCacheEntry> existingEntry = getBranchTargetAddressCache().getEntryForLine(fetchedBranchInstruction.getLineNumber());

    if (existingEntry.isPresent()) {
      predictTake = shouldTakeBranch(existingEntry.get().getTargetLine(), fetchedBranchInstruction.getLineNumber());
      predictedLine = predictTake ? existingEntry.get().getTargetLine() : nextLine;
    } else {
      int targetLine = fetchedBranchInstruction.getLineNumber() + deltaOperand.getValue();
      predictTake = shouldTakeBranch(targetLine, fetchedBranchInstruction.getLineNumber());
      predictedLine = predictTake ? targetLine : nextLine;
      existingEntry = Optional.of(new BranchTargetAddressCacheEntry(targetLine));
      getBranchTargetAddressCache().addEntry(fetchedBranchInstruction.getLineNumber(), existingEntry.get());
    }
    if (!existingEntry.get().setPrediction(predictTake)) {
      return Optional.empty();
    }
    return Optional.of(predictedLine);
  }

}