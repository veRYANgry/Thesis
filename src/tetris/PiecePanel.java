package tetris;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;


public class PiecePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 535704694816436932L;
	private DisplayPiece piece;
	public int blockSize = 10;
	
	public void setPiece(DisplayPiece piece) {
		this.piece = piece;
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(blockSize * 6, blockSize * 5);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (piece != null) {
			g.setColor(piece.color);
			for (Point block : piece.body) {
				g.fillRect(block.x*(blockSize+2)+blockSize, blockSize*3 - block.y*(blockSize+2)+blockSize, blockSize, blockSize);
			}
		}
	}
}
