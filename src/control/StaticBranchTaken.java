package control;

public class StaticBranchTaken extends StaticBranchPredictor {

  @Override
  protected boolean shouldTakeBranch(int targetLine, int instructionLine) {
    return true;
  }

}