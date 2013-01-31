package boardrater;
import tetris.Board;

public class Grant1 extends BoardRater
{
	public Grant1()
	{
		
	}
	/**
	 * This board rater takes into account the number of holes, number of troughs, max height, minimum heigh, and average height
	 * to give the board a score.
	 * @param board
	 * @return
	 */
	double rate(Board board)
	{	
		// For this board rating method, the weights are arbitrary, but reflect more or less importance
		return (20*new HeightMinMax().rateBoard(board) + 25*new HeightAvg().rateBoard(board) + 20*new SimpleHoles().rateBoard(board) + 15*new Trough().rateBoard(board));
		
	}
}
