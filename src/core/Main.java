package core;

import java.io.File;

import parse.Assembler;
import parse.ParsedProgram;

public class Main {

  private final static String defaultProgramFileName = "./programs/add.asm";

  public static void main(String[] args) {

    String programFileName = defaultProgramFileName;

    if (args.length > 0) {
      File programFile = new File(args[0]);
      if (programFile.exists() && !programFile.isDirectory()) {
        programFileName = args[0];
      } else {
        System.out.println("Invalid program file specified. Using default program file: " + defaultProgramFileName);
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
      Processor processor = new Processor(parsedProgram, 8);
      processor.run();
      /* Dump register status */
      System.out.println(outputSep + "REGISTER STATUS START");
      System.out.print(processor.getRegisterFile().toString());
      System.out.println(outputSep + "REGISTER STATUS END");
      int instructionCount = parsedProgram.getInstructionCount();
      int executedInstructionCount = processor.getExecutedInstructionCount();
      int cycles = processor.getCycleCount();
      float cyclesPerInstruction = cycles / executedInstructionCount;
      System.out.println(outputSep + "ANALYTICS START");
      System.out.println("Program instructions: " + instructionCount);
      System.out.println("Executed instructions: " + executedInstructionCount);
      System.out.println("Cycles taken: " + cycles);
      System.out.println("Cycles per instruction: " + cyclesPerInstruction);
      System.out.println(outputSep + "ANALYTICS END");
    } else {
      System.out.println("Could not parse line " + parsedProgram.getErrorLine().get() + " of program file.");
    }
  }

}