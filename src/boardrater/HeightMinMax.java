package boardrater;

import tetris.Board;

public class HeightMinMax extends BoardRater {

  double rate(Board board) {
		int maxHeight = 0;
		int minHeight = board.getHeight();
		
		for (int x = 0; x < board.getWidth(); x++) {
			int height = board.getColumnHeight(x);
			if (height > maxHeight)
				maxHeight = height; // Record height of highest column on the board
			if (height < minHeight)
				minHeight = height; // Record height of the lowest column on the board
		}
		
		return maxHeight - minHeight;
	}

}


/*
class HighestTopToLowestTop extends BoardRater {
  double rate(Board board) {
		int maxHeight   = 0;
		int minHeight   = 25;
		int[] heights   = new int[board.getWidth()];
		for(int x=0; x<heights.length; x++) {
			heights[x] = board.getColumnHeight(x);
			if (heights[x] > maxHeight)
				maxHeight = heights[x];
			if (heights[x] < minHeight)
				minHeight = heights[x];
		}
		return (double) (maxHeight-minHeight);
  }
}
*/