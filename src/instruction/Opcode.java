package instruction;

public enum Opcode {

  ADD(3),
  SUB(3),
  ADDI(3),
  SUBI(3),
  MUL(3),
  DIV(3),
  CMP(3);

  private final String instructionPrefix;
  private final int operandCount;

  Opcode(int operandCount) {
    this.instructionPrefix = super.toString().toLowerCase();
    this.operandCount = operandCount;
  }

  public String getInstructionPrefix() {
    return instructionPrefix;
  }

  public int getOperandCount() {
    return operandCount;
  }

  @Override
  public String toString() {
    return getInstructionPrefix();
  }

}