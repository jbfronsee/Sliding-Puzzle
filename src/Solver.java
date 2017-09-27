import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Solves the n-puzzle using A* search for the 8-puzzle and
 * a combination of IDA* and multiple goal states for n > 8.
 * 
 * @author joshua
 *
 */
public class Solver {

	private static final int FOUND = -1;
	// Gives greater weight to Heuristic slightly speeding up search.
	private static final int HIGH_H= 2; 

	private PState root;
	private PState goal;

	private boolean optimal;
	private int weight;

	private boolean multi;

	/**
	 * Constructs the solver with root and goal set to null.
	 */
	public Solver()
	{
		root = null;
		goal = null;
		optimal = true;
		weight = HIGH_H;
		multi = false;
	}

	/**
	 * Solves n-puzzle starting at root. Assumes goal is reachable from root.
	 * 
	 * @param root The starting state of the n-puzzle.
	 * @param goalEmpty The position of the empty tile in goal state.
	 * @return PState the goal state with path from root inside it.
	 */
	public PState solve(PState root, int goalEmpty)
	{
		if(root == null)
			return null;

		this.root = root;
		return solve(Options.GRID_ROOT, goalEmpty);
	}

	/**
	 * Solves 8-puzzle with A* and final goal. Solves greater n-puzzles
	 * with IDA* to find top left row and column then recursively solves an
	 * (n-1)-puzzle.
	 * 
	 * @param n The number of rows and columns.
	 * @param goalEmpty Position of the empty tile in goal.
	 * @return PState the final solved PState with path from root to goal.
	 */
	private PState solve(int n, int goalEmpty)
	{
		if(n > 4)
		{
			root = bigPuzzleSolve(n,goalEmpty);
			return solve(n-1,goalEmpty);
		}
		if(n == 4)
		{
			multi = true;

			// Make the goal only consider the 1st column and row of 4x4 grid/subgrid.
			byte[]goalGrid = new byte[Options.GRID_ROOT*Options.GRID_ROOT];
			int goalDepth = Options.GRID_ROOT - n;
			for(int i = 0, j = 0, depth = 0; depth < Options.GRID_ROOT;)
			{
				if(depth <= goalDepth)
				{
					goalGrid[i] = (byte)i;
					goalGrid[j] = (byte)j;
				}
				else
				{
					goalGrid[i] = PState.FREE;
					goalGrid[j] = PState.FREE;
				}

				i++; 
				j += Options.GRID_ROOT;
				if(i >= depth*Options.GRID_ROOT + Options.GRID_ROOT)
				{
					depth++;
					i = depth*Options.GRID_ROOT + depth;
					j = i;
				}
			}

			goalGrid[goalEmpty] = (goalGrid[goalEmpty] == PState.FREE) ? PState.FREE : PState.EMPTY;

			goal = new PState(goalGrid, (byte)goalEmpty);

			PState solution;
			optimal = false;
			weight = HIGH_H;
			solution = idastarSearch();
			root = solution;
			return solve(n-1, goalEmpty);
		}
		else
		{
			// Set up the goal state.
			byte[] goalGrid = new byte[Options.GRID_ROOT*Options.GRID_ROOT];
			for(byte i = 0; i < goalGrid.length; i++)
			{
				goalGrid[i] = i;
			}

			goalGrid[goalEmpty] = PState.EMPTY;
			goal = new PState(goalGrid, (byte)(goalEmpty));

			optimal = true;
			multi = false;

			PState solution = astarSearch();
			return solution;
		}
	}

