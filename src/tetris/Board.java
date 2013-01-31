package tetris;
// Board.java
//package Hw2;
import java.awt.Point;

/**
 * Represents a Tetris board -- essentially a 2-d grid of booleans. Supports
 * tetris pieces and row clearning. Has an "undo" feature that allows clients to
 * add and remove pieces efficiently. Does not do any drawing or have any idea
 * of pixels. Intead, just represents the abtsract 2-d board. See
 * Tetris-Architecture.html for an overview.
 * 
 * This is the starter file version -- a few simple things are filled in already
 * 
 * @author Nick Parlante
 * @version 1.0, Mar 1, 2001
 */
public class Board {
	protected int width; // width of the board
	protected int height;
  private boolean caching = false;
	public boolean[][] grid;
  
	/**
	 * Creates an empty board of the given width and height measured in blocks.
	 */
	public Board(int aWidth, int aHeight) {
		width = aWidth;
		height = aHeight;

		grid = new boolean[width][height];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				grid[x][y] = false;
			}
		}
		
		columnHeightDirties = new boolean[width];
		_columnHeights = new int[width];
		for(int i=0; i<width; i++) {
		  columnHeightDirties[i] = true;
		  _columnHeights[i] = -1;
	  }
	}
	
	public Board(Board o) {
		this(o.width, o.height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					grid[x][y] = o.grid[x][y];
			}
		}
	}
	
	public Board clone() {
		Board cloned = new Board(width, height);
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
					cloned.grid[x][y] = grid[x][y];
			}
		}
		
		return cloned;
	}

  public void makeDirty() {
    maxHeightDirty = true;
    for(int i=0; i<width; i++) {
      columnHeightDirties[i] = true;
    }
  }

  //CACHING CAN ONLY BE ENABLED WHILE A BOARDRATER IS READING THE BOARD. I couldn't figure out where to mark the caches as outdated, so I just make sure caching is only enabled in read-only situations.
  public void enableCaching() {
    caching = true;
    makeDirty();
  }
  public void disableCaching() {
    caching = false;
  }

	/**
	 * Returns the width of the board in blocks.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the board in blocks.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the max column height present in the board. For an empty board
	 * this is 0.
	 */
	private boolean maxHeightDirty = true;
	private int _maxHeight = -1;
	public int getMaxHeight() {
    if(caching && !maxHeightDirty) return _maxHeight;
	  maxHeightDirty = false;
		int max = 0;
		for (int i = 0; i < width; i++) {
			if (getColumnHeight(i) > max)
				max = getColumnHeight(i);
		}
		return (_maxHeight = max);

	}

	/**
	 * Given a piece and an x, returns the y value where the piece would come to
	 * rest if it were dropped straight down at that x.
	 * 
	 * <p>
	 * Implementation: use the skirt and the col heights to compute this fast --
	 * O(skirt length).
	 */
	public int dropHeight(Piece piece, int x) {
		int high = 10000;
		int result = 0;
		int temp;
		int i;
		int[] skirt = piece.getSkirt();
		for (i = x; i < x + piece.getWidth(); i++) {
			temp = skirt[i - x] - getColumnHeight(i);
			if (temp < high) {
				high = temp;
				result = i;
			}
		}
		return getColumnHeight(result) - skirt[result - x];
	}

	/**
	 * Returns the height of the given column -- i.e. the y value of the highest
	 * block + 1. The height is 0 if the column contains no blocks.
	 */
	private boolean[] columnHeightDirties;
	private int[] _columnHeights;
	public int getColumnHeight(int x) {
      if(caching && !columnHeightDirties[x]) {
        return _columnHeights[x];
      }
      columnHeightDirties[x] = false;
			for (int j = height - 1; j >= 0; j--) {
				if (grid[x][j]) {
					return (_columnHeights[x] = j+1);
				}
			}
			return (_columnHeights[x] = 0);
	}

	/**
	 * Returns the number of filled blocks in the given row.
	 */
	public int getRowCount(int y) {
		int count = 0;
		
		for (int x = 0; x < width; x++) {
			if (grid[x][y]) {
				count++;
			}
		}
		
		return count;
	}

	/**
	 * Returns true if the given block is filled in the board. Blocks outside of
	 * the valid width/height area always return true.
	 * 
	 */
	public final boolean getGrid(int x, int y) {
		if (x < 0 || x > width)
			return true;
		if (y < 0 || y > height)
			return true;
		return grid[x][y];
	}

	public boolean canPlace(Piece piece, int x, int y) {
		// might as well do all the error checking at once, since we must leave
		// the board unchanged if we've got
		// an out of bounds error - this has the added bonus of not altering the
		// board on a bad place
		for (Point block : piece.getBody()) {
			int putx = x + block.x;
			int puty = y + block.y;
			
			if (putx < 0 
					|| putx >= width 
					|| puty < 0 
					|| ((puty < height) && grid[putx][puty])) {
				return false;
			}
		}
		return true;
	}
	
	public boolean canPlace(Move move) {
		return canPlace(move.piece, move.x, move.y);
	}
	
	public void place(Piece piece, int x, int y) {
		if (!canPlace(piece, x, y)) {
			return;
		}
		for (Point block : piece.getBody()) {
			grid[x + block.x][y + block.y] = true;
		}
	}
	
	public void place(Move move) {
		place(move.piece, move.x, move.y);
	}

	/**
	 * Deletes rows that are filled all the way across, moving things above
	 * down. Returns true if any row clearing happened.
	 * 
	 * <p>
	 * Implementation: This is complicated. Ideally, you want to copy each row
	 * down to its correct location in one pass. Note that more than one row may
	 * be filled.
	 */
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
				}

				i--;
			}
		}
		return cleared;

	}
}
