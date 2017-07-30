import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import javax.swing.*;

public class PuzzlePanel extends JPanel
{
	private BufferedImage loaded; // Loaded Image scaled to the Dimension of this panel.
	private Dimension pSize; // Dimension of this panel.

	private Tile grid[][]; // The grid for the puzzle game empty tile is set to null.
	private Point positions[][]; // Position on panel the corresponding tile in grid will be drawn.
	private Location emptyLoc; // Location representing empty tile.
	
	private AState anim;

	/**
	 * Constructs the PuzzlePanel. Sets preferredSize to pSize.
	 */
	public PuzzlePanel()
	{
		super();
		loaded = null;
		pSize = new Dimension(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
		grid = new Tile[Constants.GRID_ROOT][Constants.GRID_ROOT];
		positions = new Point[Constants.GRID_ROOT][Constants.GRID_ROOT];
		emptyLoc = new Location();
		anim = new AState();
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
		int width = pSize.width/Constants.GRID_ROOT, height = pSize.height/Constants.GRID_ROOT;
		int row = p.y/height, col = p.x/width;

		int diffRow = Math.abs(emptyLoc.row - row),
				diffCol = Math.abs(emptyLoc.col - col);
		if((diffRow == 1 && diffCol == 0) || (diffCol == 1 && diffRow == 0))
		{
			grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
			grid[row][col] = null;
			emptyLoc.row = row;
			emptyLoc.col = col;
			emptyLoc.pos = emptyLoc.row*Constants.GRID_ROOT + emptyLoc.col;

			return true;
		}

		return false;
	}
	
	/**
	 * Moves the empty tile in a direction specified from Constants class.
	 * 
	 * @param direction - the direction the empty tile will move 
	 * @return true if successful false otherwise.
	 */
	public Location makeMove(byte direction)
	{
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
			grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
			grid[row][col] = null;
			emptyLoc.row = row;
			emptyLoc.col = col;
			emptyLoc.pos = emptyLoc.row*Constants.GRID_ROOT + emptyLoc.col;
		}
		return loc;
	}

	/**
	 * Scrambles the puzzle using a random number generator.
	 */
	public void scramble()
	{
		Random rand = new Random();
		int oldPos = -1; // Tracks the old position to prevent undoing a move.
		boolean conflict; // For checking another move in case of a conflict.

		for(int i = 0; i < 2*Constants.GRID_ROOT*Constants.GRID_ROOT; i++)
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
						pos = emptyLoc.pos + Constants.GRID_ROOT;
					}
					else if(emptyLoc.row == grid.length - 1)
					{
						row = emptyLoc.row - 1;
						pos = emptyLoc.pos - Constants.GRID_ROOT;
					}
					else
					{
						int upordown = rand.nextInt(2); // Selects tile above or below the empty tile.
						row = emptyLoc.row + (2*upordown - 1);
						pos = row*Constants.GRID_ROOT + emptyLoc.col;

						if(pos == oldPos)
						{
							// If there is a conflict move the other tile.
							row = emptyLoc.row + (-2*upordown + 1);
							pos = row*Constants.GRID_ROOT + emptyLoc.col;
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
						pos = emptyLoc.row*Constants.GRID_ROOT + col;

						if(pos == oldPos)
						{
							// If there is a conflict move other tile.
							col = emptyLoc.col + (-2*leftorright + 1);
							pos = emptyLoc.row*Constants.GRID_ROOT + col;
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

			grid[emptyLoc.row][emptyLoc.col] = grid[row][col];
			grid[row][col] = null;
			emptyLoc.row = row;
			emptyLoc.col = col;

			oldPos = emptyLoc.pos;
			emptyLoc.pos = pos;
		}
	}

	public void solve()
	{
		// Set up the goal state.
		byte[] goalGrid = new byte[Constants.GRID_ROOT*Constants.GRID_ROOT];
		for(byte i = 0; i < goalGrid.length - 1; i++)
		{
			goalGrid[i] = i;
		}
		
		goalGrid[goalGrid.length - 1] = -1;
		PState goal = new PState(goalGrid, (byte)(goalGrid.length - 1), (short)0, null);
		
		// Convert the current grid configuration into state form.
		byte[] rootGrid = new byte[Constants.GRID_ROOT*Constants.GRID_ROOT];
		byte rEmpty = 0;

		byte pos;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				pos = (byte) (i*Constants.GRID_ROOT + j);
				if(grid[i][j] != null)
				{
					rootGrid[pos] = (byte)grid[i][j].getTrueLoc().pos;
				}
				else
				{
					rootGrid[pos] = -1;
					rEmpty = pos;
				}
			}
		}

		PState root = new PState(rootGrid, rEmpty, (short)0, null);
		
		// Priority queue of possible solutions.
		PriorityQueue<PState> open = new PriorityQueue<PState>(10, new Comparator<PState>()
		{
			// Computes f(n) = g(n) + h(n). Actual cost from root to curr + estimated cost to goal.
			@Override
			public int compare(PState s1, PState s2)
			{
				int f1 = s1.g + s1.h;
				int f2 = s2.g + s2.h;
				return f1 - f2;
			}
		});
		
