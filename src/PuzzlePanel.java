import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.*;

/**
 * An extended JPanel that creates an n-puzzle component.
 * 
 * @author joshua
 *
 */
public class PuzzlePanel extends JPanel
{
	private BufferedImage loaded; // Loaded Image scaled to the Dimension of this panel.
	private Dimension pSize; // Dimension of this panel.

	private Tile grid[][]; // The grid for the puzzle game.
	private Point positions[][]; // Position on panel the corresponding tile in grid will be drawn.
	private Location emptyLoc; // Location representing empty tile.

	private Tile highlight; // The tile that will have a highlight around it.

	/**
	 * Constructs the PuzzlePanel. Sets preferredSize to pSize.
	 */
	public PuzzlePanel()
	{
		super();
		loaded = null;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pSize = new Dimension((int)(screenSize.width/Constants.fracWidth), 
								(int)(screenSize.height/Constants.fracHeight));
		grid = new Tile[Options.GRID_ROOT][Options.GRID_ROOT];
		positions = new Point[Options.GRID_ROOT][Options.GRID_ROOT];
		emptyLoc = null;
		highlight = null;
		setPreferredSize(pSize);
	}

	/**
	 * Makes a move based on what tile p lies in. If it is next to an empty tile 
	 * swaps the empty tile with the chosen tile.
	 * 
	 * @param p - The point the user clicked within the panel.
	 * @return true if successful false otherwise.
	 */
	public boolean playerMove(Point p)
	{
		if(p == null || emptyLoc == null || loaded == null)
			return false;
		
		// Calculate the index of the selected tile.
		int width = pSize.width/Options.GRID_ROOT, height = pSize.height/Options.GRID_ROOT;
		int row = p.y/height, col = p.x/width;
		if(row < grid.length && col < grid.length)
		{
			int diffRow = Math.abs(emptyLoc.row - row),
					diffCol = Math.abs(emptyLoc.col - col);
			if((diffRow == 1 && diffCol == 0) || (diffCol == 1 && diffRow == 0))
			{
				Tile temp = grid[emptyLoc.row][emptyLoc.col];
				grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
				grid[row][col] = temp;
				emptyLoc.row = row;
				emptyLoc.col = col;
				emptyLoc.pos = emptyLoc.row*Options.GRID_ROOT + emptyLoc.col;

				return true;
			}
			else if(diffRow == 0 && diffCol == 0)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Moves the empty tile in a direction specified from Constants class.
	 * 
	 * @param direction - the direction the empty tile will move 
	 * @return loc - the old location of emptyLoc
	 */
	public Location computerMove(byte direction)
	{
		if(emptyLoc == null || loaded == null)
			return null;
		
		Location loc = null;
		boolean success = false;
		int row = -1, col = -1;
		switch(direction)
		{
		case Constants.LEFT:
			col = emptyLoc.col - 1;
			if(col >= 0)
			{
				row = emptyLoc.row;
				success = true;
			}
			break;
		case Constants.RIGHT:
			col = emptyLoc.col + 1;
			if(col < grid[0].length)
			{
				row = emptyLoc.row;
				success = true;
			}
			break;
		case Constants.UP:
			row = emptyLoc.row - 1;
			if(row >= 0)
			{
				col = emptyLoc.col;
				success = true;
			}
			break;
		case Constants.DOWN:
			row = emptyLoc.row + 1;
			if(row < grid.length)
			{
				col = emptyLoc.col;
				success = true;
			}
			break;
		default:
			break;
		}

		if(success)
		{
			loc = new Location(emptyLoc.row, emptyLoc.col, 0);
			Tile temp = grid[emptyLoc.row][emptyLoc.col];
			grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
			grid[row][col] = temp;
			emptyLoc.row = row;
			emptyLoc.col = col;
			emptyLoc.pos = emptyLoc.row*Options.GRID_ROOT + emptyLoc.col;
		}
		return loc;
	}
	
	/**
	 * Checks if the board configuration is the goal (all
	 * tiles are in their true location).
	 * 
	 * @return true if goal false otherwise.
	 */
	public boolean goalCheck()
	{
		if(emptyLoc == null || loaded == null)
			return false;
		
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				Location loc = grid[i][j].getTrueLoc();
				if(loc.row != i || loc.col != j)
					return false;
			}
		}

		return true;
	}

