package control;

import java.util.Optional;

public class BranchTargetAddressCacheEntry {

  private final int targetLine;
  private boolean predictionMade = false;
  private boolean predictedTaken = false;
  private Optional<DynamicBranchMetric> dynamicBranchMetric = Optional.empty();

  public BranchTargetAddressCacheEntry(int targetLine) {
    this.targetLine = targetLine;
  }

  public BranchTargetAddressCacheEntry(int targetLine, DynamicBranchMetric branchMetric) {
    this.targetLine = targetLine;
    this.dynamicBranchMetric = Optional.of(branchMetric);
  }

  public void alertBranchRetired(boolean branchTaken) {
    dynamicBranchMetric.ifPresent(metric -> metric.update(branchTaken));
    predictionMade = false;
  }

  public Optional<DynamicBranchMetric> getDynamicBranchMetric() {
    return dynamicBranchMetric;
  }

  public int getTargetLine() {
    return targetLine;
  }

  public void setPrediction(boolean predictedTaken) {
    this.predictedTaken = predictedTaken;
    predictionMade = true;
  }

  public boolean getPredictionMade() {
    return predictionMade;
  }

  public boolean getPredictedTaken() {
    return predictedTaken;
  }

  public void resetPredictionMade() {
    this.predictionMade = false;
  }

  @Override
  public String toString() {
    return targetLine
      + " (Predicted " + (predictedTaken ? "taken" : "not taken") + ")"
      + (dynamicBranchMetric.isPresent() ? " " + dynamicBranchMetric.get().toString() : "");
  }

}