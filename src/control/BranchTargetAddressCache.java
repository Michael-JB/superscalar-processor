package control;

import java.util.HashMap;
import java.util.Optional;

public class BranchTargetAddressCache {

  private final HashMap<Integer, BranchTargetAddressCacheEntry> branchTargetAddressCache = new HashMap<Integer, BranchTargetAddressCacheEntry>();

  public BranchTargetAddressCache() {}

  public void addEntry(int lineNumber, BranchTargetAddressCacheEntry entry) {
    branchTargetAddressCache.put(lineNumber, entry);
  }

  public Optional<BranchTargetAddressCacheEntry> getEntryForLine(int line) {
    if (branchTargetAddressCache.containsKey(line)) {
      return Optional.of(branchTargetAddressCache.get(line));
    }
    return Optional.empty();
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