package boardrater;
import tetris.Board;

public class HeightStdDev extends BoardRater {
  double rate(Board board) {
    return Math.sqrt(new HeightVar().rate(board));
  }
}