	/**
	 * Scrambles the puzzle using a random number generator.
	 */
	public void scramble()
	{
		if(loaded == null)
			return;
		
		if(emptyLoc == null)
		{
			emptyLoc = new Location();
			emptyLoc.row = grid.length - 1;
			emptyLoc.col = grid.length - 1;
			emptyLoc.pos = emptyLoc.row*Options.GRID_ROOT + emptyLoc.col;
			
			grid[emptyLoc.row][emptyLoc.col].empty = true;
		}
		
		Random rand = new Random();
		int oldPos = -1; // Tracks the old position to prevent undoing a move.
		boolean conflict; // For checking another move in case of a conflict.

		for(int i = 0; i < Constants.NUM_PASSES*Options.GRID_ROOT*Options.GRID_ROOT; i++)
		{
			int vhflag = rand.nextInt(2); // Choose between vertical or horizontal selection.
			int row = 0, col = 0, pos = 0;

			conflict = true;
			while(conflict)
			{
				if(vhflag == 0)
				{
					col = emptyLoc.col;

					// Make vertical move.
					if(emptyLoc.row == 0)
					{
						row = emptyLoc.row + 1;
						pos = emptyLoc.pos + Options.GRID_ROOT;
					}
					else if(emptyLoc.row == grid.length - 1)
					{
						row = emptyLoc.row - 1;
						pos = emptyLoc.pos - Options.GRID_ROOT;
					}
					else
					{
						int upordown = rand.nextInt(2); // Selects tile above or below the empty tile.
						row = emptyLoc.row + (2*upordown - 1);
						pos = row*Options.GRID_ROOT + emptyLoc.col;

						if(pos == oldPos)
						{
							// If there is a conflict move the other tile.
							row = emptyLoc.row + (-2*upordown + 1);
							pos = row*Options.GRID_ROOT + emptyLoc.col;
							break;
						}
					}

					if(pos == oldPos)
					{
						vhflag = 1; // If there is a conflict do horizontal selection.
					}
					else
					{
						conflict = false;
					}
				}
				else
				{
					row = emptyLoc.row;

					// Make horizontal move.
					if(emptyLoc.col == 0)
					{
						col = emptyLoc.col + 1;
						pos = emptyLoc.pos + 1;
					}
					else if(emptyLoc.col == grid.length - 1)
					{
						col = emptyLoc.col - 1;
						pos = emptyLoc.pos - 1;
					}
					else
					{
						int leftorright = rand.nextInt(2); // Selects tile to left or right.
						col = emptyLoc.col + (2*leftorright - 1);
						pos = emptyLoc.row*Options.GRID_ROOT + col;

						if(pos == oldPos)
						{
							// If there is a conflict move other tile.
							col = emptyLoc.col + (-2*leftorright + 1);
							pos = emptyLoc.row*Options.GRID_ROOT + col;
							break;
						}
					}

					if(pos == oldPos)
					{
						vhflag = 0; // If there is a conflict do vertical selection.
					}
					else
					{
						conflict = false;
					}
				}
			}

			Tile temp = grid[emptyLoc.row][emptyLoc.col];
			grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
			grid[row][col] = temp;
			emptyLoc.row = row;
			emptyLoc.col = col;

			oldPos = emptyLoc.pos;
			emptyLoc.pos = pos;
		}
	}

