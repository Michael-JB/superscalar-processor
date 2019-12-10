package control;

public class StaticBackwardsTaken extends StaticBranchPredictor {

  @Override
  protected boolean shouldTakeBranch(int targetLine, int instructionLine) {
    return targetLine < instructionLine;
  }

}