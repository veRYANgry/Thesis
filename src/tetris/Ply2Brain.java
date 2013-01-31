package tetris;
import boardrater.*;

public class Ply2Brain implements Brain
{
	BoardRater boardRater = new FinalRater();

	public Move bestMove(Board board, Piece piece, Piece nextPiece, int limitHeight) {
		double bestScore = 1e20;
		int bestX = 0;
		int bestY = 0;
		Piece bestPiece = piece;
		
		Piece current = piece;
		Piece next = nextPiece;

		// loop through all the rotations
		do {
			final int yBound = limitHeight - current.getHeight()+1;
			final int xBound = board.getWidth() - current.getWidth()+1;

			// For current rotation, try all the possible columns
			for (int x = 0; x<xBound; x++) {
				int y = board.dropHeight(current, x);
				if ((y<yBound) && board.canPlace(current, x, y)) {
					Board testBoard = new Board(board);
					testBoard.place(current, x, y);
					testBoard.clearRows();

						// Everything in this while loop evaluates possible moves with the next piece
						do
						{
							final int jBound = limitHeight - next.getHeight()+1;
							final int iBound = testBoard.getWidth() - next.getWidth()+1;
							
							for(int i = 0; i < iBound; i++)
							{
								int j = testBoard.dropHeight(next, i);
								if(j < jBound && testBoard.canPlace(next, i, j)) {
									Board temp = new Board(testBoard);
									temp.place(next, i, j);
									temp.clearRows();
										
										double nextScore = boardRater.rateBoard(temp);
										
										if(nextScore < bestScore)
										{
											bestScore = nextScore;
											bestX = x;
											bestY = y;
											bestPiece = current;
										}
									}

								}

							next = next.nextRotation();
						} while (next != nextPiece);
						// Back out to the current piece

					}
				}
			current = current.nextRotation();
		} while (current != piece);

		Move move = new Move();
		move.x=bestX;
		move.y=bestY;
		move.piece=bestPiece;
		return(move);
	}
	
	/**
	 * This method defines how the brain will rate the board. This method can be used to reset the boardRater
	 * during testing, without needing to explicitly change code.
	 */
	public Brain setRater(BoardRater r)
	{
		boardRater = r;
		return this;
	}
}
