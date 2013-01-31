package boardrater;

import tetris.Board;

public class Leo1 extends BoardRater {
	
	/*
	 * I changed the weights to something I think will work better
	 * I also considered consecutiveHoles as one long hole.
	 */
	double rate(Board board) {
			// Add up the counts to make an overall score
			// The weights were changed
			return (new HeightVar().rateBoard(board) + Math.pow(board.getMaxHeight() / 2, 2) * new ConsecHorzHoles().rateBoard(board));	
	}
}
