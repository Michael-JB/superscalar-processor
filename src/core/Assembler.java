package core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import instruction.AddInstruction;
import instruction.Instruction;
import instruction.MoveInstruction;
import instruction.Opcode;
import instruction.RegisterOperand;
import instruction.ValueOperand;

public class Assembler {

  private final String charRegexWhitelist = "[^A-Za-z0-9# ]";
  private final String commentPrefix = "#";

  public Assembler() {}

  public List<String> getLinesFromFile(String fileName) {
    Path path = Paths.get(fileName);
    List<String> lines = new ArrayList<String>();
    try {
      lines = Files.readAllLines(path, StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.out.println("Unable to read from file: " + fileName);
      e.printStackTrace();
    }
    return lines;
  }

  public List<Instruction> parseProgramFile(String programFile) {
    List<Instruction> parsedLines = getLinesFromFile(programFile).stream()
      .map(String::trim)
      .map(s -> sanitiseLine(s))
      .map(s -> parseLine(s))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
    return parsedLines;
  }

  public String sanitiseLine(String line) {
    String cleanLine = line.replaceAll(charRegexWhitelist, "");
    if (cleanLine.contains(commentPrefix)) {
      cleanLine = cleanLine.split(commentPrefix)[0];
    }
    return cleanLine;
  }

  public Optional<Instruction> parseLine(String line) {

    String[] tokens = line.split(" ");

    if (tokens.length > 0) {

      Opcode opcode;

      switch(tokens[0]) {
        case "add":
          opcode = Opcode.ADD;
          if (tokens.length > opcode.getOperandCount()) {
            return Optional.of((Instruction) new AddInstruction(new RegisterOperand(1), new RegisterOperand(1), new RegisterOperand(1)));
          }
          break;
        case "move":
          opcode = Opcode.MOVE;
          if (tokens.length > opcode.getOperandCount()) {
            return Optional.of((Instruction) new MoveInstruction(new RegisterOperand(1), new ValueOperand(1)));
          }
          break;
        default:
          break;
      }

    }

    return Optional.empty();
  }

}