package tetris;
import boardrater.*;
// Brain.java -- the interface for Tetris brains
//package Hw2;
public interface Brain {
	
	/**
	 Given a piece and a board, returns a move object that represents
	 the best play for that piece, or returns null if no play is possible.
	 The board should be in the committed state when this is called.
	 "limitHeight" is the bottom section of the board that where pieces must
	  come to rest -- typically 20.
	*/
	public Move bestMove(Board board, Piece piece, Piece nextPiece, int limitHeight);
	
	public Brain setRater(BoardRater r);
	
	public BoardRater boardRater = new FinalRater();
}