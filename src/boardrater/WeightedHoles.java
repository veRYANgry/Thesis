package boardrater;
import tetris.Board;

public class WeightedHoles extends BoardRater {
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
		double weightedHoleCount = 0.0;
		int[] heights   = new int[board.getWidth()];
		for(int x=0; x<board.getWidth(); x++) {
			heights[x] = board.getColumnHeight(x);
			int y = heights[x] - 2;
			while(y>=0) {
				if(!board.getGrid(x,y))
					weightedHoleCount+=(double)(maxHeight-y)/(double)maxHeight;
				y--;
			}
		}
		return weightedHoleCount;
  }
}