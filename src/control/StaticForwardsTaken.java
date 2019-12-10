package control;

public class StaticForwardsTaken extends StaticBranchPredictor {

  @Override
  protected boolean shouldTakeBranch(int targetLine, int instructionLine) {
    return targetLine > instructionLine;
  }

}