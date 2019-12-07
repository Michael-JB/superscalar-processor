package control;

public abstract class DynamicBranchMetric {

  public abstract boolean shouldTakeBranch();

  public abstract void update(boolean branchTaken);

}