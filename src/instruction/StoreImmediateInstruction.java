package instruction;

public class StoreImmediateInstruction extends Instruction {

  public StoreImmediateInstruction(RegisterOperand val, ValueOperand addr) {
    super(Opcode.SAI, new Operand[] { val, addr });
  }

  @Override
  public int perform(ValueOperand... values) {
    return values[1].getValue();
  }

}