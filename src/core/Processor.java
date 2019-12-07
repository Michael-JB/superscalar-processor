package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import control.BranchPredictor;
import control.RuntimeError;
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

  private final ParsedProgram parsedProgram;
  private final ProcessorConfiguration processorConfiguration;
  private final Memory memory;
  private final RegisterFile registerFile;
  private final Register programCounterRegister;
  private final ReorderBuffer reorderBuffer;
  private final BranchPredictor branchPredictor;

  private final List<BranchUnit> branchUnits = new ArrayList<BranchUnit>();
  private final List<ArithmeticLogicUnit> arithmeticLogicUnits = new ArrayList<ArithmeticLogicUnit>();
  private final List<LoadStoreUnit> loadStoreUnits = new ArrayList<LoadStoreUnit>();

  private final Queue<FetchedInstruction> decodeBuffer = new LinkedList<FetchedInstruction>();

  private final UnitLoadComparator unitLoadComparator = new UnitLoadComparator();
  private final TagGenerator tagGenerator = new TagGenerator();

  private Optional<RuntimeError> runtimeError = Optional.empty();
  private int cycleCount = 0, executedInstructionCount = 0, correctBranchPredictions = 0, incorrectBranchPredictions = 0;

  public Processor(ParsedProgram parsedProgram, ProcessorConfiguration processorConfiguration) {
    this.parsedProgram = parsedProgram;
    this.processorConfiguration = processorConfiguration;
    this.registerFile = new RegisterFile(processorConfiguration.getRegisterFileCapacity());
    this.memory = new Memory(processorConfiguration.getMemoryCapacity());
    this.reorderBuffer = new ReorderBuffer(this, processorConfiguration.getReorderBufferCapacity(), processorConfiguration.getLoadStoreBufferCapacity());
    this.branchPredictor = processorConfiguration.getBranchPredictorType().getBranchPredictor();
    this.programCounterRegister = new Register(processorConfiguration.getRegisterFileCapacity());
    this.programCounterRegister.setValue(0);

    for (int i = 0; i < processorConfiguration.getALUCount(); i++)
      arithmeticLogicUnits.add(new ArithmeticLogicUnit(this, processorConfiguration.getReservationStationCapacities()));
    for (int i = 0; i < processorConfiguration.getBUCount(); i++)
      branchUnits.add(new BranchUnit(this, processorConfiguration.getReservationStationCapacities()));
    for (int i = 0; i < processorConfiguration.getLSUCount(); i++)
      loadStoreUnits.add(new LoadStoreUnit(this, processorConfiguration.getReservationStationCapacities()));
  }

  public void run() {
    while(!hasRuntimeError() && isProcessing()) {
      tick();
    }
  }

  public boolean step() {
    if (!hasRuntimeError() && isProcessing()) {
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

  public BranchPredictor getBranchPredictor() {
    return branchPredictor;
  }

  public void pushToDecodeBuffer(FetchedInstruction instruction) {
    decodeBuffer.offer(instruction);
  }

  public ProcessorConfiguration getProcessorConfiguration() {
    return processorConfiguration;
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

  public Optional<RuntimeError> getRuntimeError() {
    return runtimeError;
  }

  public boolean hasRuntimeError() {
    return runtimeError.isPresent();
  }

  public void raiseRuntimeError(RuntimeError runtimeError) {
    System.out.println("RUNTIME ERROR: " + runtimeError.toString());
    this.runtimeError = Optional.of(runtimeError);
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

  public void printStatus() {
    System.out.println("Re-order Buffer:");
    System.out.print(reorderBuffer.toString());
    System.out.println();

    System.out.println("Load-Store Buffer:");
    System.out.print(reorderBuffer.getLoadStoreBuffer().toString());
    System.out.println();

    System.out.println("Branch Target Address Cache:");
    System.out.print(getBranchPredictor().getBranchTargetAddressCache().toString());
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

  private void fetch() {
    if (!hasReachedProgramEnd()) {
      Instruction next = parsedProgram.getInstructionForLine(getProgramCounter().getValue());
      FetchedInstruction fetchedInstruction = new FetchedInstruction(next, getProgramCounter().getValue());
      pushToDecodeBuffer(fetchedInstruction);
      int nextLine = getBranchPredictor().predict(fetchedInstruction);
      getProgramCounter().setValue(nextLine);
      System.out.println("FETCHED INSTRUCTION: " + fetchedInstruction.toString());
    }
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

  private void execute() {
    tickUnits();
  }

  private void writeback() {
    reorderBuffer.retire();
  }

  public void tick() {
    writeback();
    execute();
    for (int i = 0; i < processorConfiguration.getWidth(); i++)
      decode();
    for (int i = 0; i < processorConfiguration.getWidth(); i++)
      fetch();

    cycleCount++;
  }
}