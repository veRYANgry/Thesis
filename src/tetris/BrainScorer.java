package tetris;

import java.util.Random;
import boardrater.*;

public class BrainScorer {

	public int seed = 0;
	
	public double rate(Brain brain){
		
		TetrisController tc = new TetrisController();
		tc.startGame(seed);
		
		while (tc.gameOn) {
			Move move = brain.bestMove(new Board(tc.board),
					tc.currentMove.piece, tc.nextPiece, tc.board
					.getHeight()
					- TetrisController.TOP_SPACE);

			while (!tc.currentMove.piece.equals(move.piece)) {
				tc.tick(TetrisController.ROTATE);
			}

			while (tc.currentMove.x != move.x) {
				tc
				.tick(((tc.currentMove.x < move.x) ? TetrisController.RIGHT
						: TetrisController.LEFT));
			}

			int current_count = tc.count;
			while ((current_count == tc.count) && tc.gameOn) {
				tc.tick(TetrisController.DOWN);
			}

		}
		
		return 0;
	}
	
}
