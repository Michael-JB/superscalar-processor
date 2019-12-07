package control;

import java.util.Optional;

public class BranchTargetAddressCacheEntry {

  private final int targetLine;
  private boolean predictedTaken = false;
  private Optional<SaturatingCounter> saturatingCounter = Optional.empty();

  public BranchTargetAddressCacheEntry(int targetLine) {
    this.targetLine = targetLine;
  }

  public BranchTargetAddressCacheEntry(int targetLine, SaturatingCounter saturatingCounter) {
    this.targetLine = targetLine;
    this.saturatingCounter = Optional.of(saturatingCounter);
  }

  public void alertBranchRetired(boolean branchTaken) {
    saturatingCounter.ifPresent(counter -> counter.updateCounter(branchTaken));
  }

  public Optional<SaturatingCounter> getSaturatingCounter() {
    return saturatingCounter;
  }

  public int getTargetLine() {
    return targetLine;
  }

  public void setPredictedTaken(boolean predictedTaken) {
    this.predictedTaken = predictedTaken;
  }

  public boolean getPredictedTaken() {
    return predictedTaken;
  }

  @Override
  public String toString() {
    return "Target: " + targetLine + " | Prediction: " + (predictedTaken ? "Taken" : "Not taken") + (saturatingCounter.isPresent() ? " " + saturatingCounter.get().toString() : "");
  }

}