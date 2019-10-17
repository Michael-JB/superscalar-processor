package parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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

  public ParsedProgram parseProgramFile(String programFile) {
    List<Optional<Instruction>> instructions = getLinesFromFile(programFile).stream()
      .map(this::sanitiseLine)
      .filter(s -> !s.isEmpty())
      .map(this::parseLine)
      .collect(Collectors.toList());

    if (instructions.stream().allMatch(Optional::isPresent)) {
      return new ParsedProgram(instructions.stream()
        .map(Optional::get)
        .collect(Collectors.toList()));
    } else {
      return new ParsedProgram(Collections.emptyList(), 1 + instructions.indexOf(instructions.stream()
        .filter(i -> !i.isPresent())
        .findFirst().get()));
    }
  }

  private List<String> getLinesFromFile(String fileName) {
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

  private String compressConsecutiveWhitespace(String line) {
    return line.replaceAll(" +", " ").trim();
  }

  private String sanitiseLine(String line) {
    String cleanLine = compressConsecutiveWhitespace(line.replaceAll(charRegexWhitelist, ""));
    if (cleanLine.contains(commentPrefix)) {
      cleanLine = cleanLine.split(commentPrefix)[0].trim();
    }
    return cleanLine;
  }

  private Optional<RegisterOperand> parseRegisterOperand(String token) {
    if (token.matches("r[0-9]+")) {
      return Optional.of(new RegisterOperand(evaluateToken(token)));
    }
    return Optional.empty();
  }

  private Optional<ValueOperand> parseValueOperand(String token) {
    if (token.matches("[0-9]+")) {
      return Optional.of(new ValueOperand(evaluateToken(token)));
    }
    return Optional.empty();
  }

  private int evaluateToken(String token) {
    return Integer.parseInt(token.replaceAll("[^0-9]", ""));
  }

  private Optional<Instruction> parseLine(String line) {
    String[] tokens = line.split(" ");

    if (tokens.length > 0) {
      Opcode opcode;

      switch(tokens[0]) {
        case "add":
          opcode = Opcode.ADD;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1]);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2]);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3]);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of((Instruction) new AddInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "move":
          opcode = Opcode.MOVE;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1]);
            Optional<ValueOperand> operand2 = parseValueOperand(tokens[2]);
            if (operand1.isPresent() && operand2.isPresent()) {
              return Optional.of((Instruction) new MoveInstruction(operand1.get(), operand2.get()));
            }
          }
          break;
        default:
          break;
      }
    }

    return Optional.empty();
  }

}