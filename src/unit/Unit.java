package unit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import core.Processor;
import instruction.Instruction;
import instruction.RegisterOperand;
import instruction.ValueOperand;

public abstract class Unit {

  protected final Processor processor;

  protected Queue<Instruction> instructionBuffer = new LinkedList<Instruction>();

  public Unit(Processor processor) {
    this.processor = processor;
  }

  public void bufferInstruction(Instruction instruction) {
    instructionBuffer.offer(instruction);
  }

  protected Processor getProcessor() {
    return processor;
  }

  protected boolean hasBufferedInstruction() {
    return !instructionBuffer.isEmpty();
  }

  protected Instruction getCurrentInstruction() {
    return instructionBuffer.peek();
  }

  protected Instruction completeCurrentInstruction() {
    Instruction completed = instructionBuffer.poll();
    processor.pushToWritebackBuffer(completed);
    return completed;
  }

  protected ValueOperand[] getValuesFromRegisters(Instruction instruction) {
    return Arrays.stream(instruction.getOperands()).map(o -> {
      if (o instanceof RegisterOperand) {
        return new ValueOperand(processor.getRegisterFile().getRegister(o.getValue()).getValue());
      } else {
        return o;
      }
    }).toArray(ValueOperand[]::new);
  }

  public abstract void tick();

}