package instruction;

import java.util.Optional;

public class EvaluatedInstruction {

  private final DecodedInstruction decodedInstruction;
  private final Optional<Integer> result;

  public EvaluatedInstruction(DecodedInstruction decodedInstruction, Optional<Integer> result) {
    this.decodedInstruction = decodedInstruction;
    this.result = result;
  }

  public DecodedInstruction getDecodedInstruction() {
    return decodedInstruction;
  }

  public Optional<Integer> getResult() {
    return result;
  }

}