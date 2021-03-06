package memory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import core.Processor;
import instruction.DecodedInstruction;
import instruction.DecodedRegisterOperand;
import instruction.Opcode;
import instruction.OpcodeCategory;
import instruction.Tag;
import unit.Unit;

public class ReservationStation {

  private final int capacity;
  private final Processor processor;
  private final Unit unit;

  private LinkedList<DecodedInstruction> instructionBuffer = new LinkedList<DecodedInstruction>();

  public ReservationStation(Processor processor, Unit unit, int capacity) {
    this.processor = processor;
    this.unit = unit;
    this.capacity = capacity;
  }

  public Unit getUnit() {
    return unit;
  }

  public int bufferedInstructionCount() {
    return instructionBuffer.size();
  }

  public boolean hasBufferedInstruction() {
    return !instructionBuffer.isEmpty();
  }

  public boolean isFull() {
    return instructionBuffer.size() >= capacity;
  }

  public boolean isMemoryReady(DecodedInstruction instruction) {
    if (instruction.getInstruction().getOpcode().getCategory().equals(OpcodeCategory.MEMORY)) {
      if (instruction.isReady()) {
        Opcode opcode = instruction.getInstruction().getOpcode();
        return !processor.getReorderBuffer().getLoadStoreBuffer().previousMemoryAccessExistsForInstruction(instruction,
          (opcode.equals(Opcode.LA) || opcode.equals(Opcode.LAI)));
      }
      return false;
    }
    return true;
  }

  public void receive(Tag tag, int result) {
    instructionBuffer.forEach(instruction -> {
      if (!instruction.isReady()) {
        List<DecodedRegisterOperand> sourceRegisters = instruction.getSourceRegisters();
        sourceRegisters.forEach(o -> {
          if (!o.getExecutionValue().isPresent() && o.getBlockingTag().isPresent() && o.getBlockingTag().get().matches(tag)) {
            o.setExecutionValue(result);
          }
        });
      }
    });
  }

  private boolean canDispatch(DecodedInstruction instruction) {
    return isMemoryReady(instruction) && isUnitReady() && instruction.isReady();
  }

  public boolean issue(DecodedInstruction instruction) {
    if (!isFull()) {
      instruction.getSourceRegisters().forEach(o -> o.tryRetrieveValue(processor));

      /* Bypass if instruction is already ready */
      if (canDispatch(instruction) && instructionBuffer.isEmpty()) {
        unit.inputInstruction(instruction);
      } else {
        instructionBuffer.offer(instruction);
      }

      /* Set register flag to INVALID */
      Optional<DecodedRegisterOperand> destinationRegister = instruction.getDestinationRegister();
      destinationRegister.ifPresent(d -> {
        Register register = processor.getRegisterFile().getRegister(d.getEncodedOperand().getValue());
        register.setFlag(RegisterFlag.INVALID);
        register.setReservingTag(instruction.getTag());
      });

      return true;
    }
    return false;
  }

  private boolean isUnitReady() {
    return !unit.hasInputInstruction();
  }

  public void dispatch() {
    for (int i = 0; i < instructionBuffer.size(); i++) {
      if (canDispatch(instructionBuffer.get(i))) {
        unit.inputInstruction(instructionBuffer.get(i));
        instructionBuffer.remove(i);
        break;
      }
    }
  }

  public void flush() {
    instructionBuffer.clear();
  }

  public String getStatus() {
    return "(" + bufferedInstructionCount() + "/" + capacity + ") | " + instructionBuffer.toString();
  }

}