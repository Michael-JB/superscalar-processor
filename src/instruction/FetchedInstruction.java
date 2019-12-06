package instruction;

public class FetchedInstruction {

  protected final Instruction instruction;
  protected final int lineNumber;

  public FetchedInstruction(Instruction instruction, int lineNumber) {
    this.instruction = instruction;
    this.lineNumber = lineNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  @Override
  public String toString() {
    return instruction.toString();
  }

}