package boardrater;

import tetris.Board;

public class RowsWithHoles extends BoardRater {
  static boolean[] holednesses;
	double rate(Board board) {	
		if(holednesses==null) holednesses = new boolean[board.getHeight()];
		for(int i=0; i<holednesses.length; i++) holednesses[i] = false;

		for (int x=0; x<board.getWidth(); x++) {
			final int colHeight = board.getColumnHeight(x);
			int y = colHeight - 2;	// addr of first possible hole
			while (y>=0) {
				if  (!board.getGrid(x,y)) {
					holednesses[y] = true;
				}
				y--;
			}
		}
		int holedRows = 0;
		for(int i=0; i<holednesses.length; i++) if(holednesses[i]) holedRows++;
		return holedRows;
	}

}