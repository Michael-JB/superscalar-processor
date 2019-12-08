package memory;

import java.util.LinkedList;
import java.util.Optional;

import core.Processor;
import instruction.DecodedRegisterOperand;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.Tag;

public class ReorderBuffer {

  private final int capacity;
  private final LinkedList<ReorderBufferEntry> reorderBuffer = new LinkedList<ReorderBufferEntry>();
  private final LoadStoreBuffer loadStoreBuffer;
  private final Processor processor;

  public ReorderBuffer(Processor processor, int capacity, int loadStoreBufferCapacity) {
    this.processor = processor;
    this.capacity = capacity;
    loadStoreBuffer = new LoadStoreBuffer(loadStoreBufferCapacity);
  }

  public boolean hasEntries() {
    return !reorderBuffer.isEmpty();
  }

  public boolean isFull() {
    return loadStoreBuffer.isFull() || reorderBuffer.size() >= capacity;
  }

  public boolean pushToTail(ReorderBufferEntry entry) {
    if (!isFull()) {
      reorderBuffer.addLast(entry);
      if (entry.getDecodedInstruction().getInstruction().getOpcode().getCategory().equals(OpcodeCategory.MEMORY)) {
        loadStoreBuffer.pushToTail(entry.getDecodedInstruction());
      }
      return true;
    }
    return false;
  }

  public Optional<Integer> getValueForTag(Tag tag) {
    return reorderBuffer.stream()
      .filter(entry -> entry.getDecodedInstruction().getTag().matches(tag))
      .map(entry -> entry.getDecodedInstruction().getExecutionResult())
      .findAny()
      .orElse(Optional.empty());
  }

  public LoadStoreBuffer getLoadStoreBuffer() {
    return loadStoreBuffer;
  }

  public void retire() {
    int instructionsRetired = 0;
    while (hasEntries() && reorderBuffer.peekFirst().isComplete() && instructionsRetired < processor.getProcessorConfiguration().getWidth()) {
      ReorderBufferEntry toRetire = reorderBuffer.pollFirst();
      toRetire.getDecodedInstruction().getExecutionResult().ifPresent(result -> {
        Optional<DecodedRegisterOperand> destinationRegister = toRetire.getDecodedInstruction().getDestinationRegister();
        destinationRegister.ifPresent(reg -> {
          Register register = processor.getRegisterFile().getRegister(reg.getEncodedOperand().getValue());
          if (register.getReservingTag().isPresent() && register.getReservingTag().get().matches(toRetire.getDecodedInstruction().getTag())) {
            register.setFlag(RegisterFlag.VALID);
            register.clearReservingTag();
          }
          register.setValue(result);
        });

        Register programCounterRegister = processor.getProgramCounter();
        Opcode retiringOpcode = toRetire.getDecodedInstruction().getInstruction().getOpcode();

        if (retiringOpcode.getCategory().equals(OpcodeCategory.CONTROL)
          && toRetire.getDecodedInstruction().getBranchTarget().isPresent()) {
          toRetire.getDecodedInstruction().getBranchTaken().ifPresent(taken -> {
            processor.getBranchPredictor().getBranchTargetAddressCache().getEntryForLine(toRetire.getDecodedInstruction().getLineNumber()).ifPresent(entry -> {
              if (entry.getPredictedTaken() == taken) {
                processor.incrementCorrectBranchPredictions();
              } else {
                processor.incrementIncorrectBranchPredictions();
                processor.flushPipeline();
                if (taken) {
                  programCounterRegister.setValue(toRetire.getDecodedInstruction().getBranchTarget().get());
                } else {
                  programCounterRegister.setValue(toRetire.getDecodedInstruction().getLineNumber() + 1);
                }
              }
              entry.alertBranchRetired(taken);
            });
          });
        }

        if (retiringOpcode.getCategory().equals(OpcodeCategory.MEMORY)) {
          if (retiringOpcode.equals(Opcode.SA) || retiringOpcode.equals(Opcode.SAI)) {
            processor.getMemory().storeToMemory(result, toRetire.getDecodedInstruction().getOperands()[0].getExecutionValue().get());
          }
        }
      });

      toRetire.getDecodedInstruction().getRuntimeError().ifPresent(error -> processor.raiseRuntimeError(error));
      loadStoreBuffer.retireInstruction(toRetire.getDecodedInstruction());
      processor.getTagGenerator().retireTag(toRetire.getDecodedInstruction().getTag());
      System.out.println("RETIRE INSTRUCTION: " + toRetire.getDecodedInstruction().toString());
      instructionsRetired++;
    }
  }

  public void flush() {
    reorderBuffer.clear();
    loadStoreBuffer.flush();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (reorderBuffer.isEmpty()) {
      sb.append("Empty" + System.lineSeparator());
    } else {
      for (int i = 0; i < reorderBuffer.size(); i++) {
        String header = "      |  ";
        if (i == 0) {
          header = "HEAD --> ";
        } else if (i == reorderBuffer.size() - 1) {
          header = "TAIL --> ";
        }
        sb.append(header + reorderBuffer.get(i).toString() + System.lineSeparator());
      }
    }
    return sb.toString();
  }

}