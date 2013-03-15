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
		
		int eyewidth = 7;
		int eyedepth = 5;
		NetworkOutputSet opSet;
		MarioInput ip;
		boolean[][] grid;
		double[] op;
		double[] inputs = new double[eyewidth * eyedepth + 16 + 6];
		
		
		grid = board.grid;
		//copy over entire board
		for(int x =  move.x - 3;x < move.x + 4 ; x++){
			for(int y = move.y;y > move.y - eyedepth ; y--){
				if(y < board.height && x < board.width && x >= 0 && y >= 0)
					inputs[(move.y - y )* eyewidth + (x - move.x + 3)] = grid[x][y] ? 1 : 0;
				else
					inputs[(move.y - y )* eyewidth + (x - move.x + 3)] = 1;
			}
		}
		
		//calculate the grid representation of the piece then use it as an input
		// max size for inputs is 16 blocks 4X4
		// place it at the end of the inputs
		Point[] pieceParts = move.piece.getBody();
		
		for(Point t : pieceParts){
			//copy an image of the piece
			inputs[eyewidth * eyedepth + t.x + t.y * 4] = 1;

		}
		
		for(int i = 0; i < 6; i++){
			inputs[eyewidth * eyedepth + 16 + i] =  move.y > i ? 1 : 0;
		}
		
//		System.out.println("<-<");
//		for(int i = 0;i < eyedepth ; i++){
//			for(int j = 0;j <eyewidth ; j++){
//				if(inputs[i * eyewidth + j ] > .6)
//				System.out.print("0");
//				else
//				System.out.print("_");
//			}
//			System.out.println();
//		}
//		
		
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
