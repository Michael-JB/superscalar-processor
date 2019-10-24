package core;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import instruction.Instruction;
import instruction.Operand;
import instruction.RegisterOperand;
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
  private int cycles = 0;

  private Queue<Instruction> decodeBuffer = new LinkedList<Instruction>();
  private Queue<Instruction> executeBuffer = new LinkedList<Instruction>();
  private Queue<Instruction> writebackBuffer = new LinkedList<Instruction>();

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

  public int getCycles() {
    return cycles;
  }

  public RegisterFile getRegisterFile() {
    return registerFile;
  }

  private Instruction decodeInstruction(Instruction instruction) {
    return instruction;
  }

  private Optional<Integer> getDestinationRegister(Instruction instruction) {
    Operand[] instructionOperands = instruction.getOperands();
    if (instructionOperands.length > 0) {
      Operand firstOperand = instructionOperands[0];
      if (firstOperand instanceof RegisterOperand) {
        return Optional.of(firstOperand.getValue());
      }
    }
    return Optional.empty();
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
          Instruction toExecute = executeBuffer.poll();
          switch(toExecute.getOpcode().getCategory()) {
            case ARITHMETIC:
              arithmeticLogicUnit.bufferInstruction(toExecute);
              arithmeticLogicUnit.tick();
              arithmeticLogicUnit.getResult().ifPresent(res -> toExecute.setResult(res));
              writebackBuffer.offer(toExecute);
              break;
            case MEMORY:
              loadStoreUnit.bufferInstruction(toExecute);
              loadStoreUnit.tick();
              loadStoreUnit.getResult().ifPresent(res -> toExecute.setResult(res));
              writebackBuffer.offer(toExecute);
              break;
            case CONTROL:
              break;
          }
        }
        currentStage = Stage.WRITEBACK;
        break;
      case WRITEBACK:
        if (!writebackBuffer.isEmpty()) {
          Instruction evaluatedInstruction = writebackBuffer.poll();
          evaluatedInstruction.getResult().ifPresent(res -> {
            Optional<Integer> destinationRegister = getDestinationRegister(evaluatedInstruction);
            destinationRegister.ifPresent(reg -> registerFile.getRegister(reg).setValue(res));
          });
        }
        programCounterRegister.setValue(programCounterRegister.getValue() + 1);
        currentStage = Stage.FETCH;
        break;
    }
    cycles++;
  }

}