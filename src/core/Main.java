package core;

import instruction.Instruction;

import java.io.File;
import java.util.List;

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

    List<Instruction> instructions = assembler.parseProgramFile(programFileName);
    instructions.stream().forEach(i -> i.execute());
  }

}