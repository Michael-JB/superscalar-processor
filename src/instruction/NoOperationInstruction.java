package instruction;

public class NoOperationInstruction extends Instruction {

  public NoOperationInstruction() {
    super(Opcode.NOP);
  }

  @Override
  public int evaluate(DecodedOperand... operands) {
    return 0;
  }

}