package instruction;

public class NoOperationInstruction extends Instruction {

  public NoOperationInstruction() {
    super(Opcode.NOP);
  }

  @Override
  public int eval() {
    return 0;
  }

}