package tetris;
import java.awt.Color;
import java.awt.Point;


public class DisplayPiece extends Piece {
	protected static DisplayPiece[] pieces;	// singleton array of first rotations
	String label = "";
	Color color;
	protected DisplayPiece next;	// "next" rotation
	
	public DisplayPiece(Point[] points) {
		super(points);
	}
	
	public DisplayPiece(String label, Color color, Point[] points) {
		this(points);
		this.label = label;
		this.color = color;
	}
	
	/**
	 Returns a piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 
	 <p>Implementation:
	 The Piece class pre-computes all the rotations once.
	 This method just hops from one pre-computed rotation
	 to the next in constant time.
	*/	
	public DisplayPiece nextRotation() {
		return next;
	}
	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #nextRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	 (provided code)
	*/
	public static DisplayPiece[] getPieces() {
		// lazy evaluation -- create array if needed
		if (pieces==null) {
		
			// use pieceRow() to compute all the rotations for each piece
			pieces = new DisplayPiece[] {
				pieceRow(new DisplayPiece("i", Color.cyan, parsePoints("0 0	0 1	0 2	0 3"))),	// 0
				pieceRow(new DisplayPiece("j", Color.blue, parsePoints("0 0	0 1	0 2	1 0"))),	// 1
				pieceRow(new DisplayPiece("l", Color.pink, parsePoints("0 0	1 0	1 1	1 2"))),	// 2
				pieceRow(new DisplayPiece("z", Color.red, parsePoints("0 0	1 0	1 1	2 1"))),	// 3
				pieceRow(new DisplayPiece("s", Color.green, parsePoints("0 1	1 1	1 0	2 0"))),	// 4
				pieceRow(new DisplayPiece("o", Color.yellow, parsePoints("0 0	0 1	1 0	1 1"))),	// 5
				pieceRow(new DisplayPiece("t", Color.magenta, parsePoints("0 0	1 0	1 1	2 0"))),	// 6
			};
		}
		
		return(pieces);
	}
	
	protected static DisplayPiece pieceRow(DisplayPiece root) {
        DisplayPiece temp = root;
        DisplayPiece prev = root;
        for(;;) {
            prev = temp;
            prev.setPieceDims();
            prev.setPieceSkirt();
            temp = new DisplayPiece(prev.label, prev.color, prev.body);
            temp = temp.rotatePiece();
            if(!temp.equals(root)) {             
                prev.next = temp;
            }
            else
            {
                prev.next = root;
                break;
            }
        } 
        
        
        return root;
	}
	
    protected DisplayPiece rotatePiece() {
        DisplayPiece piece = null;
        Point[] temp = new Point[body.length];
        // switch x,y to y,x
        for(int i = 0; i < body.length; i++) {
            temp[i] = new Point();
            temp[i].x = body[i].y;
            temp[i].y = body[i].x;
        }
        piece = new DisplayPiece(label, color, temp);
        piece.setPieceDims();
        
        for(int i = 0; i < piece.body.length; i++) {
            temp[i].x = (piece.width-1) - piece.body[i].x;
            temp[i].y = piece.body[i].y;
        }
        piece = new DisplayPiece(label, color, temp);
        return(piece);
    }
}
