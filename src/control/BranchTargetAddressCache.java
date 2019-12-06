package control;

import java.util.HashMap;

public class BranchTargetAddressCache {

  private final HashMap<Integer, BranchTargetAddressCacheEntry> branchTargetAddressCache = new HashMap<Integer, BranchTargetAddressCacheEntry>();

  public BranchTargetAddressCache() {}

  public void addEntry(int lineNumber, BranchTargetAddressCacheEntry entry) {
    if (!branchTargetAddressCache.containsKey(lineNumber)) {
      branchTargetAddressCache.put(lineNumber, entry);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (branchTargetAddressCache.isEmpty()) {
      sb.append("Empty" + System.lineSeparator());
    } else {
      branchTargetAddressCache.keySet().stream().forEach(line -> {
        BranchTargetAddressCacheEntry entry = branchTargetAddressCache.get(line);
        sb.append(String.format("%-2d", line) + " | " + entry.toString() + System.lineSeparator());
      });
    }
    return sb.toString();
  }

}