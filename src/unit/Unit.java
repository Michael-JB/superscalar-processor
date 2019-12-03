package unit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import core.Processor;
import instruction.Instruction;
import instruction.RegisterOperand;
import instruction.ValueOperand;
import memory.ReservationStation;

public abstract class Unit {

  protected final Processor processor;
  protected final ReservationStation reservationStation;
  protected int delayCounter = 0;

  protected Queue<Instruction> instructionBuffer = new LinkedList<Instruction>();

  public Unit(Processor processor) {
    this.processor = processor;
    this.reservationStation = new ReservationStation(processor, this);
  }

  public void bufferInstruction(Instruction instruction) {
    instructionBuffer.offer(instruction);
  }

  protected Processor getProcessor() {
    return processor;
  }

  public ReservationStation getReservationStation() {
    return reservationStation;
  }

  public boolean hasBufferedInstruction() {
    return !instructionBuffer.isEmpty();
  }

  private Instruction getCurrentInstruction() {
    return instructionBuffer.peek();
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

  private Instruction completeCurrentInstruction() {
    Instruction completed = instructionBuffer.poll();
    processor.pushToWritebackBuffer(completed);
    resetDelayCounter();
    processor.incrementExecutedInstructionCount();
    return completed;
  }

  public void tick() {
    if (hasBufferedInstruction()) {
      /* Get instruction from queue */
      Instruction toExecute = getCurrentInstruction();

      /* Process instruction on first tick */
      if (getDelayCounter() == 0) {
        System.out.println("Execution start: " + toExecute.toString());
        process(toExecute);
      }

      /* Increment delay counter on each tick, until latency reached */
      incrementDelayCounter();

      if (getDelayCounter() >= toExecute.getOpcode().getLatency()) {
        /* Instruction has now been completed */
        completeCurrentInstruction();
      }
    }
  }

  public abstract void process(Instruction instruction);

}