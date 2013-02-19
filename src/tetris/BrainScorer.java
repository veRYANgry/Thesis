package tetris;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import boardrater.*;

public class BrainScorer {

	public int seed = new Random(System.currentTimeMillis()).nextInt();
	BoardRater boardRater = new FinalRater();
	
	public double rate(NetBrain brain){
		
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
		return  tc.count + tc.rowsCleared * 100 + boardRater.rateBoard(tc.board);
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
