package tetris;
import java.awt.Color;
import java.awt.Point;


public class DisplayBoard extends Board {
	Color[][] colorGrid;

	public DisplayBoard(int width, int height) {
		super(width, height);
		colorGrid = new Color[width][height];
	}
	
	public DisplayBoard(DisplayBoard o) {
		this(o.width, o.height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					grid[x][y] = o.grid[x][y];
					colorGrid[x][y] = o.colorGrid[x][y];
			}
		}
	}
	
	public DisplayBoard clone() {
		DisplayBoard cloned = new DisplayBoard(width, height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					cloned.grid[x][y] = grid[x][y];
					cloned.colorGrid[x][y] = colorGrid[x][y];
			}
		}
		
		return cloned;
	}
	
	public void place(DisplayPiece piece, int x, int y) {
		if (!canPlace(piece, x, y)) {
			return;
		}
		
		for (Point block : piece.getBody()) {
			if ((y + block.y) < height) {  //keeps it from trying to draw blocks above the screen.
				grid[x + block.x][y + block.y] = true;
				colorGrid[x + block.x][y + block.y] = piece.color;
			}
		}
	}
	
	public void place(Move move) {
		place((DisplayPiece)move.piece, move.x, move.y);
	}
	
	public int clearRows() {
		int cleared = 0;

		for (int i = 0; i < height; i++) {
			if (getRowCount(i) >= width) {
				cleared++;

				for (int j = 0; j < width; j++) {
					System
							.arraycopy(grid[j], i + 1, grid[j], i, height - 1
									- i);
					grid[j][height - 1] = false;
					colorGrid[j][i] = colorGrid[j][i + 1];	
				}

				i--;
			}
		}

		return cleared;
	}
}
