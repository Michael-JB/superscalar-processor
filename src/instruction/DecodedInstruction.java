package instruction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import control.RuntimeError;

/* A stateful wrapper around instructions */
public class DecodedInstruction {

  protected final Instruction instruction;
  protected final DecodedOperand[] operands;
  protected final Tag tag;
  protected final int lineNumber;
  protected Optional<Integer> executionResult = Optional.empty();
  protected Optional<Integer> branchTarget = Optional.empty();
  protected Optional<Boolean> branchTaken = Optional.empty();
  protected Optional<RuntimeError> error = Optional.empty();
  protected InstructionStatus instructionStatus = InstructionStatus.PENDING;

  public DecodedInstruction(Instruction instruction, Tag tag, int lineNumber, DecodedOperand... decodedOperands) {
    this.instruction = instruction;
    this.operands = decodedOperands;
    this.tag = tag;
    this.lineNumber = lineNumber;

    if (operands.length != instruction.getOpcode().getOperandCount()) {
      throw new IllegalArgumentException(
        "Invalid operand count. " + operands.length + " provided, " + instruction.getOpcode().getOperandCount() + " required.");
    }
  }

  public DecodedOperand[] getOperands() {
    return operands;
  }

  public Instruction getInstruction() {
    return instruction;
  }

  public void setInstructionStatus(InstructionStatus newStatus) {
    this.instructionStatus = newStatus;
  }

  public void setBranchTaken(boolean taken) {
    this.branchTaken = Optional.of(taken);
  }

  public void setBranchTarget(int target) {
    this.branchTarget = Optional.of(target);
  }

  public Optional<Boolean> getBranchTaken() {
    return branchTaken;
  }

  public Optional<Integer> getBranchTarget() {
    return branchTarget;
  }

  public Optional<RuntimeError> getRuntimeError() {
    return error;
  }

  public void raiseRuntimeError(RuntimeError error) {
    this.error = Optional.of(error);
  }

  public InstructionStatus getInstructionStatus() {
    return instructionStatus;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public Tag getTag() {
    return tag;
  }

  public Optional<Integer> getExecutionResult() {
    return executionResult;
  }

  public void setExecutionResult(int result) {
    this.executionResult = Optional.of(result);
  }

  public boolean isReady() {
    return Arrays.stream(operands).allMatch(o -> o.isReady());
  }

  public Optional<DecodedRegisterOperand> getDestinationRegister() {
    Optional<DecodedRegisterOperand> destinationRegister = Arrays.stream(operands)
      .filter(o -> o instanceof DecodedRegisterOperand)
      .map(o -> (DecodedRegisterOperand) o)
      .filter(o -> ((RegisterOperand) o.getEncodedOperand()).getCategory().equals(RegisterOperandCategory.DESTINATION))
      .findFirst();
    return destinationRegister;
  }

  public List<DecodedRegisterOperand> getSourceRegisters() {
    List<DecodedRegisterOperand> sourceRegisters = Arrays.stream(operands)
      .filter(o -> o instanceof DecodedRegisterOperand)
      .map(o -> (DecodedRegisterOperand) o)
      .filter(o -> ((RegisterOperand) o.getEncodedOperand()).getCategory().equals(RegisterOperandCategory.SOURCE))
      .collect(Collectors.toList());
    return sourceRegisters;
  }

  public Optional<Integer> evaluate() {
    if (isReady()) {
      return instruction.evaluate(operands);
    } else {
      throw new RuntimeException("Cannot evaluate instruction; operands not ready");
    }
  }

  @Override
  public String toString() {
    return instruction.toString() + (tag != null ? " (" + tag + ")" : "");
  }

}