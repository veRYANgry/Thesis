package tetris;

import java.util.Date;
import java.util.Random;
import java.lang.*;

import boardrater.Leo1;

/**
 * No-frills brain benchmark
 * 
 */
public class SingleBrainTest {
	static class Result {
		String name;
		int score = 0;
		long thinkTime = 0;
		public String toString() {
		  return this.score+"";
		}
	}

	/* Add your yummy brain here NOM NOM NOM */
	static Brain brain = new Ply2Brain();

	static int SAMPLE_SIZE = 3;

	SingleBrainTest() {
	}

	Result[] computeResults(int seed) {
    Result[] results = new Result[SAMPLE_SIZE];
		TetrisController tc = new TetrisController();

    for(int i=0; i<SAMPLE_SIZE; i++) {
  		tc.startGame(seed+i);

  		Date start = new Date();
    
      long lastDisplay = System.currentTimeMillis(),tempTime;
  		while (tc.gameOn) {
  			Move move = brain.bestMove(new Board(tc.board),
  					tc.currentMove.piece, tc.nextPiece, tc.board
  							.getHeight()
  							- TetrisController.TOP_SPACE);

  			while (!tc.currentMove.piece.equals(move.piece)) {
  				tc.tick(TetrisController.ROTATE);
  			}

  			while (tc.currentMove.x != move.x) {
  				tc.tick(((tc.currentMove.x < move.x) ? TetrisController.RIGHT : TetrisController.LEFT));
  			}

  			int current_count = tc.count;
  			while ((current_count == tc.count) && tc.gameOn) {
  				tc.tick(TetrisController.DOWN);
  			}
      
        if((tempTime=System.currentTimeMillis()) - lastDisplay > 20000) {
          lastDisplay = tempTime;
          System.out.print("..."+tc.count);
        }
  		}
      System.out.println("..."+tc.count+".");
      System.out.println("Game: "+tc.count+".\n");
  		results[i] = new Result();
  		results[i].thinkTime = (new Date().getTime() - start.getTime());
  		results[i].name = brain.toString();
  		results[i].score = tc.count;
	  }

		return results;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SingleBrainTest bb = new SingleBrainTest();
		int seed = 0;
    if(args.length==1) seed = Integer.parseInt(args[0]);
    if(args.length==2) {
      seed = Integer.parseInt(args[0]);
      SAMPLE_SIZE = Integer.parseInt(args[1]);
    }

		System.out.println("Running with seed " + seed+":");

		Result[] results = bb.computeResults(seed);

		System.out.println("");

		System.out.println("Performance:");
    for(int i=0; i<SAMPLE_SIZE; i++) {
      System.out.print(results[i]);
      if(i<SAMPLE_SIZE-1) System.out.print(", ");
    }
    double averageScore = 0.0;
    for(int i=0; i<SAMPLE_SIZE; i++) {
      averageScore+=results[i].score;
    }
    averageScore /= SAMPLE_SIZE;
    System.out.println("\nAVERAGE PIECE COUNT: "+averageScore);
	}

}
