package control;

public class LoadStoreError extends RuntimeError {

  public LoadStoreError(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return "LoadStore Error: " + getMessage();
  }

}