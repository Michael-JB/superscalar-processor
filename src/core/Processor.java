package core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import instruction.DecodedInstruction;
import instruction.Instruction;
import instruction.RegisterOperand;
import instruction.ValueOperand;
import memory.RegisterFile;
import parse.ParsedProgram;
import unit.ArithmeticLogicUnit;

public class Processor {

  private final ParsedProgram parsedProgram;

  private final Queue<Instruction> instructionQueue;
  private final RegisterFile registerFile;
  private final ArithmeticLogicUnit arithmeticLogicUnit;

  private int programCounter = 0;

  private Stage currentStage = Stage.FETCH;

  private Instruction currentInstruction;
  private DecodedInstruction currentDecodedInstruction;
  private int result;

  public Processor(ParsedProgram parsedProgram) {
    this.parsedProgram = parsedProgram;
    this.instructionQueue = new LinkedList<Instruction>(parsedProgram.getInstructions());
    this.registerFile = new RegisterFile(16);
    this.arithmeticLogicUnit = new ArithmeticLogicUnit();
  }

  private enum Stage {
    FETCH, DECODE, EXECUTE, WRITEBACK;
  }

  public void run() {
    while(programCounter < parsedProgram.getInstructions().size()) {
      tick();
    }

    /* Dump register status */
    System.out.print(registerFile.toString());
  }

  public void tick() {
    switch(currentStage) {
      case FETCH:
        currentInstruction = instructionQueue.poll();
        currentStage = Stage.DECODE;
        break;
      case DECODE:
        currentDecodedInstruction = new DecodedInstruction(currentInstruction, Arrays.stream(currentInstruction.getOperands()).map(o -> {
          if (o instanceof RegisterOperand) {
            return new ValueOperand(registerFile.getRegister(o.getValue()).getValue());
          } else return o;
        }).toArray(ValueOperand[]::new));
        currentStage = Stage.EXECUTE;
        break;
      case EXECUTE:
        arithmeticLogicUnit.bufferInstruction(currentDecodedInstruction);
        arithmeticLogicUnit.tick();
        result = arithmeticLogicUnit.getResult().get();
        currentStage = Stage.WRITEBACK;
        break;
      case WRITEBACK:
        registerFile.getRegister(currentInstruction.getOperands()[0].getValue()).setValue(result);
        programCounter++;
        currentStage = Stage.FETCH;
        break;
    }
  }

}