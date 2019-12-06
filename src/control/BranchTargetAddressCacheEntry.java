package control;

public class BranchTargetAddressCacheEntry {

  private final int targetLine;

  public BranchTargetAddressCacheEntry(int targetLine) {
    this.targetLine = targetLine;
  }

  public int getTargetLine() {
    return targetLine;
  }

  @Override
  public String toString() {
    return "Target: " + targetLine;
  }

}