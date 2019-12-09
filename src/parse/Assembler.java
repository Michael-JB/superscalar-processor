package parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import instruction.AddImmediateInstruction;
import instruction.AddInstruction;
import instruction.BranchEqualInstruction;
import instruction.BranchGreaterThanInstruction;
import instruction.BranchGreaterThanOrEqualInstruction;
import instruction.BranchLessThanInstruction;
import instruction.BranchLessThanOrEqualInstruction;
import instruction.BranchNotEqualInstruction;
import instruction.CompareInstruction;
import instruction.CopyInstruction;
import instruction.DivideInstruction;
import instruction.Instruction;
import instruction.JumpInstruction;
import instruction.LoadImmediateInstruction;
import instruction.LoadInstruction;
import instruction.MoveInstruction;
import instruction.MultiplyInstruction;
import instruction.NoOperationInstruction;
import instruction.Opcode;
import instruction.RegisterOperand;
import instruction.RegisterOperandCategory;
import instruction.StoreImmediateInstruction;
import instruction.StoreInstruction;
import instruction.SubtractImmediateInstruction;
import instruction.SubtractInstruction;
import instruction.ValueOperand;

public class Assembler {

  private final String charRegexWhitelist = "[^A-Za-z0-9# -:]";
  private final String commentPrefix = "#";

