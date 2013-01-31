package tetris;
/*
 * JBrainTetrisFast.java
 *
 * Created on January 31, 2002, 10:58 AM
 */

//package Hw2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import boardrater.*;

import javax.swing.Box;
import javax.swing.JCheckBox;

/**
 * 
 * @author Lews Therin
 * @version
 */
public class JBrainTetrisFast extends JTetrisFast {
	private static final long serialVersionUID = 1L;

	private Brain mBrain = new Ply2Brain();
	private Move mMove;
	protected javax.swing.Timer timerAI;
	int current_count = -1;

	/** Creates new JBrainTetrisFast */
	public JBrainTetrisFast(int width, int height) {
		super(width, height);
    // double[] c = {0,-100,0,0,0,-100,-50000,10,0,0,0,-10,0};    //uncomment here to use custom weights
		// FinalRater f = new FinalRater(c);                          //and here...
		// mBrain.setRater(f);                                        //and here.
		// Create the Timer object and have it send
		// tick(DOWN) periodically
		/*
		timerAI = new javax.swing.Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tickAI();
			}
		});
		*/
	}
	
	public void startGame() {
		super.startGame();
		// Create the Timer object and have it send
		//timerAI.start();
	}
	
	public void stopGame() {
		super.stopGame();
		//timerAI.stop();
	}
	
	public void tick(int verb) {
		if (tickAI()) {
			super.tick(verb);
		}
	}

	public boolean tickAI() {
		if (current_count != tc.count) {
			current_count = tc.count;
			mMove = mBrain.bestMove(new Board(tc.board), tc.currentMove.piece, tc.nextPiece, tc.board.getHeight()-TetrisController.TOP_SPACE);
		}
		
		while (!tc.currentMove.piece.equals(mMove.piece)) { 
			super.tick(TetrisController.ROTATE);
		} 
		while (tc.currentMove.x != mMove.x) {
			super.tick(((tc.currentMove.x < mMove.x) ? TetrisController.RIGHT : TetrisController.LEFT));
		} 
		return true;
	}


	public java.awt.Container createControlPanel() {
		java.awt.Container panel2 = Box.createVerticalBox();
		panel2 = super.createControlPanel();


		return (panel2);
	}

}
