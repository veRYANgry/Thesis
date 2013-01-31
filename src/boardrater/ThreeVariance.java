package boardrater;
import tetris.Board;

//This rater returns the average of all the variances of height in all groups of three adjacent columns.
//If this is factored into the utility function, it will discourage brains from having wildly varying heights across the board.
public class ThreeVariance extends BoardRater {
  double rate(Board board) {
    int w = board.getWidth();
    double runningVarianceSum = 0.0;
    for(int i=0; i<w-2; i++) {
      double  h0 = (double)board.getColumnHeight(i),
              h1 = (double)board.getColumnHeight(i+1),
              h2 = (double)board.getColumnHeight(i+2);
      double  m = (h0+h1+h2)/3.0;
      h0-=m;  h1-=m;  h2-=m;
      h0*=h0; h1*=h1; h2*=h2;
      runningVarianceSum+=(h0+h1+h2)/3.0;
    }
    return runningVarianceSum / (double)(w-3);
  }
}