package instruction;

public class BranchLessThanOrEqualInstruction extends Instruction {

  public BranchLessThanOrEqualInstruction(RegisterOperand srcA, RegisterOperand srcB, ValueOperand off) {
    super(Opcode.BLE, srcA, srcB, off);
  }

  @Override
  public int eval(ValueOperand... values) {
    return values[0].getValue() <= values[1].getValue() ? values[2].getValue() : 0;
  }

}