  public ParsedProgram parseProgramFile(String programFile) {

    List<String> sanitisedLines = getLinesFromFile(programFile).stream()
      .map(this::sanitiseLine)
      .filter(s -> !s.isEmpty())
      .collect(Collectors.toList());

    HashMap<String, Integer> labels = getLabels(sanitisedLines);

    List<String> labelsRemoved = sanitisedLines.stream()
      .map(this::removeLabel)
      .collect(Collectors.toList());

    List<Optional<Instruction>> instructions = labelsRemoved.stream()
      .map(l -> parseLine(labelsRemoved.indexOf(l), l, labels))
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

  public String removeLabel(String line) {
    if (line.contains(":")) {
      return sanitiseLine(line.split(":", 2)[1]);
    }
    return line;
  }

  public Optional<String> longestMatchingLabel(String token, HashMap<String, Integer> labels) {
    return labels.keySet().stream()
      .filter(label -> token.equals(label))
      .max(Comparator.comparing(String::length));
  }

  public String replaceLabelForRelativeNumber(int lineNumber, String token, HashMap<String, Integer> labels) {
    Optional<String> longestMatchingLabel = longestMatchingLabel(token, labels);
    if (longestMatchingLabel.isPresent()) {
      return "" + (labels.get(longestMatchingLabel.get()) - lineNumber);
    }
    return token;
  }

  public String replaceLabelForAbsoluteNumber(String token, HashMap<String, Integer> labels) {
    Optional<String> longestMatchingLabel = longestMatchingLabel(token, labels);
    if (longestMatchingLabel.isPresent()) {
      return "" + labels.get(longestMatchingLabel.get());
    }
    return token;
  }

  public HashMap<String, Integer> getLabels(List<String> lines) {
    HashMap<String, Integer> labels = new HashMap<String, Integer>();
    for (String line : lines) {
      if (line.contains(":")) {
        String label = line.split(":", 2)[0];
        labels.put(label, lines.indexOf(line));
      }
    }
    return labels;
  }

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

  private String compressConsecutiveWhitespace(String line) {
    return line.replaceAll(" +", " ").trim();
  }

  public String sanitiseLine(String line) {
    String cleanLine = compressConsecutiveWhitespace(line.replaceAll(charRegexWhitelist, ""));
    if (cleanLine.contains(commentPrefix)) {
      cleanLine = cleanLine.split(commentPrefix)[0].trim();
    }
    return cleanLine;
  }

  private Optional<RegisterOperand> parseRegisterOperand(String token, RegisterOperandCategory category) {
    if (token.matches("r[0-9]+")) {
      return Optional.of(new RegisterOperand(Integer.parseInt(token.replaceAll("[^0-9]", "")), category));
    }
    return Optional.empty();
  }

  private Optional<ValueOperand> parseValueOperand(String token) {
    if (token.matches("[0-9-]+")) {
      return Optional.of(new ValueOperand(Integer.parseInt(token.replaceAll("[^0-9-]", ""))));
    }
    return Optional.empty();
  }

  public Optional<Instruction> parseLine(int lineNumber, String line, HashMap<String, Integer> labels) {
    String[] tokens = line.split(" ");

    if (tokens.length > 0) {
      Opcode opcode;

      switch(tokens[0]) {
        case "add":
          opcode = Opcode.ADD;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new AddInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "sub":
          opcode = Opcode.SUB;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new SubtractInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "addi":
          opcode = Opcode.ADDI;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<ValueOperand> operand3 = parseValueOperand(tokens[3]);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new AddImmediateInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "subi":
          opcode = Opcode.SUBI;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<ValueOperand> operand3 = parseValueOperand(tokens[3]);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new SubtractImmediateInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "mul":
          opcode = Opcode.MUL;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new MultiplyInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "div":
          opcode = Opcode.DIV;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new DivideInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "cmp":
          opcode = Opcode.CMP;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand3 = parseRegisterOperand(tokens[3], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new CompareInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "move":
          opcode = Opcode.MOVE;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<ValueOperand> operand2 = parseValueOperand(tokens[2]);
            if (operand1.isPresent() && operand2.isPresent()) {
              return Optional.of(new MoveInstruction(operand1.get(), operand2.get()));
            }
          }
          break;
        case "copy":
          opcode = Opcode.COPY;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            if (operand1.isPresent() && operand2.isPresent()) {
              return Optional.of(new CopyInstruction(operand1.get(), operand2.get()));
            }
          }
          break;
        case "la":
          opcode = Opcode.LA;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<ValueOperand> operand3 = parseValueOperand(tokens[3]);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new LoadInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "lai":
          opcode = Opcode.LAI;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.DESTINATION);
            Optional<ValueOperand> operand2 = parseValueOperand(tokens[2]);
            if (operand1.isPresent() && operand2.isPresent()) {
              return Optional.of(new LoadImmediateInstruction(operand1.get(), operand2.get()));
            }
          }
          break;
        case "sa":
          opcode = Opcode.SA;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            Optional<ValueOperand> operand3 = parseValueOperand(tokens[3]);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new StoreInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "sai":
          opcode = Opcode.SAI;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<ValueOperand> operand2 = parseValueOperand(tokens[2]);
            if (operand1.isPresent() && operand2.isPresent()) {
              return Optional.of(new StoreImmediateInstruction(operand1.get(), operand2.get()));
            }
          }
          break;
        case "beq":
          opcode = Opcode.BEQ;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchEqualInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "bne":
          opcode = Opcode.BNE;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchNotEqualInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "bgt":
          opcode = Opcode.BGT;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchGreaterThanInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "bge":
          opcode = Opcode.BGE;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchGreaterThanOrEqualInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "blt":
          opcode = Opcode.BLT;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchLessThanInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "ble":
          opcode = Opcode.BLE;
          if (tokens.length > opcode.getOperandCount()) {
            Optional<RegisterOperand> operand1 = parseRegisterOperand(tokens[1], RegisterOperandCategory.SOURCE);
            Optional<RegisterOperand> operand2 = parseRegisterOperand(tokens[2], RegisterOperandCategory.SOURCE);
            String potentialLabel = replaceLabelForRelativeNumber(lineNumber, tokens[3], labels);
            Optional<ValueOperand> operand3 = parseValueOperand(potentialLabel);
            if (operand1.isPresent() && operand2.isPresent() && operand3.isPresent()) {
              return Optional.of(new BranchLessThanOrEqualInstruction(operand1.get(), operand2.get(), operand3.get()));
            }
          }
          break;
        case "jmp":
          opcode = Opcode.JMP;
          if (tokens.length > opcode.getOperandCount()) {
            String potentialLabel = replaceLabelForAbsoluteNumber(tokens[1], labels);
            Optional<ValueOperand> operand1 = parseValueOperand(potentialLabel);
            if (operand1.isPresent()) {
              return Optional.of(new JumpInstruction(operand1.get()));
            }
          }
          break;
        case "nop":
          opcode = Opcode.NOP;
          if (tokens.length > opcode.getOperandCount()) {
            return Optional.of(new NoOperationInstruction());
          }
          break;
        default:
          break;
      }
    }

    return Optional.empty();
  }

}