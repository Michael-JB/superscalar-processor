package instruction;

public class StoreImmediateInstruction extends Instruction {

  public StoreImmediateInstruction(RegisterOperand val, ValueOperand addr) {
    super(Opcode.SAI, val, addr);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[1].getValue();
  }

}