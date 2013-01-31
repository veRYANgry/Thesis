package tetris;
/*
 * JBrainTetris.java
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
public class JBrainTetris extends JTetris {
	private static final long serialVersionUID = 1L;

	private Brain mBrain = new Ply1Brain();
	private Move mMove;
	protected javax.swing.Timer timerAI;
	int current_count = -1;

	/** Creates new JBrainTetris */
	public JBrainTetris(int width, int height) {
		super(width, height);
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
		
		if (!tc.currentMove.piece.equals(mMove.piece)) { 
			super.tick(TetrisController.ROTATE);
		} else if (tc.currentMove.x != mMove.x) {
			super.tick(((tc.currentMove.x < mMove.x) ? TetrisController.RIGHT : TetrisController.LEFT));
		} else {
			return true;
		}
		return false;
	}


	public java.awt.Container createControlPanel() {
		java.awt.Container panel2 = Box.createVerticalBox();
		panel2 = super.createControlPanel();


		return (panel2);
	}

}
