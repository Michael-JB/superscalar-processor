package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import unit.UnitLoadComparator;

public class Processor { /* CONSTRAINT: Currently only works if all instructions have latency 1 */

  private final boolean PIPELINE = true;
  private final int ALU_COUNT = 2; // Arithmetic Logic units
  private final int BU_COUNT = 1; // Branch units
  private final int LSU_Count = 2; // Load store units

  private final ParsedProgram parsedProgram;

  private final RegisterFile registerFile;
  private final Register programCounterRegister;

  private final List<BranchUnit> branchUnits = new ArrayList<BranchUnit>();
  private final List<ArithmeticLogicUnit> arithmeticLogicUnits = new ArrayList<ArithmeticLogicUnit>();
  private final List<LoadStoreUnit> loadStoreUnits = new ArrayList<LoadStoreUnit>();

  private final UnitLoadComparator unitLoadComparator = new UnitLoadComparator();

  private final Queue<Instruction> decodeBuffer = new LinkedList<Instruction>();
  private final Queue<Instruction> executeBuffer = new LinkedList<Instruction>();
  private final Queue<Instruction> writebackBuffer = new LinkedList<Instruction>();

  private Stage currentStage = Stage.FETCH;
  private int cycleCount = 0, executedInstructionCount = 0;

  public Processor(ParsedProgram parsedProgram, int registerFileCapacity, int memoryCapacity) {
    this.parsedProgram = parsedProgram;
    this.registerFile = new RegisterFile(registerFileCapacity);
    for (int i = 0; i < ALU_COUNT; i++) {
      arithmeticLogicUnits.add(new ArithmeticLogicUnit(this));
    }
    for (int i = 0; i < BU_COUNT; i++) {
      branchUnits.add(new BranchUnit(this));
    }
    for (int i = 0; i < LSU_Count; i++) {
      loadStoreUnits.add(new LoadStoreUnit(this, memoryCapacity));
    }
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

  public boolean step() {
    if (isProcessing()) {
      tick();
      return true;
    }
    return false;
  }

  private boolean isProcessing() {
    return !hasReachedProgramEnd() || !decodeBuffer.isEmpty() || !executeBuffer.isEmpty()
      || !writebackBuffer.isEmpty() || arithmeticLogicUnits.stream().anyMatch(u -> u.hasBufferedInstruction())
      || loadStoreUnits.stream().anyMatch(u -> u.hasBufferedInstruction())
      || branchUnits.stream().anyMatch(u -> u.hasBufferedInstruction());
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

  public int getCycleCount() {
    return cycleCount;
  }

  public int getExecutedInstructionCount() {
    return executedInstructionCount;
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
    arithmeticLogicUnits.forEach(u -> u.tick());
    loadStoreUnits.forEach(u -> u.tick());
    branchUnits.forEach(u -> u.tick());
  }

  private void fetch() {
    if (!hasReachedProgramEnd()) {
      pushToDecodeBuffer(parsedProgram.getInstructions().get(getProgramCounter().getValue()));
      getProgramCounter().setValue(getProgramCounter().getValue() + 1);
    }
  }

  private void decode() {
    if (!decodeBuffer.isEmpty()) {
      pushToExecuteBuffer(decodeInstruction(decodeBuffer.poll()));
    }
  }

  private void execute() {
    if (!executeBuffer.isEmpty()) {
      Instruction toExecute = executeBuffer.poll();
      switch(toExecute.getOpcode().getCategory()) {
        case ARITHMETIC:
          arithmeticLogicUnits.stream().min(unitLoadComparator).get().bufferInstruction(toExecute);
          break;
        case MEMORY:
          loadStoreUnits.stream().min(unitLoadComparator).get().bufferInstruction(toExecute);
          break;
        case CONTROL:
          branchUnits.stream().min(unitLoadComparator).get().bufferInstruction(toExecute);
          break;
      }
      executedInstructionCount++;
    }
    tickUnits();
  }

  private void writeback() {
    if (!writebackBuffer.isEmpty()) {
      Instruction evaluatedInstruction = writebackBuffer.poll();
      evaluatedInstruction.getWritebackResult().ifPresent(res -> {
        Optional<Integer> destinationRegister = getDestinationRegister(evaluatedInstruction);
        destinationRegister.ifPresent(reg -> registerFile.getRegister(reg).setValue(res));
      });
    }
  }

  public void tick() {
    if (PIPELINE) {
      writeback();
      execute();
      decode();
      fetch();
    } else {
      switch(currentStage) {
        case FETCH:
          fetch();
          currentStage = Stage.DECODE;
          break;
        case DECODE:
          decode();
          currentStage = Stage.EXECUTE;
          break;
        case EXECUTE:
          execute();
          currentStage = Stage.WRITEBACK;
          break;
        case WRITEBACK:
          writeback();
          currentStage = Stage.FETCH;
          break;
      }
    }

    cycleCount++;
  }
}