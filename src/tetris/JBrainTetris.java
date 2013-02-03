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

	public NetBrain mBrain;
	private Move mMove;
	protected javax.swing.Timer timerAI;
	int current_count = -1;

	/** Creates new JBrainTetris */
	public JBrainTetris(int width, int height,NetBrain mBrain) {
		super(width, height);
		this.mBrain = mBrain;
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
		tickAI();

	}

	public boolean tickAI() {
		if (current_count != tc.count) {
			current_count = tc.count;
			 mBrain.netMove(new Board(tc.board),
					tc.currentMove, tc.nextPiece, tc.board
					.getHeight()
					- TetrisController.TOP_SPACE);


			tc.tick(10);		}
		
		
		if (!tc.gameOn) {
			stopGame();
		}
		
		countLabel.setText(Integer.toString(tc.count));
		nextPiecePanel.setPiece(tc.nextPiece);
		
		repaint();
		
		return false;
	}


	public java.awt.Container createControlPanel() {
		java.awt.Container panel2 = Box.createVerticalBox();
		panel2 = super.createControlPanel();


		return (panel2);
	}

}
