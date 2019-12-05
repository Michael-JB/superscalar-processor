package unit;

import java.util.Optional;

import core.Processor;
import instruction.Instruction;
import instruction.InstructionStatus;
import memory.Register;
import memory.RegisterFlag;
import memory.ReservationStation;

public abstract class Unit {

  protected final Processor processor;
  protected final ReservationStation reservationStation;
  protected int delayCounter = 0;

  protected Optional<Instruction> inputInstruction = Optional.empty();

  public Unit(Processor processor) {
    this.processor = processor;
    this.reservationStation = new ReservationStation(processor, this);
  }

  public void inputInstruction(Instruction instruction) {
    inputInstruction = Optional.of(instruction);
  }

  protected Processor getProcessor() {
    return processor;
  }

  public ReservationStation getReservationStation() {
    return reservationStation;
  }

  public boolean hasInputInstruction() {
    return inputInstruction.isPresent();
  }

  private void incrementDelayCounter() {
    delayCounter++;
  }

  private int getDelayCounter() {
    return delayCounter;
  }

  private void resetDelayCounter() {
    delayCounter = 0;
  }

  private void completeCurrentInstruction() {
    if (hasInputInstruction()) {
      Instruction completed = inputInstruction.get();
      completed.setInstructionStatus(InstructionStatus.EXECUTED);
      inputInstruction = Optional.empty();
      System.out.println("EXECUTION COMPLETE: " + completed.toString());
      resetDelayCounter();
      processor.incrementExecutedInstructionCount();

      completed.getExecutionResult().ifPresent(result -> {
        completed.getDestinationRegister().ifPresent(reg -> {
          Register register = processor.getRegisterFile().getRegister(reg.getValue());
          processor.broadcastToReservationStations(completed.getTag(), result);
          if (register.getReservingTag().isPresent() && register.getReservingTag().get().getValue() == completed.getTag().getValue()) {
            register.setFlag(RegisterFlag.READY);
          }
        });
      });

    } else {
      throw new IllegalArgumentException("Cannot complete non-existent instruction");
    }
  }

  public void tick() {
    if (hasInputInstruction()) {
      /* Get instruction from queue */
      Instruction toExecute = inputInstruction.get();

      /* Process instruction on first tick */
      if (getDelayCounter() == 0) {
        System.out.println("EXECUTION START: " + toExecute.toString());
        toExecute.setInstructionStatus(InstructionStatus.EXECUTING);
      }

      /* Increment delay counter on each tick, until latency reached */
      incrementDelayCounter();

      if (getDelayCounter() >= toExecute.getOpcode().getLatency()) {
        /* Instruction has now been completed */
        process(toExecute);
        completeCurrentInstruction();
      }
    }
  }

  public String getStatus() {
    return inputInstruction.isPresent() ? inputInstruction.get() + " (" + delayCounter + "/" + inputInstruction.get().getOpcode().getLatency() + ")" : "IDLE";
  }

  public abstract void process(Instruction instruction);
}