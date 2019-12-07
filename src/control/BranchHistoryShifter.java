package control;

import java.util.Arrays;

public class BranchHistoryShifter extends DynamicBranchMetric {

  private final int width;
  private final double threshold;
  private final int[] history;

  public BranchHistoryShifter(int width) {

    if (width <= 0 || width > 5) {
      throw new IllegalArgumentException("History width out of range");
    }

    this.history = new int[width];
    this.width = width;
    this.threshold = Math.ceil(width / 2);
  }

  public int getAsInteger() {
    int total = 0;
    for (int i = 0; i < history.length; i++) {
      if (history[i] == 0 || history[i] == 1) {
        int factor = (int) Math.pow(2, history.length - i - 1);
        total += factor * history[i];
      } else {
        throw new IllegalArgumentException("Non-binary value in history shifter");
      }
    }
    return total;
  }

  public int getWidth() {
    return width;
  }

  public boolean shouldTakeBranch() {
    return Arrays.stream(history).sum() > threshold;
  }

  public void update(boolean branchTaken) {
    for (int i = history.length - 1; i > 0; i--) {
      history[i] = history[i - 1];
    }
    history[0] = branchTaken ? 1 : 0;
  }

  @Override
  public String toString() {
    return Arrays.toString(history);
  }

}