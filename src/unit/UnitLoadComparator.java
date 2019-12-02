package unit;

import java.util.Comparator;

public class UnitLoadComparator implements Comparator<Unit> {
  public int compare(Unit u1, Unit u2) {
    return Integer.compare(u1.bufferedInstructionCount(), u2.bufferedInstructionCount());
  }
}