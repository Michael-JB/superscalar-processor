package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import instruction.Instruction;
import instruction.RegisterOperand;
import instruction.Tag;
import instruction.TagGenerator;
import memory.Register;
import memory.RegisterFile;
import memory.RegisterFlag;
import parse.ParsedProgram;
import unit.ArithmeticLogicUnit;
import unit.BranchUnit;
import unit.LoadStoreUnit;
import unit.UnitLoadComparator;

public class Processor { /* CONSTRAINT: Currently only works if all instructions have latency 1 */

  private final boolean PIPELINE = true;
  private final int ALU_COUNT = 1; // Arithmetic Logic units
  private final int BU_COUNT = 1; // Branch units
  private final int LSU_Count = 1; // Load store units

  private final ParsedProgram parsedProgram;

  private final RegisterFile registerFile;
  private final Register programCounterRegister;

  private final List<BranchUnit> branchUnits = new ArrayList<BranchUnit>();
  private final List<ArithmeticLogicUnit> arithmeticLogicUnits = new ArrayList<ArithmeticLogicUnit>();
  private final List<LoadStoreUnit> loadStoreUnits = new ArrayList<LoadStoreUnit>();

  private final UnitLoadComparator unitLoadComparator = new UnitLoadComparator();

  private final Queue<Instruction> decodeBuffer = new LinkedList<Instruction>();
  private final Queue<Instruction> writebackBuffer = new LinkedList<Instruction>();

  private final TagGenerator tagGenerator = new TagGenerator();

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
    return !hasReachedProgramEnd() || !decodeBuffer.isEmpty() || !writebackBuffer.isEmpty()
      || arithmeticLogicUnits.stream().anyMatch(u -> u.getReservationStation().hasBufferedInstruction())
      || loadStoreUnits.stream().anyMatch(u -> u.getReservationStation().hasBufferedInstruction())
      || branchUnits.stream().anyMatch(u -> u.getReservationStation().hasBufferedInstruction())
      || arithmeticLogicUnits.stream().anyMatch(u -> u.hasInputInstruction())
      || loadStoreUnits.stream().anyMatch(u -> u.hasInputInstruction())
      || branchUnits.stream().anyMatch(u -> u.hasInputInstruction());
  }

  public void pushToDecodeBuffer(Instruction instruction) {
    decodeBuffer.offer(instruction);
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

  private boolean hasReachedProgramEnd() {
    return programCounterRegister.getValue() >= parsedProgram.getInstructionCount();
  }

  private void tickUnits() {
    arithmeticLogicUnits.forEach(u -> u.tick());
    loadStoreUnits.forEach(u -> u.tick());
    branchUnits.forEach(u -> u.tick());
  }

  private void fetch() {
    System.out.println("\nFetch");
    if (!hasReachedProgramEnd()) {
      pushToDecodeBuffer(parsedProgram.getInstructions().get(getProgramCounter().getValue()));
      getProgramCounter().setValue(getProgramCounter().getValue() + 1);
    }
  }

  private void dispatchReservationStations() {
    // System.out.println("Dispatching all reservation stations");
    arithmeticLogicUnits.forEach(u -> u.getReservationStation().dispatch());
    loadStoreUnits.forEach(u -> u.getReservationStation().dispatch());
    branchUnits.forEach(u -> u.getReservationStation().dispatch());
  }

  private void broadcastToReservationStations(Tag tag, int result) {
    // System.out.println("Broadcasting to all reservation stations");
    arithmeticLogicUnits.forEach(u -> u.getReservationStation().receive(tag, result));
    loadStoreUnits.forEach(u -> u.getReservationStation().receive(tag, result));
    branchUnits.forEach(u -> u.getReservationStation().receive(tag, result));
  }

  private void decode() {
    System.out.println("\nDecode");
    dispatchReservationStations();
    if (!decodeBuffer.isEmpty()) {
      Instruction toIssue = decodeBuffer.peek();
      toIssue.setTag(tagGenerator.generateTag());
      boolean successfulIssue = false;
      switch(toIssue.getOpcode().getCategory()) {
        case ARITHMETIC:
          successfulIssue = arithmeticLogicUnits.stream().min(unitLoadComparator).get().getReservationStation().issue(toIssue);
          break;
        case MEMORY:
          successfulIssue = loadStoreUnits.stream().min(unitLoadComparator).get().getReservationStation().issue(toIssue);
          break;
        case CONTROL:
          successfulIssue = branchUnits.stream().min(unitLoadComparator).get().getReservationStation().issue(toIssue);
          break;
      }
      if (successfulIssue) {
        decodeBuffer.poll();
      }
    }
  }

  public void incrementExecutedInstructionCount() {
    executedInstructionCount++;
  }

  private void execute() {
    System.out.println("\nExecute");
    tickUnits();
  }

  private void writeback() {
    System.out.println("\nWriteback");
    if (!writebackBuffer.isEmpty()) {
      Instruction evaluatedInstruction = writebackBuffer.poll();
      evaluatedInstruction.getWritebackResult().ifPresent(res -> {
        Optional<RegisterOperand> destinationRegister = evaluatedInstruction.getDestinationRegister();
        destinationRegister.ifPresent(reg -> {
          Register register = registerFile.getRegister(reg.getValue());
          if (register.getReservingTag().isPresent() && register.getReservingTag().get().getValue() == evaluatedInstruction.getTag().getValue()) {
            register.setFlag(RegisterFlag.VALID);
            register.setValue(res);
          }
          broadcastToReservationStations(evaluatedInstruction.getTag(), res);
          System.out.println("RS Broadcast: tag=" + evaluatedInstruction.getTag().getValue() + ", res=" + res);
        });
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