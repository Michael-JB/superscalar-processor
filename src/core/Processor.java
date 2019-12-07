package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import control.BranchTargetAddressCache;
import control.DynamicBranchPredictor;
import control.StaticBranchPredictor;
import instruction.DecodedInstruction;
import instruction.DecodedOperand;
import instruction.FetchedInstruction;
import instruction.Instruction;
import instruction.Tag;
import instruction.TagGenerator;
import memory.Memory;
import memory.Register;
import memory.RegisterFile;
import memory.ReorderBuffer;
import memory.ReorderBufferEntry;
import memory.ReservationStation;
import parse.ParsedProgram;
import unit.ArithmeticLogicUnit;
import unit.BranchUnit;
import unit.LoadStoreUnit;
import unit.Unit;
import unit.UnitLoadComparator;

public class Processor {

  private final int REORDER_BUFFER_CAPACITY = 16;
  private final int LOAD_STORE_BUFFER_CAPACITY = 16;
  private final int RESERVATION_STATION_CAPACITIES = 4;
  private final int ALU_COUNT = 2; // Arithmetic Logic units
  private final int BU_COUNT = 1; // Branch units
  private final int LSU_Count = 2; // Load store units

  private final ParsedProgram parsedProgram;
  private final int width;
  private final Memory memory;
  private final RegisterFile registerFile;
  private final Register programCounterRegister;
  private final List<BranchUnit> branchUnits = new ArrayList<BranchUnit>();
  private final List<ArithmeticLogicUnit> arithmeticLogicUnits = new ArrayList<ArithmeticLogicUnit>();
  private final List<LoadStoreUnit> loadStoreUnits = new ArrayList<LoadStoreUnit>();
  private final UnitLoadComparator unitLoadComparator = new UnitLoadComparator();
  private final Queue<FetchedInstruction> decodeBuffer = new LinkedList<FetchedInstruction>();
  private final TagGenerator tagGenerator = new TagGenerator();
  private final ReorderBuffer reorderBuffer = new ReorderBuffer(this, REORDER_BUFFER_CAPACITY, LOAD_STORE_BUFFER_CAPACITY);

  private final BranchTargetAddressCache branchTargetAddressCache = new BranchTargetAddressCache();
  private final StaticBranchPredictor staticBranchPredictor = new StaticBranchPredictor(branchTargetAddressCache);
  private final DynamicBranchPredictor dynamicBranchPredictor = new DynamicBranchPredictor(branchTargetAddressCache, 3);

  private int cycleCount = 0, executedInstructionCount = 0, correctBranchPredictions = 0, incorrectBranchPredictions = 0;

  public Processor(ParsedProgram parsedProgram, int width, int registerFileCapacity, int memoryCapacity) {
    this.parsedProgram = parsedProgram;
    this.width = width;
    this.registerFile = new RegisterFile(registerFileCapacity);
    this.memory = new Memory(memoryCapacity);
    for (int i = 0; i < ALU_COUNT; i++) {
      arithmeticLogicUnits.add(new ArithmeticLogicUnit(this, RESERVATION_STATION_CAPACITIES));
    }
    for (int i = 0; i < BU_COUNT; i++) {
      branchUnits.add(new BranchUnit(this, RESERVATION_STATION_CAPACITIES));
    }
    for (int i = 0; i < LSU_Count; i++) {
      loadStoreUnits.add(new LoadStoreUnit(this, RESERVATION_STATION_CAPACITIES));
    }
    this.programCounterRegister = new Register(registerFileCapacity);
    this.programCounterRegister.setValue(0);
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
    return !hasReachedProgramEnd() || !decodeBuffer.isEmpty() || reorderBuffer.hasEntries()
      || reorderBuffer.getLoadStoreBuffer().hasEntries()
      || arithmeticLogicUnits.stream().anyMatch(Unit::isProcessing)
      || loadStoreUnits.stream().anyMatch(Unit::isProcessing)
      || branchUnits.stream().anyMatch(Unit::isProcessing);
  }

  public BranchTargetAddressCache getBranchTargetAddressCache() {
    return branchTargetAddressCache;
  }

  public void pushToDecodeBuffer(FetchedInstruction instruction) {
    decodeBuffer.offer(instruction);
  }

  public int getWidth() {
    return width;
  }

