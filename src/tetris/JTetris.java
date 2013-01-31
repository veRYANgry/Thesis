package tetris;
// JTetris.java
//package Hw2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 JTetris presents a tetris game in a window.
 It handles the GUI and the animation.
 The Piece and Board classes handle the
 lower-level computations.
 This code is provided in finished form for the students.
 See Tetris-Architecture.html for an overview.
 
 @author	Nick Parlante
 @version	1.0, March 1, 2001
*/

/*
 Implementation notes:
 -The "currentPiece" points to a piece that is
 currently falling, or is null when there is no piece.
 -tick() moves the current piece
 -a timer object calls tick(DOWN) periodically
 -keystrokes call tick with LEFT, RIGHT, etc.
 -Board.undo() is used to remove the piece from its
 old position and then Board.place() is used to install
 the piece in its new position.
*/

public class JTetris extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2288695225264400597L;
	
	protected PiecePanel nextPiecePanel; // Displays the nextPiece for the player to see
	
	// Controls
	protected JLabel countLabel;
	protected JLabel timeLabel;
	protected JButton startButton;
	protected JButton stopButton;
	protected JLabel seedDisplay;
	protected JButton startSeed;
	protected javax.swing.Timer timer;
	protected JSlider speed;
	protected JFormattedTextField seq;
	
	public final int DELAY = 400;	// milliseconds per tick
	
	protected long startTime;	// used to measure elapsed time
	
	TetrisController tc;

	JTetris(int width, int height) {
		super();

		setPreferredSize(new Dimension(width, height));
		
		tc = new TetrisController();

		/*
		 Register key handlers that call
		 tick with the appropriate constant.
		 e.g. 'j' and '4'  call tick(LEFT)
		*/
		
		// LEFT
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.LEFT);
				}
			}, "left", KeyStroke.getKeyStroke('4'), WHEN_IN_FOCUSED_WINDOW
		);
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.LEFT);
				}
			}, "left", KeyStroke.getKeyStroke('j'), WHEN_IN_FOCUSED_WINDOW
		);
		
		
		// RIGHT
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.RIGHT);
				}
			}, "right", KeyStroke.getKeyStroke('6'), WHEN_IN_FOCUSED_WINDOW
		);
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.RIGHT);
				}
			}, "right", KeyStroke.getKeyStroke('l'), WHEN_IN_FOCUSED_WINDOW
		);
		
		
		// ROTATE	
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.ROTATE);
				}
			}, "rotate", KeyStroke.getKeyStroke('5'), WHEN_IN_FOCUSED_WINDOW
		);
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.ROTATE);
				}
			}, "rotate", KeyStroke.getKeyStroke('k'), WHEN_IN_FOCUSED_WINDOW
		);
		
		
		// DROP
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.DROP);
				}
			}, "drop", KeyStroke.getKeyStroke('0'), WHEN_IN_FOCUSED_WINDOW
		);
		registerKeyboardAction(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tick(TetrisController.DROP);
				}
			}, "drop", KeyStroke.getKeyStroke('n'), WHEN_IN_FOCUSED_WINDOW
		);		
		
		
		// Create the Timer object and have it send
		// tick(DOWN) periodically
		timer = new javax.swing.Timer(DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick(TetrisController.DOWN);
			}
		});
	}

	void tick(int verb) {
		tc.tick(verb);

		
		if (!tc.gameOn) {
			stopGame();
		}
		
		countLabel.setText(Integer.toString(tc.count));
		nextPiecePanel.setPiece(tc.nextPiece);
		
		repaint();
	}

	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	*/
	public void startGame() {
		tc.startGame();
			
		// draw the new board state once
		repaint();
		
		enableButtons();
		timeLabel.setText(" ");
		
		timer.start();
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Same as startGame(), but gives the controller a seed to generate a specific sequence of pieces
	 * @param seed
	 */
	public void startGame(int seed) {
		tc.startGame(seed);
			
		// draw the new board state once
		repaint();
		
		enableButtons();
		timeLabel.setText(" ");
		seedDisplay.setText(Integer.toString(seed));
		
		timer.start();
		startTime = System.currentTimeMillis();
	}
	
	
	/**
	 Sets the enabling of the start/stop buttons
	 based on the gameOn state.
	*/
	private void enableButtons() {
	    startButton.setEnabled(!tc.gameOn);
	    stopButton.setEnabled(tc.gameOn);
	    startSeed.setEnabled(!tc.gameOn);
	}
	
	/**
	 Stops the game.
	*/
	public void stopGame() {
		tc.gameOn = false;
		enableButtons();
		timer.stop();
		
		long delta = (System.currentTimeMillis() - startTime)/10;
		timeLabel.setText(Double.toString(delta/100.0) + " seconds");

	}
	
	
	/*
	 Pixel helpers.
	 These centralize the translation of (x,y) coords
	 that refer to blocks in the board to (x,y) coords that
	 count pixels. Centralizing these computations here
	 is the only prayer that repaintPiece() and paintComponent()
	 will be consistent.
	 
	 The +1's and -2's are to account for the 1 pixel
	 rect around the perimeter.
	*/
	
	
	// width in pixels of a block
	private final float dX() {
		return( ((float)(getWidth()-2)) / tc.board.getWidth() );
	}

	// height in pixels of a block
	private final float dY() {
		return( ((float)(getHeight()-2)) / tc.board.getHeight() );
	}
	
	// the x pixel coord of the left side of a block
	private final int xPixel(int x) {
		return(Math.round(1 + (x * dX())));
	}
	
	// the y pixel coord of the top of a block
	private final int yPixel(int y) {
		return(Math.round(getHeight() -1 - (y+1)*dY()));
	}


	/**
	 Draws the current board with a 1 pixel border
	 around the whole thing. Uses the pixel helpers
	 above to map board coords to pixel coords.
	 Draws rows that are filled all the way across in green.
	*/
	public void paintComponent(Graphics g) {
		
		// Draw a rect around the whole thing
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
		
		
		// Draw the line separating the top
		int spacerY = yPixel(tc.displayBoard.getHeight() - TetrisController.TOP_SPACE - 1);
		g.setColor(Color.WHITE);
		g.drawLine(0, spacerY, getWidth()-1, spacerY);		
		
		// Factor a few things out to help the optimizer
		final int dx = Math.round(dX()-2);
		final int dy = Math.round(dY()-2);
		final int bWidth = tc.displayBoard.getWidth();

		int x, y;
		// Loop through and draw all the blocks
		// left-right, bottom-top
		for (x=0; x<bWidth; x++) {
			int left = xPixel(x);	// the left pixel
			
			// draw from 0 up to the col height
			final int yHeight = tc.displayBoard.getColumnHeight(x);
			for (y=0; y<yHeight; y++) {
				if (tc.displayBoard.getGrid(x, y)) {
					g.setColor(tc.displayBoard.colorGrid[x][y]);
					g.fillRect(left+1, yPixel(y)+1, dx, dy);	// +1 to leave a white border
					
				}
			}
		}
	}
	
	
	/**
	 Updates the timer to reflect the current setting of the 
	 speed slider.
	*/
	public void updateTimer() {
		double value = ((double)speed.getValue())/speed.getMaximum();
		timer.setDelay((int)(DELAY - value*DELAY));
	}
	
	
	/**
	 Creates the panel of UI controls.
	 This code is very repetitive -- the GUI/XML
	 extensions in Java 1.4 should make this sort
	 of ugly code less necessary.
	*/
	public java.awt.Container createControlPanel() {
		java.awt.Container panel = Box.createVerticalBox();
		
		nextPiecePanel = new PiecePanel();
		panel.add(nextPiecePanel);
		
		// COUNT
		countLabel = new JLabel("0");
		seedDisplay = new JLabel();
		panel.add(countLabel);
		
		// TIME 
		timeLabel = new JLabel(" ");
		panel.add(timeLabel);

		panel.add(Box.createVerticalStrut(12));
		
		// START button
		startButton = new JButton("Start");
		panel.add(startButton);
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		// STOP button
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		
		// SEED button
		startSeed = new JButton("Seed");
		panel.add(startSeed);
		startSeed.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(seq.getText().length()==0)
  				startGame(new Random().nextInt(10));
				else
				{
					startGame(Integer.parseInt(seq.getText()));
				}
			}
		});
		
		// Seed textfield
		seq = new JFormattedTextField(NumberFormat.getIntegerInstance());
		panel.add(seq);
		
		enableButtons();
		

                
		JPanel row = new JPanel();
		
		// SPEED slider
		panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Speed:"));
		speed = new JSlider(0, DELAY, 75);	// min, max, current
		speed.setPreferredSize(new Dimension(100,15));
                
		
		updateTimer();
		row.add(speed);
		
		panel.add(row);
		speed.addChangeListener( new ChangeListener() {
			// when the slider changes, sync the timer to its value
			public void stateChanged(ChangeEvent e) {
				updateTimer();
			}
           
		});
		

		
		return(panel);
	}
	

	
	/**
	 Creates a Window,
	 installs the JTetris or JBrainTetris,
	 checks the testMode state,
	 install the controls in the WEST.
	*/
	public static void main(String[] args)
	
	{
		JFrame frame = new JFrame("TETRIS A.I.");
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
		JTetris tetris = new JBrainTetris(TetrisController.WIDTH*pixels+2, (TetrisController.HEIGHT+TetrisController.TOP_SPACE)*pixels+2);	
		
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

		// Quit on window close
		frame.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
	}
}
	
