package boardrater;

import tetris.Board;

public class HeightAvg extends BoardRater {
	double rate(Board board) {
		int sumHeight = 0;
		// Count the holes, and sum up the heights
		for (int x=0; x<board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			sumHeight += colHeight;
		}
		return ((double)sumHeight)/board.getWidth();
	}

}
