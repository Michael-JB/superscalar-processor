package control;

public class BranchTargetAddressCacheEntry {

  private final int targetLine;
  private boolean predictedTaken = false;

  public BranchTargetAddressCacheEntry(int targetLine) {
    this.targetLine = targetLine;
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
    return "Target: " + targetLine + " | Prediction: " + (predictedTaken ? "Taken" : "Not taken");
  }

}