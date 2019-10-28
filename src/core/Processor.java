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
import unit.BranchUnit;
import unit.LoadStoreUnit;

public class Processor { /* CONSTRAINT: Currently only works if all instructions have latency 1 */

  private final ParsedProgram parsedProgram;

  private final RegisterFile registerFile;
  private final Register programCounterRegister;
  private final ArithmeticLogicUnit arithmeticLogicUnit;
  private final LoadStoreUnit loadStoreUnit;
  private final BranchUnit branchUnit;

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
    this.branchUnit = new BranchUnit(this);
    this.programCounterRegister = new Register(registerFileCapacity);
    this.programCounterRegister.setValue(0);
  }

  private enum Stage {
    FETCH, DECODE, EXECUTE, WRITEBACK;
  }

  public void run() {
    while(isProcessing()) {
      tick();
    }
  }

  private boolean isProcessing() {
    return !hasReachedProgramEnd() || !decodeBuffer.isEmpty() || !executeBuffer.isEmpty()
      || !writebackBuffer.isEmpty() || arithmeticLogicUnit.hasBufferedInstruction()
      || loadStoreUnit.hasBufferedInstruction();
  }

  public void pushToDecodeBuffer(Instruction instruction) {
    decodeBuffer.offer(instruction);
  }

  public void pushToExecuteBuffer(Instruction instruction) {
    executeBuffer.offer(instruction);
  }

  public void pushToWritebackBuffer(Instruction instruction) {
    writebackBuffer.offer(instruction);
  }

  public Register getProgramCounter() {
    return programCounterRegister;
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

  private boolean hasReachedProgramEnd() {
    return programCounterRegister.getValue() >= parsedProgram.getInstructionCount();
  }

  private void tickUnits() {
    arithmeticLogicUnit.tick();
    loadStoreUnit.tick();
    branchUnit.tick();
  }

  public void tick() {
    tickUnits();

    switch(currentStage) {
      case FETCH:
        if (!hasReachedProgramEnd()) {
          pushToDecodeBuffer(parsedProgram.getInstructions().get(getProgramCounter().getValue()));
          getProgramCounter().setValue(getProgramCounter().getValue() + 1);
        }
        currentStage = Stage.DECODE;
        break;
      case DECODE:
        if (!decodeBuffer.isEmpty()) {
          pushToExecuteBuffer(decodeInstruction(decodeBuffer.poll()));
        }
        currentStage = Stage.EXECUTE;
        break;
      case EXECUTE:
        if (!executeBuffer.isEmpty()) {
          Instruction toExecute = executeBuffer.poll();
          switch(toExecute.getOpcode().getCategory()) {
            case ARITHMETIC:
              arithmeticLogicUnit.bufferInstruction(toExecute);
              break;
            case MEMORY:
              loadStoreUnit.bufferInstruction(toExecute);
              break;
            case CONTROL:
              branchUnit.bufferInstruction(toExecute);
              break;
          }
        }
        currentStage = Stage.WRITEBACK;
        break;
      case WRITEBACK:
        if (!writebackBuffer.isEmpty()) {
          Instruction evaluatedInstruction = writebackBuffer.poll();
          evaluatedInstruction.getWritebackResult().ifPresent(res -> {
            Optional<Integer> destinationRegister = getDestinationRegister(evaluatedInstruction);
            destinationRegister.ifPresent(reg -> registerFile.getRegister(reg).setValue(res));
          });
        }
        currentStage = Stage.FETCH;
        break;
    }
    cycles++;
  }

}