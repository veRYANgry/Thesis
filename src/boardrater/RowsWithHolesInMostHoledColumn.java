package boardrater;

import tetris.Board;

public class RowsWithHolesInMostHoledColumn extends BoardRater {

	double rate(Board board) {	
		// Count the holes, and sum up the heights
		int mostHolesInAnyColumn = 0;
		for (int x=0; x<board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			
			int y = colHeight-2;
			int holes = 0;

			while (y>=0) {
				if  (!board.getGrid(x,y)) {
					holes++;
				}
				y--;
			}
			
			if(mostHolesInAnyColumn<holes) mostHolesInAnyColumn = holes;
		}
		return mostHolesInAnyColumn;
	}

}
