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

    System.out.println("--- PROGRAM START ---");
    System.out.print(parsedProgram.toString());
    System.out.println("--- PROGRAM END   ---");

    if (!parsedProgram.hasError()) {
      Processor processor = new Processor(parsedProgram, 16);
      processor.run();
      /* Dump register status */
      System.out.print(processor.getRegisterFile().toString());
    } else {
      System.out.println("Could not parse line " + parsedProgram.getErrorLine().get() + " of program file.");
    }
  }

}