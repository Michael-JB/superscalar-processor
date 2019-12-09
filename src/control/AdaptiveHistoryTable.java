package control;

import java.util.HashMap;
import java.util.StringJoiner;

public class AdaptiveHistoryTable extends DynamicBranchMetric {

  private final int width, saturationBitCount;
  private final BranchHistoryShifter historyShifter;
  private final HashMap<Integer, SaturatingCounter> table = new HashMap<Integer, SaturatingCounter>();

  public AdaptiveHistoryTable(int width, int saturationBitCount) {

    if (width <= 0 || width > 3) {
      throw new IllegalArgumentException("Table width out of range");
    }

    this.historyShifter = new BranchHistoryShifter(width);
    this.width = width;
    this.saturationBitCount = saturationBitCount;
  }

  public int getWidth() {
    return width;
  }

  public boolean shouldTakeBranch() {
    int encodedHistory = historyShifter.getAsInteger();
    if (!table.containsKey(encodedHistory)) {
      table.put(encodedHistory, new SaturatingCounter(saturationBitCount));
    }
    return table.get(encodedHistory).shouldTakeBranch();
  }

  public void update(boolean branchTaken) {
    int encodedHistory = historyShifter.getAsInteger();
    if (!table.containsKey(encodedHistory)) {
      table.put(encodedHistory, new SaturatingCounter(saturationBitCount));
    }
    table.get(encodedHistory).update(branchTaken);
    historyShifter.update(branchTaken);
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(System.lineSeparator(), System.lineSeparator(), "");
    sj.add("Local history: " + historyShifter.toString());
    if (!table.isEmpty()) {
      table.keySet().stream().forEach(history -> {
        SaturatingCounter counter = table.get(history);
        sj.add("> " + String.format("%" + width + "s",
          Integer.toBinaryString(history)).replace(' ', '0') + ": " + counter.toString());
      });
    }
    return sj.toString();
  }

}