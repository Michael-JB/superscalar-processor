package core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import instruction.DecodedInstruction;
import instruction.EvaluatedInstruction;
import instruction.Instruction;
import instruction.RegisterOperand;
import instruction.ValueOperand;
import memory.Register;
import memory.RegisterFile;
import parse.ParsedProgram;
import unit.ArithmeticLogicUnit;
import unit.LoadStoreUnit;

public class Processor {

  private final ParsedProgram parsedProgram;

  private final RegisterFile registerFile;
  private final Register programCounterRegister;
  private final ArithmeticLogicUnit arithmeticLogicUnit;
  private final LoadStoreUnit loadStoreUnit;

  private Stage currentStage = Stage.FETCH;

  private Queue<Instruction> decodeBuffer = new LinkedList<Instruction>();
  private Queue<DecodedInstruction> executeBuffer = new LinkedList<DecodedInstruction>();
  private Queue<EvaluatedInstruction> writebackBuffer = new LinkedList<EvaluatedInstruction>();

  public Processor(ParsedProgram parsedProgram, int registerFileCapacity) {
    this.parsedProgram = parsedProgram;
    this.registerFile = new RegisterFile(registerFileCapacity);
    this.arithmeticLogicUnit = new ArithmeticLogicUnit(this);
    this.loadStoreUnit = new LoadStoreUnit(this);
    this.programCounterRegister = new Register(registerFileCapacity);
    this.programCounterRegister.setValue(0);
  }

  private enum Stage {
    FETCH, DECODE, EXECUTE, WRITEBACK;
  }

  public void run() {
    while(programCounterRegister.getValue() < parsedProgram.getInstructions().size()) {
      tick();
    }
  }

  public RegisterFile getRegisterFile() {
    return registerFile;
  }

  private DecodedInstruction decodeInstruction(Instruction instruction) {
    return new DecodedInstruction(instruction, Arrays.stream(instruction.getOperands()).map(o -> {
        if (o instanceof RegisterOperand) {
          return new ValueOperand(registerFile.getRegister(o.getValue()).getValue());
        } else {
          return o;
        }
      }).toArray(ValueOperand[]::new));
  }

  public void tick() {
    switch(currentStage) {
      case FETCH:
        decodeBuffer.offer(parsedProgram.getInstructions().get(programCounterRegister.getValue()));
        currentStage = Stage.DECODE;
        break;
      case DECODE:
        if (!decodeBuffer.isEmpty()) {
          executeBuffer.offer(decodeInstruction(decodeBuffer.poll()));
        }
        currentStage = Stage.EXECUTE;
        break;
      case EXECUTE:
        if (!executeBuffer.isEmpty()) {
          DecodedInstruction toExecute = executeBuffer.poll();
          switch(toExecute.getEncodedInstruction().getOpcode().getCategory()) {
            case ARITHMETIC:
              arithmeticLogicUnit.bufferInstruction(toExecute);
              arithmeticLogicUnit.tick();
              writebackBuffer.offer(new EvaluatedInstruction(toExecute, arithmeticLogicUnit.getResult()));
              break;
            case MEMORY:
              loadStoreUnit.bufferInstruction(toExecute);
              loadStoreUnit.tick();
              writebackBuffer.offer(new EvaluatedInstruction(toExecute, loadStoreUnit.getResult()));
              break;
            case CONTROL:
              break;
          }
        }
        currentStage = Stage.WRITEBACK;
        break;
      case WRITEBACK:
        if (!writebackBuffer.isEmpty()) {
          EvaluatedInstruction evaluatedInstruction = writebackBuffer.poll();
          if (evaluatedInstruction.getResult().isPresent()) {
            registerFile.getRegister(evaluatedInstruction.getDecodedInstruction().getEncodedInstruction().getOperands()[0].getValue()).setValue(evaluatedInstruction.getResult().get());
          }
        }
        programCounterRegister.setValue(programCounterRegister.getValue() + 1);
        currentStage = Stage.FETCH;
        break;
    }
  }

}