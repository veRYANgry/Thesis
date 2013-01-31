package boardrater;
import tetris.Board;
import java.lang.*;
import java.util.*;

//Classes finishing off this abstract class are capable of taking a board and returning a number, using rateBoard.
//Override the abstract double rate(Board) method to do this; they will bench themselves.
public abstract class BoardRater {
  int callCount = 0;
  int runTime   = 0;
  abstract double rate(Board board);
  public double rateBoard(Board board) {
    this.callCount++;
    board.enableCaching();
    long start = System.nanoTime();
    double ret = this.rate(board);
    this.runTime += System.nanoTime()-start;
    board.disableCaching();
    return ret;
  }
}