	/**
	 * Solve the puzzle using Solver and return a list of moves to solve
	 * the puzzle.
	 * 
	 * @return LinkedList<Byte> path to solve the puzzle.
	 */
	public LinkedList<Byte> solve()
	{
		if(loaded == null || emptyLoc == null)
			return null;
		
		Solver sol = new Solver();

		// Convert the current grid configuration into state form.
		byte[] rootGrid = new byte[Options.GRID_ROOT*Options.GRID_ROOT];
		byte rEmpty = 0;

		byte pos;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				pos = (byte) (i*Options.GRID_ROOT + j);
				if(grid[i][j].empty == false)
				{
					rootGrid[pos] = (byte)grid[i][j].getTrueLoc().pos;
				}
				else
				{
					rootGrid[pos] = PState.EMPTY;
					rEmpty = pos;
				}
			}
		}

		PState root = new PState(rootGrid, rEmpty);
		PState solution = sol.solve(root, grid[emptyLoc.row][emptyLoc.col].getTrueLoc().pos);
		
		if(solution != null)
		{
		
			LinkedList<Byte> path = solution.constructPath();
		
			String pString = "[";
			for(Byte dir: path)
			{
				switch(dir.byteValue())
				{
				case Constants.LEFT:
					pString += "LEFT";
					break;
				case Constants.RIGHT:
					pString +="RIGHT";
					break;
				case Constants.UP:
					pString += "UP";
					break;
				case Constants.DOWN:
					pString += "DOWN";
					break;
				default:
					break;
				}
				pString += ", ";
			}

			pString += "]";

			System.out.println(pString);

			System.out.println(path.size());

			return path;
		}
		
		return null;
		//animateSolution();
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

	/**
	 * Loads a BufferedImage as a scaled image to panel dimensions.
	 * 
	 * @param image
	 */
	public void loadImage(BufferedImage image)
	{
		if (image == null)
			return;
		
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

		splitImage();
	}
	
	/**
	 * Splits the image into a nxn grid where n is Options.GRID_ROOT.
	 * "Split" is stored as a 2D array of Tiles.
	 */
	public void splitImage()
	{
		emptyLoc = null;
		
		grid = new Tile[Options.GRID_ROOT][Options.GRID_ROOT];
		positions = new Point[Options.GRID_ROOT][Options.GRID_ROOT];
		
		// Split the image into 9 Tiles with the last being empty.
		int x = 0, y = 0, width = pSize.width/Options.GRID_ROOT, height = pSize.height/Options.GRID_ROOT;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				// Ordering from left to right top to bottom
				grid[i][j] = new Tile(x,y,width,height, new Location(i,j, i*Options.GRID_ROOT + j), false);
				positions[i][j] = new Point(x,y);
				x += width;
			}

			x = 0;
			y += height;
		}
	}
	
	/**
	 * Reset highlight to null.
	 */
	public void unsetHighlight()
	{
		highlight = null;
	}
	
	/**
	 * Set highlight to a tile that p lies in.
	 * If p is not within the panel space does nothing.
	 * 
	 * @param Point p
	 */
	public void setHighlight(Point p)
	{
		if(p == null || loaded == null)
			return;
		
		unsetHighlight();
		int width = pSize.width/Options.GRID_ROOT, height = pSize.height/Options.GRID_ROOT;
		
		int row = p.y/height, col = p.x/width;
		
		if(row < grid.length && col < grid.length)
			highlight = grid[row][col];
	}
	
	/**
	 * Sets emptyLoc to the point p if it is within panel space.
	 * 
	 * @param Point p
	 */
	public void setEmptyLoc(Point p)
	{
		if(p == null || loaded == null)
			return;
		
		int width = pSize.width/Options.GRID_ROOT, height = pSize.height/Options.GRID_ROOT;
		
		int row = p.y/height, col = p.x/width;
		
		if(row < grid.length && col < grid.length)
		{
			emptyLoc = new Location();
			emptyLoc.row = row;
			emptyLoc.col = col;
			emptyLoc.pos = emptyLoc.row*Options.GRID_ROOT + emptyLoc.col;
			grid[emptyLoc.row][emptyLoc.col].empty = true;
		}
	}
	
	/**
	 * Resets empty to null.
	 */
	public void resetEmpty()
	{
		if (emptyLoc == null)
			return;
		
		grid[emptyLoc.row][emptyLoc.col].empty = false;
		emptyLoc = null;
	}
	
	/**
	 * Returns emptyLoc.
	 * 
	 * @return emptyLoc.
	 */
	public Location getEmptyLoc()
	{
		return emptyLoc;
	}
	
	/**
	 * Returns dimensions.
	 * 
	 * @return pSize
	 */
	public Dimension getDimensions()
	{
		return pSize;
	}
	
	/**
	 * Returns the positions of the tile at row and col.
	 * 
	 * @param row
	 * @param col
	 * @return Point on panel to draw Tile. Null if row and col
	 * are out of bounds.
	 */
	public Point getPosition(int row, int col)
	{
		if(row < positions.length && col < positions.length)
			return positions[row][col];
		else
			return null;
	}
	
	/**
	 * Returns a copy of the Tile in the grid.
	 * 
	 * @param row
	 * @param col
	 * @return Tile copy of tile. Null if row and col are out of bounds.
	 */
	public Tile getTile(int row, int col)
	{
		if(row < grid.length && col < grid.length)
			return grid[row][col].getCopy();
		else
			return null;
	}

	/**
	 * Paints this panel. Draws Tiles at respective positions and highlight if present.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		if(g == null)
			return;
		
		super.paintComponent(g);
		if(loaded != null)
		{
			g.clearRect(0, 0, pSize.width, pSize.height);

			int hposx = 0, hposy = 0;
			// Draw each Tile in order.
			for(int i = 0; i < grid.length; i++)
			{
				for(int j = 0; j < grid[i].length; j++)
				{
					Tile tile = grid[i][j];
					if(tile.empty != true)
						g.drawImage(loaded.getSubimage(tile.x, tile.y, tile.width, tile.height), positions[i][j].x, positions[i][j].y, null);
					
					if(tile == highlight)
					{
						hposx = positions[i][j].x;
						hposy = positions[i][j].y;
					}
				}
			}
			
			if(highlight != null)
			{
				Graphics2D g2d = (Graphics2D) g;
				Color oldc = g2d.getColor();
				Stroke olds = g2d.getStroke();
				g2d.setColor(new Color(255,255,0));
				g2d.setStroke(new BasicStroke(5));
				g2d.drawRect(hposx, hposy, highlight.width, highlight.height); 
				g2d.setStroke(olds);
				g2d.setColor(oldc);
			}
		}
	}
}