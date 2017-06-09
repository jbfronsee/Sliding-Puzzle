import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class PuzzlePanel extends JPanel
{
	private static final int DEFAULT_WIDTH = 400, DEFAULT_HEIGHT = 500;
	
	private BufferedImage loaded; // Loaded Image scaled to the Dimension of this panel.
	private Dimension pSize; // Dimension of this panel.
	
	private Rectangle grid[]; // The grid for the puzzle game empty tile is set to null.
	private int emptyTile; // Index representing the empty tile.
	
	/**
	 * Constructs the PuzzlePanel. Sets preferredSize to pSize.
	 */
	public PuzzlePanel()
	{
		super();
		loaded = null;
		pSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		grid = new Rectangle[9];
		emptyTile = -1;
		setPreferredSize(pSize);
	}
	
	/**
	 * Makes a move based on what tile p lies in. If it is next to an empty tile 
	 * swaps the empty tile with the chosen tile.
	 * 
	 * @param p - The point the user clicked within the panel.
	 * @return true if successful false otherwise.
	 */
	public boolean makeMove(Point p)
	{
		// Calculate the index of the selected tile.
		int width = pSize.width/3, height = pSize.height/3;
		int wInd = p.x/width, hInd = p.y/height;
		int tile = hInd*3 + wInd;
		
		// Difference of 3 -> up or down. Difference of 1 -> left or right.
		int diff = Math.abs(emptyTile - tile);
		if(diff == 3 || diff == 1)
		{
			grid[emptyTile] = grid[tile];
			grid[tile] = null;
			emptyTile = tile;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns loaded image file.
	 * 
	 * @return loaded
	 */
	public BufferedImage getImage()
	{
		return loaded;
	}
	
	public void loadImage(BufferedImage image)
	{
		// Create BufferedImage and get graphics context.
		loaded = new BufferedImage(pSize.width, pSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = loaded.createGraphics();
		AffineTransform Tx = null;
		
		// Calculate scale values so image will cover the panel.
		double sx, sy;
		sx = 1.0/(image.getWidth()/pSize.getWidth());
		sy = 1.0/(image.getHeight()/pSize.getHeight());
		Tx = AffineTransform.getScaleInstance(sx, sy);
		
		g2d.drawRenderedImage(image, Tx);
		
		// Split the image into 9 rectangles with the last being empty.
		int x = 0, y = 0, count = 0, width = pSize.width/3, height = pSize.height/3;
		for(int i = 0; i < grid.length - 1; i++)
		{
			grid[i] = new Rectangle(x,y,width,height);
			
			if(count >= 2)
			{
				y += height;
				x = 0;
				count = -1;
			}
			else
			{
				x += width;
			}
			
			count++;
		}
		
		grid[grid.length - 1] = null;
		emptyTile = grid.length - 1;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(loaded != null)
		{
			g.clearRect(0, 0, pSize.width, pSize.height);
			
			// Draw each rectangle in order.
			int x = 0, y = 0, count = 0, width = pSize.width/3, height = pSize.height/3;;
			for(int i = 0; i < grid.length; i++)
			{
				Rectangle rect = grid[i];
				
				if(rect != null)
					g.drawImage(loaded.getSubimage(rect.x, rect.y, rect.width, rect.height), x, y, null);
				
				if(count >= 2)
				{
					y += height;
					x = 0;
					count = -1;
				}
				else
				{
					x += width;
				}
				
				count++;
			}
		}
	}
}