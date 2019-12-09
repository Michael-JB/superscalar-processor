package control;

import java.util.Arrays;
import java.util.Optional;

import instruction.FetchedInstruction;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.ValueOperand;

public abstract class BranchPredictor {

  private final BranchTargetAddressCache branchTargetAddressCache;

  public BranchPredictor() {
    this.branchTargetAddressCache = new BranchTargetAddressCache();
  }

  public BranchTargetAddressCache getBranchTargetAddressCache() {
    return branchTargetAddressCache;
  }

  protected abstract Optional<Integer> predictBranchInstruction(FetchedInstruction fetchedBranchInstruction, ValueOperand deltaOperand);

  public Optional<Integer> predict(FetchedInstruction fetchedInstruction) {
    Opcode opcode = fetchedInstruction.getInstruction().getOpcode();
    int nextLine = fetchedInstruction.getLineNumber() + 1;
    if (opcode.getCategory().equals(OpcodeCategory.CONTROL)) {
      Optional<ValueOperand> valueOperand = Arrays.stream(fetchedInstruction.getInstruction().getOperands())
        .filter(o -> o instanceof ValueOperand)
        .map(o -> (ValueOperand) o)
        .findAny();

      if (valueOperand.isPresent()) {
        if (opcode == Opcode.JMP) {
          return Optional.of(valueOperand.get().getValue());
        } else {
          return predictBranchInstruction(fetchedInstruction, valueOperand.get());
        }
      }
    }
    return Optional.of(nextLine);
  }

}