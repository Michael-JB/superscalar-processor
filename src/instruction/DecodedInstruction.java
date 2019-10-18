package instruction;

public class DecodedInstruction {

  private final Instruction encodedInstruction;
  private final ValueOperand[] decodedOperands;

  public DecodedInstruction(Instruction encodedInstruction, ValueOperand... decodedOperands) {
    this.encodedInstruction = encodedInstruction;
    this.decodedOperands = decodedOperands;

    if (decodedOperands.length != encodedInstruction.getOpcode().getOperandCount()) {
      throw new IllegalArgumentException(
        "Invalid decoded operand count. " + decodedOperands.length + " provided, " + encodedInstruction.getOpcode().getOperandCount() + " required.");
    }
  }

  public Instruction getEncodedInstruction() {
    return encodedInstruction;
  }

  public ValueOperand[] getDecodedOperands() {
    return decodedOperands;
  }

  public int execute() {
    return encodedInstruction.execute(decodedOperands);
  }

}