package tetris;
import java.util.Random;

public class TetrisController {
	// size of the board in blocks
	public static final int WIDTH = 10; //10
	public static final int HEIGHT = 4; //20
	// public static final int WIDTH = 100; //10
  // public static final int HEIGHT = 30; //20
	
	public int rowsCleared = 0;

	// Extra blocks at the top for pieces to start.
	// If a piece is sticking up into this area
	// when it has landed -- game over!
	public static final int TOP_SPACE = 4;

	// Board data structures
	public DisplayBoard displayBoard;
	public DisplayBoard board;
	public DisplayPiece[] pieces;

	// The current piece in play or null
	public DisplayPiece nextPiece; // The piece which will be generated next
	public Move currentMove;

	// State of the game
	public boolean gameOn;	// true if we are playing
	public int count;		// how many pieces played so far

	public Random random;	// the random generator for new pieces
	
	public TetrisController() {
		gameOn = false;

		pieces = DisplayPiece.getPieces();
		board = new DisplayBoard(WIDTH, HEIGHT + TOP_SPACE);
		displayBoard = new DisplayBoard(board);
	}

	public static final int ROTATE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DROP = 3;
	public static final int DOWN = 4;
	/**
	 Called to change the position of the current piece.
	 Each key press call this once with the verbs
	 LEFT RIGHT ROTATE DROP for the user moves,
	 and the timer calls it with the verb DOWN to move
	 the piece down one square.

	 Before this is called, the piece is at some location in the board.
	 This advances the piece to be at its next location.

	 Overriden by the brain when it plays.
	 * @throws Exception 
	 */
	public void tick(int verb) {
		if (!gameOn) return;

		// Sets the newXXX ivars
		Move newMove = computeNewPosition(verb, currentMove);
		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),  then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		 */
		if (board.canPlace(newMove)) {
			currentMove = newMove;
			displayBoard = new DisplayBoard(board);
			displayBoard.place(currentMove);
		} else if (verb==DOWN) {	// it's landed
			board.place(currentMove);
			
			rowsCleared += board.clearRows();
			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				gameOn = false;
			}
			else {// Otherwise add a new piece and keep playing
				addNewPiece();
			}
		}
	}

	/**
	 Figures a new position for the current piece
	 based on the given verb (LEFT, RIGHT, ...).
	 The board should be in the committed state --
	 i.e. the piece should not be in the board at the moment.
	 This is necessary so dropHeight() may be called without
	 the piece "hitting itself" on the way down.

	 Sets the ivars newX, newY, and newPiece to hold
	 what it thinks the new piece position should be.
	 (Storing an intermediate result like that in
	 ivars is a little tacky.)
	 */
	public Move computeNewPosition(int verb, Move currentMove) {
		// As a starting point, the new position is the same as the old
		
		Piece newPiece = currentMove.piece;
		int newX = currentMove.x;
		int newY = currentMove.y;

		// Make changes based on the verb
		switch (verb) {
		case LEFT: newX--; break;

		case RIGHT: newX++; break;

		case ROTATE:
			newPiece = newPiece.nextRotation();

			// tricky: make the piece appear to rotate about its center
			// can't just leave it at the same lower-left origin as the
			// previous piece.
			newX = newX + (currentMove.piece.getWidth() - newPiece.getWidth())/2;
			newY = newY + (currentMove.piece.getHeight() - newPiece.getHeight())/2;
			break;

		case DOWN: newY--; break;

		case DROP:
			newY = board.dropHeight(newPiece, newX);

			// trick: avoid the case where the drop would cause
			// the piece to appear to move up
			if (newY > currentMove.y) {
				newY = currentMove.y;
			}
			break;

		default:
			break;
		}
		
		Move newMove = new Move();
		newMove.piece = newPiece;
		newMove.x = newX;
		newMove.y = newY;
		
		return newMove;

	}

	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	 */
	public void startGame() {
		random = new Random();	// diff seq each game
		
		// cheap way to reset the board state
		board = new DisplayBoard(WIDTH, HEIGHT + TOP_SPACE);

		count = 0;
		rowsCleared = 0;
		gameOn = true;

		nextPiece = pickNextPiece();

		addNewPiece();

	}
	
	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	 */
	public void startGame(int seed) {
		random = new Random(seed);	// diff seq each game
		
		// cheap way to reset the board state
		board = new DisplayBoard(WIDTH, HEIGHT + TOP_SPACE);

		count = 0;
		gameOn = true;

		nextPiece = pickNextPiece();

		addNewPiece();
	}

	/**
	 Selects the next piece to use using the random generator
	 set in startGame().
	 */
	public DisplayPiece pickNextPiece() {	
		return pieces[random.nextInt(3)];
	}

	/**
	 Tries to add a new random piece at the top of the board.
	 Ends the game if it's not possible.
	 */
	public void addNewPiece() {
		count++;

		// Center it up at the top
		Move newMove = new Move();
		newMove.piece = nextPiece;
		newMove.x = (board.getWidth() - newMove.piece.getWidth())/2;
		newMove.y = board.getHeight() - newMove.piece.getHeight();
		
		nextPiece = pickNextPiece();
		
		if (board.canPlace(newMove)) {
			currentMove = newMove;
		} else {
			gameOn = false;
		}
	}
}