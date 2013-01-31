package tetris;

public class Move {
	// Move is used as a struct to store a single Move
	// ("static" here means it does not have a pointer to an
	// enclosing Brain object, it's just in the Brain namespace.)
		public int x;
		public int y;
		public Piece piece;
}
