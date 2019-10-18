package instruction;

public enum Opcode {

  /* Arithmetic */
  ADD(3),  /* add  r1 r2 r3 = r1 <- r2 + r3 */
  SUB(3),  /* sub  r1 r2 r3 = r1 <- r2 - r3 */
  ADDI(3), /* addi r1 r2 v1 = r1 <- r2 + v1 */
  SUBI(3), /* subi r1 r2 v1 = r1 <- r2 - v1 */
  MUL(3),  /* mul  r1 r2 r3 = r1 <- r2 * r3 */
  DIV(3),  /* div  r1 r2 r3 = r1 <- r2 / r3 */
  CMP(3),  /* cmp  r1 r2 r3 = r1 <- {-1 if r2 < r3, 0 if r2 = r3, 1 if r2 > r3} */
  MOVE(2), /* move r1 v1    = r1 <- v1 */

  /* Memory */
  LD(3),   /* ld   r1 r2 v1 = r1 <- [r2 + v1] */
  LDI(2),  /* ldi  r1 v1    = r1 <- [v1] */

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