package core;

import java.io.File;
import java.util.Scanner;

import parse.Assembler;
import parse.ParsedProgram;

public class Main {

  private final static String defaultProgramFileName = "./programs/add.asm";

  private static void log(String message) {
    System.out.println("------------------------------ " + message);
  }

  public static void main(String[] args) {

    String programFileName = defaultProgramFileName;
    boolean interactiveMode = false;

    if (args.length > 0) {
      File programFile = new File(args[0]);
      if (programFile.exists() && !programFile.isDirectory()) {
        programFileName = args[0];
      } else {
        System.out.println("Invalid program file specified. Using default program file: " + defaultProgramFileName);
      }
      if (args.length > 1) {
        interactiveMode = args[1].equals("true");
      }
    } else {
      System.out.println("No program file specified. Using default program file: " + defaultProgramFileName);
    }

    Assembler assembler = new Assembler();
    ParsedProgram parsedProgram = assembler.parseProgramFile(programFileName);

    log("PROGRAM INSTRUCTIONS START");
    System.out.print(parsedProgram.toString());
    log("PROGRAM INSTRUCTIONS END");

    if (!parsedProgram.hasError()) {
      Processor processor = new Processor(parsedProgram, 4, 16, 32);

      if (interactiveMode) {
        Scanner scanner = new Scanner(System.in);
        log("PROGRAM STEP START");
        while (processor.step()) {
          System.out.println();
          processor.printStatus();
          scanner.nextLine();
          log("PROGRAM STEP");
        }
        scanner.close();
      } else {
        log("PROGRAM RUN START");
        processor.run();
        log("PROGRAM RUN END");
      }

      log("PROCESSOR FINAL STATUS START");
      processor.printStatus();
      log("PROCESSOR FINAL STATUS END");

      int instructionCount = parsedProgram.getInstructionCount();
      int executedInstructionCount = processor.getExecutedInstructionCount();
      int cycles = processor.getCycleCount();
      int correctBranchPredictions = processor.getCorrectBranchPredictionCount();
      int incorrectBranchPredictions = processor.getIncorrectBranchPredictionCount();
      float cyclesPerInstruction = (float) cycles / (float) executedInstructionCount;
      float instructionsPerCycle = (float) executedInstructionCount / (float) cycles;
      float correctBranchPredictionRate = (float) (100 * correctBranchPredictions) / (incorrectBranchPredictions + correctBranchPredictions);

      log("ANALYTICS START");
      System.out.println("Program instructions: " + instructionCount);
      System.out.println("Executed instructions: " + executedInstructionCount);
      System.out.println("Cycles taken: " + cycles);
      System.out.println("Branches correctly predicted: " + correctBranchPredictions);
      System.out.println("Branches incorrectly predicted: " + incorrectBranchPredictions);
      System.out.println("Correct branch prediction rate: " +  String.format("%.1f", correctBranchPredictionRate) + "%");
      System.out.println("Cycles per instruction: " + String.format("%.2f", cyclesPerInstruction));
      System.out.println("Instructions per cycle: " +  String.format("%.2f", instructionsPerCycle));
      log("ANALYTICS END");
    } else {
      System.out.println("Could not parse line " + parsedProgram.getErrorLine().get() + " of program file.");
    }
  }

}