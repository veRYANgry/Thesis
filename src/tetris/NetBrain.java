package tetris;

import org.neat4j.neat.data.core.NetworkOutputSet;
import org.neat4j.neat.nn.core.NeuralNet;

import ch.idsia.agents.controllers.MarioInput;

import boardrater.BoardRater;
import boardrater.FinalRater;

public class NetBrain implements Brain {

	public BoardRater boardRater = new FinalRater();
	private NeuralNet net;
	
	public NetBrain(NeuralNet net) {
		this.net = net;
	}

	@Override
	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
		NetworkOutputSet opSet;
		MarioInput ip;
		boolean[][] grid;
		double[] op;
		double[] inputs = new double[board.height * board.width];
		
		grid = board.grid;
		//copy over entire board
		for(int i = 0;i < board.height ; i++){
			for(int j = 0;j < board.width ; j++){
				inputs[i * board.width + j ] = grid[j][i] ? 1 : 0;
			}
		}
		
		ip = new MarioInput();

		ip.SetNetworkInput(inputs);

		// execute net over data set

		opSet = net.execute(ip);
		op = opSet.nextOutput().values();
		
		return null;
	}

	public Brain setRater(BoardRater r)
	{
		boardRater = r;
		return this;
	}

}
