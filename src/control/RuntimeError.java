package control;

public class RuntimeError {

  private final String message;

  public RuntimeError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "Runtime Error: " + getMessage();
  }

}