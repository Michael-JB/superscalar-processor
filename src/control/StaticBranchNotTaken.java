package control;

public class StaticBranchNotTaken extends StaticBranchPredictor {

  @Override
  protected boolean shouldTakeBranch(int targetLine, int instructionLine) {
    return false;
  }

}