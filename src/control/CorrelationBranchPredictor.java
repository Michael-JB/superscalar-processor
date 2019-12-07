package control;

public class CorrelationBranchPredictor extends DynamicBranchPredictor {

  private final int tableWidth, saturationBitCount;

  public CorrelationBranchPredictor(int tableWidth, int saturationBitCount) {
    this.tableWidth = tableWidth;
    this.saturationBitCount = saturationBitCount;
  }

  @Override
  protected DynamicBranchMetric createMetricForEntry() {
    return new AdaptiveHistoryTable(tableWidth, saturationBitCount);
  }

}