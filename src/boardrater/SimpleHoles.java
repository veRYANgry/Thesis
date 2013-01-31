package boardrater;

import tetris.Board;

public class SimpleHoles extends BoardRater {

	double rate(Board board) {	
		int holes = 0;
		// Count the holes, and sum up the heights
		for (int x=0; x<board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			
			int y = colHeight - 2;	// addr of first possible hole
			
			while (y>=0) {
				if  (!board.getGrid(x,y)) {
					holes++;
				}
				y--;
			}
		}
		return holes;
	}

}