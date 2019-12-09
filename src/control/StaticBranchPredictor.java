package control;

import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.ValueOperand;

public class StaticBranchPredictor extends BranchPredictor {

  private boolean shouldTakeBranch(int targetLine, int instructionLine) {
    return targetLine < instructionLine;
  }

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
      predictTake = shouldTakeBranch(entry.getTargetLine(), fetchedBranchInstruction.getLineNumber());
      predictedLine = predictTake ? entry.getTargetLine() : nextLine;
      entry.setPrediction(predictTake);
    } else {
      int targetLine = fetchedBranchInstruction.getLineNumber() + deltaOperand.getValue();
      predictTake = shouldTakeBranch(targetLine, fetchedBranchInstruction.getLineNumber());
      predictedLine = predictTake ? targetLine : nextLine;
      BranchTargetAddressCacheEntry entry = new BranchTargetAddressCacheEntry(targetLine);
      entry.setPrediction(predictTake);
      getBranchTargetAddressCache().addEntry(fetchedBranchInstruction.getLineNumber(), entry);
    }
    return Optional.of(predictedLine);
  }

}