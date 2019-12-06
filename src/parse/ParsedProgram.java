package parse;

import java.util.List;
import java.util.Optional;

import instruction.Instruction;

public class ParsedProgram {

  private final List<Instruction> instructions;
  private final Optional<Integer> errorLine;

  public ParsedProgram(List<Instruction> instructions, int errorLine) {
    this.instructions = instructions;
    this.errorLine = Optional.of(errorLine);
  }

  public ParsedProgram(List<Instruction> instructions) {
    this.instructions = instructions;
    this.errorLine = Optional.empty();
  }

  public int getInstructionCount() {
    return instructions.size();
  }

  public List<Instruction> getInstructions() {
    return instructions;
  }

  public Optional<Integer> getErrorLine() {
    return errorLine;
  }

  public boolean hasError() {
    return errorLine.isPresent();
  }

  public Instruction getInstructionForLine(int line) {
    if (line >= 0 && line < instructions.size()) {
      return instructions.get(line);
    } else {
      throw new IllegalArgumentException("Line number out of program bounds");
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    instructions.stream().forEachOrdered(i -> sb.append(i.toString() + System.lineSeparator()));
    return sb.toString();
  }

}