package control;

public enum BranchPredictorType {

  STATIC_FORWARDS_TAKEN(new StaticForwardsTaken()),
  STATIC_BACKWARDS_TAKEN(new StaticBackwardsTaken()),
  ONE_BIT_HISTORY(new HistoryMajorityBranchPredictor(1)),
  THREE_BIT_HISTORY(new HistoryMajorityBranchPredictor(3)),
  FIVE_BIT_HISTORY(new HistoryMajorityBranchPredictor(5)),
  ONE_BIT_SATURATED(new SaturatedBranchPredictor(1)),
  TWO_BIT_SATURATED(new SaturatedBranchPredictor(2)),
  THREE_BIT_SATURATED(new SaturatedBranchPredictor(3)),
  TWO_BIT_LOCAL_TWO_LEVEL_ADAPTIVE(new CorrelationBranchPredictor(2, 2)),
  THREE_BIT_LOCAL_TWO_LEVEL_ADAPTIVE(new CorrelationBranchPredictor(3, 3)),
  TWO_BIT_GLOBAL_TWO_LEVEL_ADAPTIVE(new GlobalCorrelationBranchPredictor(2, 2)),
  THREE_BIT_GLOBAL_TWO_LEVEL_ADAPTIVE(new GlobalCorrelationBranchPredictor(3, 3));

  private final BranchPredictor branchPredictor;

  BranchPredictorType(BranchPredictor branchPredictor) {
    this.branchPredictor = branchPredictor;
  }

  public BranchPredictor getBranchPredictor() {
    return branchPredictor;
  }

}