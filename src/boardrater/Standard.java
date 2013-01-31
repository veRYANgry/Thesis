package boardrater;

import tetris.Board;

public class Standard extends BoardRater {

	/*
	 A simple brain function.
	 Given a board, produce a number that rates
	 that board position -- larger numbers for worse boards.
	 This version just counts the height
	 and the number of "holes" in the board.
	 See Tetris-Architecture.html for brain ideas.
	*/
	double rate(Board board) {
		// Add up the counts to make an overall score
		// The weights, 8, 40, etc., are just made up numbers that appear to work
		return (8*board.getMaxHeight() + 40*new HeightAvg().rateBoard(board) + 1.25*new SimpleHoles().rateBoard(board));	
	}

}
