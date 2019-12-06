package memory;

import java.util.LinkedList;
import java.util.Optional;

import core.Processor;
import instruction.DecodedRegisterOperand;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.Tag;

public class ReorderBuffer {

  int REORDER_BUFFER_SIZE = 16;
  int RETIRE_THRESHOLD = 1;

  LinkedList<ReorderBufferEntry> reorderBuffer = new LinkedList<ReorderBufferEntry>();

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
    return reorderBuffer.stream().filter(entry -> entry.getDecodedInstruction().getTag().matches(tag)).map(entry -> entry.getDecodedInstruction().getExecutionResult()).findAny().orElse(Optional.empty());
  }

  public void retire() {
    int instructionsRetired = 0;
    while (hasEntries() && reorderBuffer.peekFirst().isComplete() && instructionsRetired < RETIRE_THRESHOLD) {

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

        if (retiringOpcode.getCategory().equals(OpcodeCategory.CONTROL) && result != 0) {
          if (retiringOpcode == Opcode.NOP) {
            /* Do nothing */
          } else if (retiringOpcode == Opcode.JMP || retiringOpcode == Opcode.JMPR) {
            /* Jump instruction, so update program counter absolutely */
            programCounterRegister.setValue(result);
            processor.flushPipeline();
          } else {
            /* Branch instruction, so update program counter relatively */
            programCounterRegister.setValue(toRetire.getDecodedInstruction().getLineNumber() + result);
            processor.flushPipeline();
          }
        }

        if (retiringOpcode.getCategory().equals(OpcodeCategory.MEMORY)) {
          if (retiringOpcode.equals(Opcode.SA) || retiringOpcode.equals(Opcode.SAI)) {
            processor.getMemory().storeToMemory(result, toRetire.getDecodedInstruction().getOperands()[0].getExecutionValue().get());
          }
        }
      });

      System.out.println("RETIRE INSTRUCTION: " + toRetire.getDecodedInstruction().toString());
      instructionsRetired++;
    }
  }

  public void flush() {
    reorderBuffer.clear();
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