	/**
	 * Solves puzzle one tile at a time. Starts with upper left tile
	 * then moves right until all tiles are in positon then moves down from upper left
	 * until first row and col of grid/subgrid is solved.
	 * 
	 * @param n The number of rows and columns.
	 * @param goalEmpty Positon of empty tile in goal.
	 * @return PState with all rows up until the first row and column of nxn grid/subgrid solved.
	 */
	private PState bigPuzzleSolve(int n, int goalEmpty)
	{
		multi = true;
		optimal = false;
		weight = HIGH_H;


		int goalDepth;
		int goalPos;
		int bounds;
		int modifier;
		for(int round = 1; round <= 2; round++)
		{
			/* GoalPos is the current goal tile. In round 1 it moves along the first row.
			 * in round 2 it moves along the first column.
			 * 
			 * Bounds are the end of the first row in round 1 and column in round 2.
			 * Modifier is the value to move to the next row in round 1 and column in round 2.*/
			if(round == 1)
			{
				goalDepth = Options.GRID_ROOT - n;
				goalPos = goalDepth*Options.GRID_ROOT + goalDepth;
				bounds = goalDepth*Options.GRID_ROOT + Options.GRID_ROOT;
				modifier = 1;
			}
			else
			{
				goalDepth = Options.GRID_ROOT - n;
				goalPos = goalDepth*Options.GRID_ROOT + goalDepth + Options.GRID_ROOT;
				bounds = Options.GRID_ROOT*(Options.GRID_ROOT - 1) + goalDepth + 1;
				modifier = Options.GRID_ROOT;
			}

			for(;goalPos < bounds; goalPos += modifier)
			{

				/* Construct the goal grid and solve one at a time until the row and column is solved. */
				byte[]goalGrid = new byte[Options.GRID_ROOT*Options.GRID_ROOT];

				for(int i = 0, j = 0, depth = 0; depth < Options.GRID_ROOT;)
				{
					if(depth < goalDepth)
					{
						goalGrid[i] = (byte)i;
						goalGrid[j] = (byte)j;
					}
					if(depth == goalDepth)
					{
						if(round == 1)
						{
							if(i <= goalPos)
							{
								goalGrid[i] = (byte)i;
							}
							else
							{
								goalGrid[i] = PState.FREE;
							}

							goalGrid[j] = PState.FREE;
						}
						else
						{
							if(j <= goalPos)
							{
								goalGrid[j] = (byte)j;
							}
							else
							{
								goalGrid[j] = PState.FREE;
							}
							
							goalGrid[i] = (byte)i;
						}
					}
					else
					{
						goalGrid[i] = PState.FREE;
						goalGrid[j] = PState.FREE;
					}

					i++; 
					j += Options.GRID_ROOT;
					if(i >= depth*Options.GRID_ROOT + Options.GRID_ROOT)
					{
						depth++;
						i = depth*Options.GRID_ROOT + depth;
						j = i;
					}
				}

				goalGrid[goalEmpty] = (goalGrid[goalEmpty] == PState.FREE) ? PState.FREE : PState.EMPTY;

				goal = new PState(goalGrid, (byte)goalEmpty);

				root = idastarSearch();
			}
		}

		return root;
	}
	
	/**
	 * Implements A* search on the n-puzzle. Works best with 8-puzzle. If 
	 * optimal is set it will find the shortest path from root to goal.
	 * 
	 * @return PState The PState with solution path from root to goal.
	 */
	private PState astarSearch()
	{
		// Priority queue of possible solutions.
		PriorityQueue<PState> open; 

		// Perform A* search
		if(optimal)
		{
			open = new PriorityQueue<PState>(10, new Comparator<PState>()
			{
				// Computes f(n) = g(n) + h(n). Actual cost from root to curr + estimated cost to goal.
				@Override
				public int compare(PState s1, PState s2)
				{
					int f1 = s1.getG() + s1.getH();
					int f2 = s2.getG() + s2.getH();
					return f1 - f2;
				}
			});
		}
		else // Perform A* search with out an admissible heuristic. Improves speed of the search without optimality.
		{
			open = new PriorityQueue<PState>(10, new Comparator<PState>()
			{
				// Computes f(n) = h(n). Estimated cost to goal.
				@Override
				public int compare(PState s1, PState s2)
				{
					int f1 = s1.getG() + weight*s1.getH();
					int f2 = s2.getG() + weight*s2.getH();
					return f1 - f2;
				}
			});
		}


		// List of states already checked.
		HashSet<PState> closed = new HashSet<PState>();

		PState currState = root;
		while(!currState.isGoal(goal, multi))
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

		return currState;
	}

	/**
	 * Implements an iterative version of IDA* search. If optimal is
	 * set it will find the shortest path from root to goal. Uses less memory than A*.
	 * Good for n-puzzle with n > 8.
	 * 
	 * @return PState The PState with solution path from root to goal.
	 */
	private PState idastarSearch()
	{
		PState state = null;

		// Threshold that will cut off the depth first search.
		int thresh = root.getH();
		while(thresh != FOUND && thresh < Integer.MAX_VALUE)
		{
			int min = Integer.MAX_VALUE;

			// Uses a stack to track next state in DFS.
			LinkedList<PState> stack = new LinkedList<PState>();
			stack.push(root);
			while(!stack.isEmpty())
			{
				state = stack.pop();
				if(!state.inPath(state))
				{
					int f;
					if(optimal)
						f = state.getG() + state.getH();
					else
						f = state.getG() + weight*state.getH();

					// Cut off at f > thresh and use minimum f for new thresh.
					if(f > thresh)
					{
						if(f < min)
							min = f;
					}
					else
					{
						if(state.isGoal(goal, multi))
						{
							thresh = FOUND;
							break;
						}

						stack.addAll(0, state.genSuccessors());
					}
				}
			}

			if(thresh != FOUND)
				thresh = min;
		}

		return state;
	}
}