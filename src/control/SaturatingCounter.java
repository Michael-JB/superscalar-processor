package control;

public class SaturatingCounter {

  private final int bitCount;
  private final int minimum = 0, maximum, threshold;

  private int counter;

  public SaturatingCounter(int bitCount) {

    if (bitCount <= 0 || bitCount > 8) {
      throw new IllegalArgumentException("Saturating counter bit count out of range");
    }

    this.bitCount = bitCount;
    this.maximum = (int) Math.pow(2, bitCount) - 1;
    this.threshold = (int) Math.pow(2, bitCount - 1);
    this.counter = threshold;
  }

  public int getBitCount() {
    return bitCount;
  }

  public boolean shouldTakeBranch() {
    return counter >= threshold;
  }

  public void updateCounter(boolean branchTaken) {
    if (branchTaken) {
      counter = Math.min(counter + 1, maximum);
    } else {
      counter = Math.max(counter - 1, minimum);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = minimum; i <= maximum; i++) {
      if (i == counter) {
        sb.append("C");
      } else if (i == threshold) {
        sb.append("T");
      } else {
        sb.append("-");
      }
    }
    sb.append("]");
    return sb.toString();
  }

}