package memory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import core.Processor;
import instruction.DecodedInstruction;
import instruction.DecodedRegisterOperand;
import instruction.Tag;
import unit.Unit;

public class ReservationStation {

  private final int RESERVATION_STATION_SIZE = 4;
  private final Processor processor;
  private final Unit unit;

  private Queue<DecodedInstruction> instructionBuffer = new LinkedList<DecodedInstruction>();

  public ReservationStation(Processor processor, Unit unit) {
    this.unit = unit;
    this.processor = processor;
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
    return instructionBuffer.size() >= RESERVATION_STATION_SIZE;
  }

  public void receive(Tag tag, int result) {
    instructionBuffer.forEach(instruction -> {
      if (!instruction.isReady()) {
        List<DecodedRegisterOperand> sourceRegisters = instruction.getSourceRegisters();
        sourceRegisters.forEach(o -> {
          if (o.getBlockingTag().isPresent() && o.getBlockingTag().get().matches(tag)) {
            o.setExecutionValue(result);
          }
        });
      }
    });
  }

  public boolean issue(DecodedInstruction instruction) {
    if (!isFull()) {
      instruction.getSourceRegisters().forEach(o -> o.tryRetrieveValue(processor));

      /* Set register flag to INVALID */
      Optional<DecodedRegisterOperand> destinationRegister = instruction.getDestinationRegister();
      destinationRegister.ifPresent(d -> {
        Register register = processor.getRegisterFile().getRegister(d.getEncodedOperand().getValue());
        register.setFlag(RegisterFlag.INVALID);
        register.setReservingTag(instruction.getTag());
      });

      instructionBuffer.offer(instruction);
      return true;
    }
    return false;
  }

  public void dispatch() {
    if (!instructionBuffer.isEmpty()) {
      DecodedInstruction toDispatch = instructionBuffer.peek();
      if (toDispatch.isReady() && !unit.hasInputInstruction()) {
        unit.inputInstruction(instructionBuffer.poll());
      }
    }
  }

  public void flush() {
    instructionBuffer.clear();
  }

  public String getStatus() {
    return "(" + bufferedInstructionCount() + "/" + RESERVATION_STATION_SIZE + ") | " + instructionBuffer.toString();
  }

}