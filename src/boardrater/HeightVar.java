package boardrater;
import tetris.Board;

public class HeightVar extends BoardRater {
	double rate(Board board) {
		int sumHeight = 0;
		// Count the holes, and sum up the heights
		for (int x=0; x<board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
		}
		double avgHeight = ((double)sumHeight)/board.getWidth();		
		
		// find the variance
		int varisum = 0;
		for (int x = 0; x < board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			varisum += Math.pow(colHeight - avgHeight, 2);
		}
		
		return varisum / board.getWidth();
	}

}