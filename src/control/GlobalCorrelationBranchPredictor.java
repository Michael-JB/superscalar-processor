package control;

public class GlobalCorrelationBranchPredictor extends DynamicBranchPredictor {

  private final int tableWidth, saturationBitCount;
  private final BranchHistoryShifter branchHistoryShifter;

  public GlobalCorrelationBranchPredictor(int tableWidth, int saturationBitCount) {
    this.tableWidth = tableWidth;
    this.saturationBitCount = saturationBitCount;
    this.branchHistoryShifter = new BranchHistoryShifter(tableWidth);
  }

  @Override
  protected DynamicBranchMetric createMetricForEntry() {
    return new AdaptiveHistoryTable(tableWidth, saturationBitCount, branchHistoryShifter);
  }

}