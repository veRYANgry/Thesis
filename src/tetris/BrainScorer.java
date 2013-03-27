package tetris;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import boardrater.*;

public class BrainScorer {

	public int seed = new Random(System.currentTimeMillis()).nextInt();
	BoardRater boardRater = new FinalRater();
	
	
	public double[] rate(NetBrain brain, ArrayList<double[]> hueristics){
		
		
		double[] score = new double[2];
		double[] temp;
		
		temp = scorerun(seed,brain,hueristics);
		score[0] +=  temp[0];
		score[1] +=  temp[1];
		temp =  scorerun(0,brain,hueristics);
		score[0] +=  temp[0];
		score[1] +=  temp[1];
		temp =  scorerun(2,brain,hueristics);
		score[0] +=  temp[0];
		score[1] +=  temp[1];
		
		return  score;
	}
	
	private double[] scorerun(int seed ,NetBrain brain, ArrayList<double[]> heuristics){
		double[] score = new double[2];
		double[] values = scoreBrain(seed,brain);
		double[] constantH = {1 , 10 , 1};
		if(heuristics != null){
			score[1] = heuristics.get(0)[0] * values[0] + heuristics.get(0)[1] * values[1] + heuristics.get(0)[2] * values[2];
		}
		
		score[0] = constantH[0] * values[0] +  constantH[1] * values[1] +  constantH[2] * values[2];
		return score;
	}
	
	private double[] scoreBrain(int seed,NetBrain brain ){
		
		TetrisController tc = new TetrisController();
		tc.startGame(seed);
		int movetime = 0;
		while (tc.gameOn) {
			Move tempMove;
			
			tempMove = brain.netMove(new Board(tc.board),
					tc.currentMove, tc.nextPiece, tc.board
					.getHeight()
					- TetrisController.TOP_SPACE);
				
			if (!tc.currentMove.piece.equals(tempMove.piece)) { 
				movetime++;
				tc.tick(TetrisController.ROTATE);
			} 
			if (tc.currentMove.x != tempMove.x) {
				movetime++;
				tc.tick(((tc.currentMove.x < tempMove.x) ? TetrisController.RIGHT : TetrisController.LEFT));
				if(movetime > 8) {
					tc.tick(TetrisController.DOWN);
					movetime = 0;
				}
				
			}else {
				tc.tick(TetrisController.DOWN);
				movetime = 0;
			}
			if(tc.count > 100)
				break;
				

		}
		double[] values = new double[3];
		values[0] = tc.count;
		values[1] = tc.rowsCleared;
		values[2] =  boardRater.rateBoard(tc.board);
		
		return  values;
		
	}
	
	public void demo(NetBrain brain){
		JFrame frame = new JFrame("TETRIS A.I. Demo");
		JComponent container = (JComponent)frame.getContentPane();
		container.setLayout(new BorderLayout());
                
        // Set the metal look and feel
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName() );
        }
        catch (Exception ignored) {}
		
		// Could create a JTetris or JBrainTetris here
		final int pixels = 16;
		JBrainTetris tetris = new JBrainTetris(TetrisController.WIDTH*pixels+2, (TetrisController.HEIGHT+TetrisController.TOP_SPACE)*pixels+2,brain);	

		container.add(tetris, BorderLayout.CENTER);
		
		Container panel = tetris.createControlPanel();
		
		// Add the quit button last so it's at the bottom
		panel.add(Box.createVerticalStrut(12));
		JButton quit = new JButton("Quit");
		panel.add(quit);
		quit.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		
		container.add(panel, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);
		tetris.tc.startGame(seed);


	}
	
}
