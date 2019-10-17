package instruction;

public enum Opcode {

  /* Arithmetic */
  ADD(3),
  SUB(3),
  ADDI(3),
  SUBI(3),
  MUL(3),
  DIV(3),
  CMP(3),

  /* Memory */
  MOVE(2),
  LD(3),
  LDI(2),

  /* ... */
  ;

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