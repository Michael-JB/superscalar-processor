package control;

public class BranchError extends RuntimeError {

  public BranchError(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return "Branch Error: " + getMessage();
  }

}