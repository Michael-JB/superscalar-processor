package control;

public class ArithmeticError extends RuntimeError {

  public ArithmeticError(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return "Arithmetic Error: " + getMessage();
  }

}