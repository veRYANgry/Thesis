package tetris;

import java.util.Date;
import java.util.Random;
import boardrater.*;

import boardrater.Leo1;

/**
 * No-frills brain benchmark
 * 
 */
public class BrainBenchmark {
	static class Result {
		String brainName;
		String raterName;
		int score = 0;
		long thinkTime = 0;
	}

	/* Add your yummy brains here NOM NOM NOM */
	static Brain brainz[] = { 
		//new Ply1Brain(), 
		new Ply2Brain(),
		//new PlyNBrain()
	};

	static BoardRater raterz[] ={
		new Grant1(),
		new AverageSquaredTroughHeight(),
		new BlocksAboveHoles(),
		new ConsecHorzHoles(),
		new HeightAvg(),
		new HeightMax(),
		new HeightMinMax(),
		new HeightStdDev(),
		new HeightVar(),
		new Standard(),
		new Leo1(),
		new RowsWithHolesInMostHoledColumn(),
		new SimpleHoles(),
		new ThreeVariance(),
		new Trough(),
		new WeightedHoles(),
		//new FinalRater()
	};

	static final int SAMPLE_SIZE = 50;

	BrainBenchmark() {
	}

	Result[][] computeResults(int seed) {
		Result[][] results = new Result[brainz.length][raterz.length];

		TetrisController tc = new TetrisController();

		for (int i = 0; i < brainz.length; i++) {
			for(int j = 0; j < raterz.length; j++){
				brainz[i].setRater(raterz[j]); // sets the rating method the brain must use for this iteration
				tc.startGame(seed);

				Date start = new Date();

				while (tc.gameOn) {
					Move move = brainz[i].bestMove(new Board(tc.board),
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

				results[i][j] = new Result();
				results[i][j].thinkTime = (new Date().getTime() - start.getTime());
				results[i][j].brainName = brainz[i].toString()+" ";
				results[i][j].raterName = raterz[j].toString()+ " ";
				results[i][j].score = tc.count;
			}}

		return results;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrainBenchmark bb = new BrainBenchmark();

		Result sums[][] = new Result[brainz.length][raterz.length];
		for (int i = 0; i < sums.length; i++) {
			for (int j = 0; j < raterz.length; j++)
				sums[i][j] = new Result();
		}

		for (int seed = 0; seed <= SAMPLE_SIZE; seed++) {
			System.out.println("Seed " + seed + " score time");

			Result[][] results = bb.computeResults(seed);

			for (int i = 0; i < results.length; i++) {
				for (int j = 0; j < results[i].length; j++) {
					System.out.println(results[i][j].thinkTime + " "
							+ results[i][j].brainName + ":" 
							+ results[i][j].raterName + results[i][j].score);
					sums[i][j].brainName = results[i][j].brainName;
					sums[i][j].raterName = results[i][j].raterName;
					sums[i][j].score += results[i][j].score;
					sums[i][j].thinkTime += results[i][j].thinkTime;
				}
			}
			System.out.println("");
		}
		
		System.out.println("Average Scores");
		for (int i = 0; i < sums.length; i++) {
			for (int j = 0; j < sums[i].length; j++) {
				System.out.println(
						+ (sums[i][j].score / SAMPLE_SIZE) + " "
						//+ ((double) sums[i][j].score / sums[i][j].thinkTime)
						//+ sums[i][j].brainName + ":"+sums[i][j].raterName
				);
			}
		}

	}

}