		// List of states already checked.
		ArrayList<PState> closed = new ArrayList<PState>();
		
		PState currState = root;
		while(!currState.equals(goal))
		{
			ArrayList<PState> successors = currState.genSuccessors();
			for(PState state: successors)
			{
				if(!closed.contains(state))
				{
					open.add(state);
				}
			}
			
			closed.add(currState);
			currState = open.poll();
		}
		
		anim.path = currState.constructPath();
		
		String pString = "[";
		for(Byte dir: anim.path)
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
		
		animateSolution();

	}
	
	public void animateSolution()
	{
		if(anim.path == null)
			return;
		
		anim.timer = new Timer(100, null);
		ActionListener al = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent ae) 
			{
				if(anim.finish)
				{
					anim.dir = anim.path.poll();
					if(anim.dir == null)
					{
						anim.timer.stop();
					}
					else
					{
						anim.loc = makeMove(anim.dir);
						anim.tilePos = positions[anim.loc.row][anim.loc.col];
						anim.tile = grid[anim.loc.row][anim.loc.col];
						anim.gx = anim.tilePos.x;
						anim.gy = anim.tilePos.y;
						
						switch(anim.dir.byteValue())
						{
							case Constants.LEFT:
								anim.tilePos.x = anim.gx - anim.tile.width;
								anim.animation = -.99;
								break;
							case Constants.RIGHT:
								anim.tilePos.x = anim.gx + anim.tile.width;
								anim.animation = .99;
								break;
							case Constants.UP:
								anim.tilePos.y = anim.gy - anim.tile.height;
								anim.animation = -.99;
								break;
							case Constants.DOWN:
								anim.tilePos.y = anim.gy + anim.tile.height;
								anim.animation = .99;
								break;
							default:
								break;
						}
						
						
						// Should change magic numbers to constants. ****************************
						anim.rate = .1;
						anim.finish = false;
					}
				}
				else
				{
					switch(anim.dir.byteValue())
					{
						case Constants.LEFT:
							if(anim.tilePos.x < anim.gx)
							{
								anim.tilePos.x = anim.gx + (int)(anim.animation*anim.tile.width);
								anim.animation += anim.rate;
							}
							else
							{
								anim.tilePos.x = anim.gx;
								anim.finish = true;
							}
							break;
						case Constants.RIGHT:
							if(anim.tilePos.x > anim.gx)
							{
								anim.tilePos.x = anim.gx + (int)(anim.animation*anim.tile.width);
								anim.animation -= anim.rate;
							}
							else
							{
								anim.tilePos.x = anim.gx;
								anim.finish = true;
							}
							break;
						case Constants.UP:
							if(anim.tilePos.y < anim.gy)
							{
								anim.tilePos.y = anim.gy + (int)(anim.animation*anim.tile.height);
								anim.animation += anim.rate;
							}
							else
							{
								anim.tilePos.y = anim.gy;
								anim.finish = true;
							}
							break;
						case Constants.DOWN:
							if(anim.tilePos.y > anim.gy)
							{
								anim.tilePos.y = anim.gy + (int)(anim.animation*anim.tile.height);
								anim.animation -= anim.rate;
							}
							else
							{
								anim.tilePos.y = anim.gy;
								anim.finish = true;
							}
							break;
						default:
							break;
					}
					
					repaint();
				}
			}
		};
		
		anim.timer.addActionListener(al);
		anim.timer.start();
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

		// Split the image into 9 Tiles with the last being empty.
		int x = 0, y = 0, width = pSize.width/Constants.GRID_ROOT, height = pSize.height/Constants.GRID_ROOT;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[i].length; j++)
			{
				// Ordering from left to right top to bottom
				grid[i][j] = new Tile(x,y,width,height, new Location(i,j, i*Constants.GRID_ROOT + j));
				positions[i][j] = new Point(x,y);
				x += width;
			}

			x = 0;
			y += height;
		}

		emptyLoc.row = grid.length - 1;
		emptyLoc.col = grid.length -1;
		emptyLoc.pos = emptyLoc.row*Constants.GRID_ROOT + emptyLoc.col;
		grid[emptyLoc.row][emptyLoc.col] = null;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(loaded != null)
		{
			g.clearRect(0, 0, pSize.width, pSize.height);

			// Draw each Tile in order.
			//int x = 0, y = 0, width = pSize.width/Constants.GRID_ROOT, height = pSize.height/Constants.GRID_ROOT;
			for(int i = 0; i < grid.length; i++)
			{
				for(int j = 0; j < grid[i].length; j++)
				{
					Tile tile = grid[i][j];
					if(tile != null)
						g.drawImage(loaded.getSubimage(tile.x, tile.y, tile.width, tile.height), positions[i][j].x, positions[i][j].y, null);

					//x += width;
				}

				//x = 0;
				//y += height;
			}
		}
	}
}