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

  public List<Instruction> getInstructions() {
    return instructions;
  }

  public Optional<Integer> getErrorLine() {
    return errorLine;
  }

  public boolean hasError() {
    return errorLine.isPresent();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    instructions.stream().forEachOrdered(i -> sb.append(i.toString() + System.lineSeparator()));
    return sb.toString();
  }

}