package boardrater;
import tetris.Board;

public class HeightMax extends BoardRater {
  double rate(Board board) {
    return (double) board.getMaxHeight();
  }
}