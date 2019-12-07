package control;

public class SaturatedBranchPredictor extends DynamicBranchPredictor {

  private final int saturatingCounterBits;

  public SaturatedBranchPredictor(int saturatingCounterBits) {
    this.saturatingCounterBits = saturatingCounterBits;
  }

  @Override
  protected DynamicBranchMetric createMetricForEntry() {
    return new SaturatingCounter(saturatingCounterBits);
  }

}