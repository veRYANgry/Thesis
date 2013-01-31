package tetris;

import java.util.LinkedList;
import boardrater.*;

public class PlyNBrain implements Brain {
	Piece[] possiblePieces;
	BoardRater boardRater = new FinalRater();
	int level = 0;

	class Score {
		Move move;
		int score;
	}

	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
		
		LinkedList<Piece> pieces = new LinkedList<Piece>();
		pieces.add(piece);
		pieces.add(nextPiece);
		Score bestScore = new Score();
		bestScore.move = new Move();
		bestScore.move.x = 0;
		bestScore.move.y = 0;
		bestScore.move.piece = piece;
		bestScore.score = Integer.MAX_VALUE;
		
		for (Score score : getScores(board, pieces, limitHeight, level)) {
			if ((score.score < bestScore.score)) {
				bestScore = score;
			}
		}
		return bestScore.move;
	}

	LinkedList<Score> getScores(Board board, LinkedList<Piece> actualPieces,
			int limitHeight, int level) {

		LinkedList<Piece> pieces;

		Piece first = actualPieces.removeFirst();
		Piece current = first;
		pieces = new LinkedList<Piece>();
		do {
			pieces.add(current);
			current = current.nextRotation();
		} while (current != first);

		LinkedList<Score> scores = new LinkedList<Score>();

		for (Piece piece : pieces) {
			int xBound = board.getWidth() - piece.getWidth() + 1;
			int yBound = limitHeight - piece.getHeight() + 1;
			for (int x = 0; x < xBound; x++) {
				int y = board.dropHeight(piece, x);
				if ((y < yBound) && board.canPlace(piece, x, y)) {
					Score score = new Score();
					Board nextBoard = board.clone();
					nextBoard.place(piece, x, y);

					if (level < 2) {
						score.score = (int) boardRater.rateBoard(nextBoard);
					} else {
						LinkedList<LinkedList<Piece>> nextPossiblePieces = new LinkedList<LinkedList<Piece>>();
						
						if (actualPieces.size() > 0) {
							/* Since the actual piece is given, only give that and any future given pieces */
							nextPossiblePieces.add((LinkedList<Piece>) actualPieces
									.clone());
						} else {
							for (Piece possiblePiece : possiblePieces) {
								LinkedList<Piece> nextPossibleLLPiece = new LinkedList<Piece>();
								nextPossibleLLPiece.add(possiblePiece);
								nextPossiblePieces.add(nextPossibleLLPiece);
							}
						}

						/* Find avg best score for each possible piece */
						for (LinkedList<Piece> nextPossiblePiece : nextPossiblePieces) {
							LinkedList<Score> nextScores = getScores(nextBoard,
									nextPossiblePiece,
									limitHeight, level - 1);

							int bestScore = Integer.MAX_VALUE;
							/* Find min score */
							for (Score nextScore : nextScores) {
								if (nextScore.score < bestScore) {
									bestScore = nextScore.score;
								}
							}

							score.score += bestScore / nextPossiblePieces.size();
						}
					}

					score.move = new Move();
					score.move.x = x;
					score.move.y = y;
					score.move.piece = piece;
					scores.add(score);
				}
			}
		}

		return scores;
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
