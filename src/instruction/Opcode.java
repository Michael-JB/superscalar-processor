package instruction;

public enum Opcode {

  /* Arithmetic */
  ADD(OpcodeCategory.ARITHMETIC, 3),  /* add  r1 r2 r3 = r1 <- r2 + r3 */
  SUB(OpcodeCategory.ARITHMETIC, 3),  /* sub  r1 r2 r3 = r1 <- r2 - r3 */
  ADDI(OpcodeCategory.ARITHMETIC, 3), /* addi r1 r2 v1 = r1 <- r2 + v1 */
  SUBI(OpcodeCategory.ARITHMETIC, 3), /* subi r1 r2 v1 = r1 <- r2 - v1 */
  MUL(OpcodeCategory.ARITHMETIC, 3),  /* mul  r1 r2 r3 = r1 <- r2 * r3 */
  DIV(OpcodeCategory.ARITHMETIC, 3),  /* div  r1 r2 r3 = r1 <- r2 / r3 */
  CMP(OpcodeCategory.ARITHMETIC, 3),  /* cmp  r1 r2 r3 = r1 <- {-1 if r2 < r3, 0 if r2 = r3, 1 if r2 > r3} */
  MOVE(OpcodeCategory.ARITHMETIC, 2), /* move r1 v1    = r1 <- v1 */

  /* Memory */
  LA(OpcodeCategory.MEMORY, 3),       /* la   r1 r2 v1 = r1 <- [r2 + v1] */
  LAI(OpcodeCategory.MEMORY, 2),      /* lai  r1 v1    = r1 <- [v1] */
  SA(OpcodeCategory.MEMORY, 3),       /* sa   r1 r2 v1 = [r2 + v1] <- r1 */
  SAI(OpcodeCategory.MEMORY, 2),      /* sai  r1 v1    = [v1] <- r1 */

  /* Control */
  BEQ(OpcodeCategory.CONTROL, 3),     /* beq  r1 r2 v1 = PC -= {v1 if r1 = r2, 0 if r1 != r2} */
  BNE(OpcodeCategory.CONTROL, 3),     /* bne  r1 r2 v1 = PC -= {0 if r1 = r2, v1 if r1 != r2} */
  JMP(OpcodeCategory.CONTROL, 1),     /* jmp v1        = PC <- v1 */
  JMPR(OpcodeCategory.CONTROL, 1),    /* jmpr r1       = PC <- r1 */
  ;

  private final String instructionPrefix;
  private final int operandCount;
  private final OpcodeCategory category;

  Opcode(OpcodeCategory category, int operandCount) {
    this.instructionPrefix = super.toString().toLowerCase();
    this.operandCount = operandCount;
    this.category = category;
  }

  public String getInstructionPrefix() {
    return instructionPrefix;
  }

  public int getOperandCount() {
    return operandCount;
  }

  public OpcodeCategory getCategory() {
    return category;
  }

  @Override
  public String toString() {
    return getInstructionPrefix();
  }

}