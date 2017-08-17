import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PState
{
	public static final byte EMPTY = -1;
	public static final byte FREE = -2;
	
	public byte move; // Represents the move taken to get to this state.
	
	public byte grid[]; // The state representation of the grid.
	public byte empty; // Location of the empty square.
	public short g; // Actual cost to get to this state.
	public short h; // Projected cost to get to goal state.
	
	private PState parent; // Parent state of this state.
	//private PState child;
	
	public PState(byte grid[], byte empty, short g, PState parent)
	{
		this.grid = grid;
		this.empty = empty;
		this.g = g;
		this.parent = parent;
		this.h = computeHeuristic();
		this.move = Constants.NONE;
	}
	
	public PState(byte grid[], byte empty, short g, PState parent, byte move)
	{
		this(grid,empty,g,parent);
		this.move = move;
	}
	
/*	public void setChild(PState child)
	{
		this.child = child;
	}
	
	public PState getChild()
	{
		return child;
	}*/
	
	public PState getParent()
	{
		return parent;
	}
	
	/**
	 * Computes Manhattan distance (horizontal distance + vertical distance) of state to goal.
	 * 
	 * @param state - array representing the state of the board.
	 * @return h - the computed heuristic
	 */
	public short computeHeuristic()
	{
		short h = 0;
		if(parent != null)
		{
			int val, row, col, nRow, nCol, oRow, oCol, newDiff, oldDiff;
		
			h = parent.h;
			
			val = grid[parent.empty];
			row = val/Options.GRID_ROOT;
			col = val%Options.GRID_ROOT;
			nRow = parent.empty/Options.GRID_ROOT;
			nCol = parent.empty%Options.GRID_ROOT;
			oRow = this.empty/Options.GRID_ROOT;
			oCol = this.empty%Options.GRID_ROOT;
			
			newDiff = Math.abs(nRow - row) + Math.abs(nCol - col);
			oldDiff = Math.abs(oRow - row) + Math.abs(oCol - col);
			
			h += newDiff - oldDiff;
		}
		else
		{
			int row, col, tRow, tCol;
			for(int i = 0; i < grid.length; i++)
			{
				if(grid[i] >= 0)
				{
					row = i/Options.GRID_ROOT;
					col = i%Options.GRID_ROOT;
					tRow = grid[i]/Options.GRID_ROOT;
					tCol = grid[i]%Options.GRID_ROOT;
					h += Math.abs(tRow - row) + Math.abs(tCol - col);
				}
			}
		}

		return h;
	}
	
	/**
	 * Generates the successor states for this state.
	 * 
	 * @return successors - ArrayList containing each successor state.
	 */
	public ArrayList<PState> genSuccessors()
	{
		// Return null if empty is out of bounds.
		if(empty >= grid.length || empty < 0)
			return null;

		ArrayList<PState> successors = new ArrayList<PState>();
		byte[] child;
		int eRow = empty/Options.GRID_ROOT;
		int eCol = empty%Options.GRID_ROOT;

		int newPos;
		if(eRow - 1 >= 0)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow - 1)*Options.GRID_ROOT + eCol;
			child[empty] = child[newPos];
			child[newPos] = EMPTY;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.UP));
		}

		if(eRow + 1 < Options.GRID_ROOT)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow + 1)*Options.GRID_ROOT + eCol;
			child[empty] = child[newPos];
			child[newPos] = EMPTY;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.DOWN));
		}

		if(eCol - 1 >= 0)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow)*Options.GRID_ROOT + (eCol - 1);
			child[empty] = child[newPos];
			child[newPos] = EMPTY;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.LEFT));
		}

		if(eCol + 1 < Options.GRID_ROOT)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow)*Options.GRID_ROOT + (eCol + 1);
			child[empty] = child[newPos];
			child[newPos] = EMPTY;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.RIGHT));
		}

		return successors;
	}
	
	/**
	 * Checks whether this state is a goal state. Uses .equals() if there
	 * is only one goal state otherwise it checks for FREE tiles.
	 * 
	 * @param goal - the goal state 
	 * @param multi - set this to true if there are free tiles in the goal state
	 * @return
	 */
	public boolean isGoal(PState goal, boolean multi)
	{	
		if(!multi)
			return this.equals(goal);
		
		if(goal == null)
			return false;
		
		if(grid.length != goal.grid.length)
			return false;
		
		for(int i = 0; i < grid.length; i++)
		{
			if((grid[i] != goal.grid[i]) && (goal.grid[i] != FREE))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Constructs the path to goal state using the parents of this state.
	 * 
	 * @return path - LinkedList of moves taken to get to this state.
	 */
	public LinkedList<Byte> constructPath()
	{
		LinkedList<Byte> path = new LinkedList<Byte>();
		PState currState = this;
		while(currState.parent != null)
		{
			path.addFirst(currState.move);
			currState = currState.parent;
		}
		return path;
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(grid);
	}
	
	/**
	 * Two states are considered equal if the grid representation is the same.
	 * 
	 * @return isEqual - true if they are the same false otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		
		PState state = null;
		if(obj instanceof PState)
		{
			state = (PState) obj;
			
			isEqual = Arrays.equals(state.grid, grid);
		}
		
		return isEqual;
	}
}