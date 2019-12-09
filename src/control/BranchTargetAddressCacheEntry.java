package control;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class BranchTargetAddressCacheEntry {

  private final int MAX_PREDICTION_LEVEL = 16;

  private final int targetLine;
  private Optional<DynamicBranchMetric> dynamicBranchMetric = Optional.empty();
  private LinkedList<Boolean> predictions = new LinkedList<Boolean>();

  public BranchTargetAddressCacheEntry(int targetLine) {
    this.targetLine = targetLine;
  }

  public BranchTargetAddressCacheEntry(int targetLine, DynamicBranchMetric branchMetric) {
    this.targetLine = targetLine;
    this.dynamicBranchMetric = Optional.of(branchMetric);
  }

  public void alertBranchRetired(boolean branchTaken) {
    dynamicBranchMetric.ifPresent(metric -> metric.update(branchTaken));
    if (!predictions.isEmpty()) {
      predictions.removeFirst();
    }
  }

  public Optional<DynamicBranchMetric> getDynamicBranchMetric() {
    return dynamicBranchMetric;
  }

  public int getTargetLine() {
    return targetLine;
  }

  public void setPrediction(boolean predictedTaken) {
    predictions.addLast(predictedTaken);
  }

  public boolean isAtMaxLevel() {
    return predictions.size() >= MAX_PREDICTION_LEVEL;
  }

  public boolean getPredictedTaken() {
    return predictions.peek();
  }

  public void resetPredictionMade() {
    predictions.clear();
  }

  @Override
  public String toString() {
    return targetLine
      + " (Prediction queue: "
      + (predictions.isEmpty() ? "-" :
        predictions.stream()
          .map(p -> p ? "1" : "0")
          .collect(Collectors.joining("")))
      + ")"
      + (dynamicBranchMetric.isPresent() ? " " + dynamicBranchMetric.get().toString() : "");
  }

}