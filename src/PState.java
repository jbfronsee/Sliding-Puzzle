import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PState
{
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
		this.h = computeHeuristic(grid);
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
	public short computeHeuristic(byte state[])
	{
		short h = 0;
		int row, col, tRow, tCol;
		for(int i = 0; i < state.length; i++)
		{
			if(state[i] >= 0)
			{
				row = i/Constants.GRID_ROOT;
				col = i%Constants.GRID_ROOT;
				tRow = state[i]/Constants.GRID_ROOT;
				tCol = state[i]%Constants.GRID_ROOT;
				h += Math.abs(tRow - row) + Math.abs(tCol - col);
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
		int eRow = empty/Constants.GRID_ROOT;
		int eCol = empty%Constants.GRID_ROOT;

		int newPos;
		if(eRow - 1 >= 0)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow - 1)*Constants.GRID_ROOT + eCol;
			child[empty] = child[newPos];
			child[newPos] = -1;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.UP));
		}

		if(eRow + 1 < Constants.GRID_ROOT)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow + 1)*Constants.GRID_ROOT + eCol;
			child[empty] = child[newPos];
			child[newPos] = -1;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.DOWN));
		}

		if(eCol - 1 >= 0)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow)*Constants.GRID_ROOT + (eCol - 1);
			child[empty] = child[newPos];
			child[newPos] = -1;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.LEFT));
		}

		if(eCol + 1 < Constants.GRID_ROOT)
		{
			child = Arrays.copyOf(grid, grid.length);

			newPos = (eRow)*Constants.GRID_ROOT + (eCol + 1);
			child[empty] = child[newPos];
			child[newPos] = -1;
			successors.add(new PState(child, (byte)newPos, (short)(g + 1), this, Constants.RIGHT));
		}

		return successors;
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