//LOL, THIS RETURNS ALL ZEROS. so why are the weights all centering around a given value?!?!?!? it must mean the genetic algorithm SUCKS.

package boardrater;

import tetris.Board;

public class Trough extends BoardRater {

	double rate(Board board) {
		int[] troughs = new int[board.getWidth()];
		int troughCount = 0;
		
		for (int x = 0; x < board.getWidth(); x++) {
			int height = board.getColumnHeight(x); // Store the height for each column
			// The start of the trough will always be at heights[x] - 1, so we want to include this space in the
			// depth without changing our loop for counting holes
			if (height > 0 && !board.getGrid(x, height-1)) {
				troughs[x]++;
				troughCount++;
			}
			
		}
    // System.out.println(troughCount);
		return troughCount;
	}

}
