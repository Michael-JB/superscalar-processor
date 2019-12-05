package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import instruction.Instruction;
import instruction.Tag;
import instruction.TagGenerator;
import memory.Register;
import memory.RegisterFile;
import memory.ReorderBuffer;
import memory.ReorderBufferEntry;
import memory.ReservationStation;
import parse.ParsedProgram;
import unit.ArithmeticLogicUnit;
import unit.BranchUnit;
import unit.LoadStoreUnit;
import unit.UnitLoadComparator;

public class Processor { /* CONSTRAINT: Currently only works if all instructions have latency 1 */

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

  private final TagGenerator tagGenerator = new TagGenerator();

  private final ReorderBuffer reorderBuffer = new ReorderBuffer(this);

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

  public ReorderBuffer getReorderBuffer() {
    return reorderBuffer;
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
      Instruction fetchedInstruction = parsedProgram.getInstructions().get(getProgramCounter().getValue());
      pushToDecodeBuffer(fetchedInstruction);
      getProgramCounter().setValue(getProgramCounter().getValue() + 1);
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
      Instruction toIssue = decodeBuffer.peek();
      ReservationStation toIssueTo = null;

      switch(toIssue.getOpcode().getCategory()) {
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
          toIssue.setTag(tagGenerator.generateTag());
          toIssueTo.issue(toIssue);
          reorderBuffer.pushToTail(new ReorderBufferEntry(toIssue));
          System.out.println("ISSUED INSTRUCTION: " + toIssue.toString());
        } else {
          System.out.println("ISSUE BLOCKED: " + toIssue.toString());
        }
      }
    }
  }

  public void printStatus() {
    System.out.println("Re-order Buffer:");
    System.out.println(reorderBuffer.getStatus());
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
  }

  public void incrementExecutedInstructionCount() {
    executedInstructionCount++;
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
    decode();
    fetch();

    cycleCount++;
  }
}