  public Memory getMemory() {
    return memory;
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

  public int getCorrectBranchPredictionCount() {
    return correctBranchPredictions;
  }

  public int getIncorrectBranchPredictionCount() {
    return incorrectBranchPredictions;
  }

  public RegisterFile getRegisterFile() {
    return registerFile;
  }

  public ReorderBuffer getReorderBuffer() {
    return reorderBuffer;
  }

  public TagGenerator getTagGenerator() {
    return tagGenerator;
  }

  private boolean hasReachedProgramEnd() {
    return programCounterRegister.getValue() >= parsedProgram.getInstructionCount();
  }

  public void flushPipeline() {
    arithmeticLogicUnits.forEach(Unit::flush);
    loadStoreUnits.forEach(Unit::flush);
    branchUnits.forEach(Unit::flush);
    registerFile.flush();
    reorderBuffer.flush();
    decodeBuffer.clear();
    tagGenerator.flush();
  }

  private void tickUnits() {
    arithmeticLogicUnits.forEach(Unit::tick);
    loadStoreUnits.forEach(Unit::tick);
    branchUnits.forEach(Unit::tick);
  }

  private void fetch() {
    if (!hasReachedProgramEnd()) {
      Instruction next = parsedProgram.getInstructionForLine(getProgramCounter().getValue());
      FetchedInstruction fetchedInstruction = new FetchedInstruction(next, getProgramCounter().getValue());
      pushToDecodeBuffer(fetchedInstruction);
      // int nextLine = staticBranchPredictor.predict(fetchedInstruction);
      int nextLine = dynamicBranchPredictor.predict(fetchedInstruction);
      getProgramCounter().setValue(nextLine);
      System.out.println("FETCHED INSTRUCTION: " + fetchedInstruction.toString());
    }
  }

  private void dispatchReservationStations() {
    arithmeticLogicUnits.forEach(u -> u.getReservationStation().dispatch());
    loadStoreUnits.forEach(u -> u.getReservationStation().dispatch());
    branchUnits.forEach(u -> u.getReservationStation().dispatch());
  }

  public void broadcastToReservationStations(Tag tag, int result) {
    arithmeticLogicUnits.forEach(u -> u.getReservationStation().receive(tag, result));
    loadStoreUnits.forEach(u -> u.getReservationStation().receive(tag, result));
    branchUnits.forEach(u -> u.getReservationStation().receive(tag, result));
  }

  private void decode() {
    dispatchReservationStations();
    if (!decodeBuffer.isEmpty()) {
      FetchedInstruction toIssue = decodeBuffer.peek();
      ReservationStation toIssueTo = null;

      switch(toIssue.getInstruction().getOpcode().getCategory()) {
        case ARITHMETIC:
          toIssueTo = arithmeticLogicUnits.stream().min(unitLoadComparator).get().getReservationStation();
          break;
        case MEMORY:
          toIssueTo = loadStoreUnits.stream().min(unitLoadComparator).get().getReservationStation();
          break;
        case CONTROL:
          toIssueTo = branchUnits.stream().min(unitLoadComparator).get().getReservationStation();
          break;
      }

      if (toIssueTo != null) {
        if (!toIssueTo.isFull() && !reorderBuffer.isFull()) {
          decodeBuffer.poll();

          Tag tag = tagGenerator.generateTag();
          int lineNumber = toIssue.getLineNumber();
          DecodedOperand[] decodedOperands = Arrays.stream(toIssue.getInstruction().getOperands()).map(o -> o.decode()).toArray(DecodedOperand[]::new);
          DecodedInstruction decodedInstruction = new DecodedInstruction(toIssue.getInstruction(), tag, lineNumber, decodedOperands);

          reorderBuffer.pushToTail(new ReorderBufferEntry(decodedInstruction));
          toIssueTo.issue(decodedInstruction);
          System.out.println("ISSUED INSTRUCTION: " + decodedInstruction.toString());
        } else {
          System.out.println("ISSUE BLOCKED: " + toIssue.toString());
        }
      }
    }
  }

  public void printStatus() {
    System.out.println("Re-order Buffer:");
    System.out.print(reorderBuffer.toString());
    System.out.println();

    System.out.println("Load-Store Buffer:");
    System.out.print(reorderBuffer.getLoadStoreBuffer().toString());
    System.out.println();

    System.out.println("Branch Target Address Cache:");
    System.out.print(branchTargetAddressCache.toString());
    System.out.println();

    System.out.println("Reservation Stations:");
    for (int i = 0; i < arithmeticLogicUnits.size(); i++)
      System.out.println("ALU RS " + i + " | " + arithmeticLogicUnits.get(i).getReservationStation().getStatus());
    for (int i = 0; i < branchUnits.size(); i++)
      System.out.println("BRU RS " + i + " | " + branchUnits.get(i).getReservationStation().getStatus());
    for (int i = 0; i < loadStoreUnits.size(); i++)
      System.out.println("LSU RS " + i + " | " + loadStoreUnits.get(i).getReservationStation().getStatus());
    System.out.println();

    System.out.println("Execution Units:");
    for (int i = 0; i < arithmeticLogicUnits.size(); i++)
      System.out.println("ALU " + i + " | " + arithmeticLogicUnits.get(i).getStatus());
    for (int i = 0; i < branchUnits.size(); i++)
      System.out.println("BRU " + i + " | " + branchUnits.get(i).getStatus());
    for (int i = 0; i < loadStoreUnits.size(); i++)
      System.out.println("LSU " + i + " | " + loadStoreUnits.get(i).getStatus());
    System.out.println();

    System.out.println("Registers:");
    System.out.print(registerFile.toString());
    System.out.println();

    System.out.println("Memory:");
    System.out.println(memory.toString(8));
  }

  public void incrementExecutedInstructionCount() {
    executedInstructionCount++;
  }

  public void incrementCorrectBranchPredictions() {
    correctBranchPredictions++;
  }

  public void incrementIncorrectBranchPredictions() {
    incorrectBranchPredictions++;
  }

  private void execute() {
    tickUnits();
  }

  private void writeback() {
    reorderBuffer.retire();
  }

  public void tick() {
    writeback();
    execute();
    for (int i = 0; i < width; i++)
      decode();
    for (int i = 0; i < width; i++)
      fetch();

    cycleCount++;
  }
}