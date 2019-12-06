package instruction;

public enum Opcode {

  /* Arithmetic */
  ADD (OpcodeCategory.ARITHMETIC, 3, 1), /* add  r1 r2 r3 = r1 <- r2 + r3 */
  SUB (OpcodeCategory.ARITHMETIC, 3, 1), /* sub  r1 r2 r3 = r1 <- r2 - r3 */
  ADDI(OpcodeCategory.ARITHMETIC, 3, 1), /* addi r1 r2 v1 = r1 <- r2 + v1 */
  SUBI(OpcodeCategory.ARITHMETIC, 3, 1), /* subi r1 r2 v1 = r1 <- r2 - v1 */
  MUL (OpcodeCategory.ARITHMETIC, 3, 2), /* mul  r1 r2 r3 = r1 <- r2 * r3 */
  DIV (OpcodeCategory.ARITHMETIC, 3, 8), /* div  r1 r2 r3 = r1 <- r2 / r3 */
  CMP (OpcodeCategory.ARITHMETIC, 3, 1), /* cmp  r1 r2 r3 = r1 <- {-1 if r2 < r3, 0 if r2 = r3, 1 if r2 > r3} */
  MOVE(OpcodeCategory.ARITHMETIC, 2, 1), /* move r1 v1    = r1 <- v1 */

  /* Memory */
  LA  (OpcodeCategory.MEMORY, 3, 2),     /* la   r1 r2 v1 = r1 <- [r2 + v1] */
  LAI (OpcodeCategory.MEMORY, 2, 2),     /* lai  r1 v1    = r1 <- [v1] */
  SA  (OpcodeCategory.MEMORY, 3, 1),     /* sa   r1 r2 v1 = [r2 + v1] <- r1 */
  SAI (OpcodeCategory.MEMORY, 2, 1),     /* sai  r1 v1    = [v1] <- r1 */

  /* Control */
  BEQ (OpcodeCategory.CONTROL, 3, 1),    /* beq  r1 r2 v1 = PC += {v1 if r1 = r2, 0 otherwise} */
  BNE (OpcodeCategory.CONTROL, 3, 1),    /* bne  r1 r2 v1 = PC += {v1 if r1 != r2, 0 otherwise} */
  BGT (OpcodeCategory.CONTROL, 3, 1),    /* bgt  r1 r2 v1 = PC += {v1 if r1 > r2, 0 otherwise} */
  BGE (OpcodeCategory.CONTROL, 3, 1),    /* bge  r1 r2 v1 = PC += {v1 if r1 >= r2, 0 otherwise} */
  BLT (OpcodeCategory.CONTROL, 3, 1),    /* blt  r1 r2 v1 = PC += {v1 if r1 < r2, 0 otherwise} */
  BLE (OpcodeCategory.CONTROL, 3, 1),    /* ble  r1 r2 v1 = PC += {v1 if r1 <= r2, 0 otherwise} */
  JMP (OpcodeCategory.CONTROL, 1, 1),    /* jmp v1        = PC <- v1 */
  NOP (OpcodeCategory.CONTROL, 0, 1),    /* nop           = Do nothing */
  ;

  private final String instructionPrefix;
  private final OpcodeCategory category;
  private final int operandCount;
  private final int latency;

  Opcode(OpcodeCategory category, int operandCount, int latency) {
    this.instructionPrefix = super.toString().toLowerCase();
    this.operandCount = operandCount;
    this.category = category;
    this.latency = latency;
  }

  public String getInstructionPrefix() {
    return instructionPrefix;
  }

  public int getOperandCount() {
    return operandCount;
  }

  public int getLatency() {
    return latency;
  }

  public OpcodeCategory getCategory() {
    return category;
  }

  @Override
  public String toString() {
    return getInstructionPrefix();
  }

}