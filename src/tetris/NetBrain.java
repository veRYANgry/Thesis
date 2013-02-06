package tetris;

import java.awt.Point;

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

	
	public Move netMove(Board board, Move move, Piece nextPiece,
			int limitHeight) {
		NetworkOutputSet opSet;
		MarioInput ip;
		boolean[][] grid;
		double[] op;
		double[] inputs = new double[board.height * board.width + 16];
		
		
		grid = board.grid;
		//copy over entire board
		for(int i = 0;i < board.height ; i++){
			for(int j = 0;j < board.width ; j++){
				inputs[i * board.width + j ] = grid[j][i] ? 1 : 0;
			}
		}
		
		//calculate the grid representation of the piece then use it as an input
		// max size for inputs is 16 blocks 4X4
		// place it at the end of the inputs
		Point[] pieceParts = move.piece.getBody();
		
		for(Point t : pieceParts){
			//copy an image of the piece
			inputs[board.height * board.width + t.x + t.y * 4] =  1;
			//copy the piece to the board
			inputs[t.x + t.y * board.width + move.x + move.y * board.width] = 1;
		}
		
		
//		System.out.println("<-<");
//		for(int i = 0;i < board.height ; i++){
//			for(int j = 0;j < board.width ; j++){
//				if(inputs[i * board.width + j ] > .6)
//				System.out.print("0");
//				else
//				System.out.print("_");
//			}
//			System.out.println();
//		}
		
		
		ip = new MarioInput();

		ip.SetNetworkInput(inputs);

		// execute net over data set

		opSet = net.execute(ip);
		op = opSet.nextOutput().values();
		
		Move newMove = new Move();
		newMove.x = move.x;
		newMove.y = move.y;
		
		if(op[0] > .75)
			newMove.x++;
		if(op[1] > .75)
			newMove.x--;
		if(op[2] > .75)
			newMove.piece = move.piece.nextRotation();
		else
			newMove.piece = move.piece;
		return newMove;
	}

	public Brain setRater(BoardRater r)
	{
		boardRater = r;
		return this;
	}


	@Override
	public Move bestMove(Board board, Piece piece, Piece nextPiece,
			int limitHeight) {
		// TODO Auto-generated method stub
		return null;
	}



}
