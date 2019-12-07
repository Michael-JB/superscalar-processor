package control;

public class HistoryMajorityBranchPredictor extends DynamicBranchPredictor {

  private final int historyWidth;

  public HistoryMajorityBranchPredictor(int historyWidth) {
    this.historyWidth = historyWidth;
  }

  @Override
  protected DynamicBranchMetric createMetricForEntry() {
    return new BranchHistoryShifter(historyWidth);
  }

}