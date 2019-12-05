package memory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import core.Processor;
import instruction.RegisterOperand;
import instruction.Tag;

public class ReorderBuffer {

  int REORDER_BUFFER_SIZE = 16;
  int RETIRE_THRESHOLD = 1;

  Deque<ReorderBufferEntry> reorderBuffer = new ArrayDeque<ReorderBufferEntry>();

  private final Processor processor;

  public ReorderBuffer(Processor processor) {
    this.processor = processor;
  }

  public boolean hasEntries() {
    return !reorderBuffer.isEmpty();
  }

  public boolean isFull() {
    return reorderBuffer.size() >= REORDER_BUFFER_SIZE;
  }

  public boolean pushToTail(ReorderBufferEntry entry) {
    if (!isFull()) {
      reorderBuffer.addLast(entry);
      return true;
    }
    return false;
  }

  public Optional<Integer> getValueForTag(Tag tag) {
    return reorderBuffer.stream().filter(entry -> entry.getInstruction().getTag().getValue() == tag.getValue()).map(entry -> entry.getInstruction().getExecutionResult()).findAny().orElse(Optional.empty());
  }

  public void retire() {
    int instructionsRetired = 0;
    while (hasEntries() && reorderBuffer.peekFirst().isComplete() && instructionsRetired < RETIRE_THRESHOLD) {

      ReorderBufferEntry toRetire = reorderBuffer.pollFirst();
      toRetire.getExecutionResult().ifPresent(result -> {
        Optional<RegisterOperand> destinationRegister = toRetire.getInstruction().getDestinationRegister();
        destinationRegister.ifPresent(reg -> {
          Register register = processor.getRegisterFile().getRegister(reg.getValue());
          if (register.getReservingTag().isPresent() && register.getReservingTag().get().getValue() == toRetire.getInstruction().getTag().getValue()) {
            register.setFlag(RegisterFlag.VALID);
            register.clearReservingTag();
            register.setValue(result);
          }
          System.out.println("RETIRE INSTRUCTION: " + toRetire.getInstruction().toString() + " | Result: " + result);
        });
      });

      instructionsRetired++;
    }
  }

  public String getStatus() {
    return reorderBuffer.toString();
  }

}