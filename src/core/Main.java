package core;

import java.io.File;
import java.util.Scanner;

import parse.Assembler;
import parse.ParsedProgram;

public class Main {

  private final static String defaultProgramFileName = "./programs/add.asm";

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
    String outputSep = "------------------------------ ";

    System.out.println(outputSep + "PROGRAM INSTRUCTIONS START");
    System.out.print(parsedProgram.toString());
    System.out.println(outputSep + "PROGRAM INSTRUCTIONS END");

    if (!parsedProgram.hasError()) {
      Processor processor = new Processor(parsedProgram, 16, 100);

      if (interactiveMode) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(outputSep + "PROGRAM STEP START");
        while (processor.step()) {
          System.out.println();
          processor.printStatus();
          scanner.nextLine();
          System.out.println(outputSep + "PROGRAM STEP");
        }
        scanner.close();
      } else {
        System.out.println(outputSep + "PROGRAM RUN START");
        processor.run();
        System.out.println(outputSep + "PROGRAM RUN END");
      }

      System.out.println(outputSep + "PROCESSOR STATUS START");
      processor.printStatus();
      System.out.println(outputSep + "PROCESSOR STATUS END");

      int instructionCount = parsedProgram.getInstructionCount();
      int executedInstructionCount = processor.getExecutedInstructionCount();
      int cycles = processor.getCycleCount();
      float cyclesPerInstruction = (float) cycles / (float) executedInstructionCount;
      float instructionsPerCycle = (float) executedInstructionCount / (float) cycles;

      System.out.println(outputSep + "ANALYTICS START");
      System.out.println("Program instructions: " + instructionCount);
      System.out.println("Executed instructions: " + executedInstructionCount);
      System.out.println("Cycles taken: " + cycles);
      System.out.println("Cycles per instruction: " + cyclesPerInstruction);
      System.out.println("Instructions per cycle: " + instructionsPerCycle);
      System.out.println(outputSep + "ANALYTICS END");
    } else {
      System.out.println("Could not parse line " + parsedProgram.getErrorLine().get() + " of program file.");
    }
  }

}