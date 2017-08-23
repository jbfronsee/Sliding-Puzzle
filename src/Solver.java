import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Solver {

private PState root;
private PState goal;

boolean optimal;
boolean multi;

private LinkedList<PState> path;
	
public Solver()
{
	root = null;
	goal = null;
	optimal = true;
	multi = false;
}

public PState solve(PState root, int goalEmpty)
{
	this.root = root;
	return solve(Options.GRID_ROOT, goalEmpty);
}

	private PState solve(int n, int goalEmpty)
	{
	
		if(n > 3)
		{
			multi = true;
			
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
			
			goal = new PState(goalGrid, (byte)goalEmpty, (short)0, null);
			
			PState solution;
			if(n > 4)
			{
				optimal = false;
				
				solution = idastarSearch();
				root = solution;
			}
			else
			{
				optimal = true;
				
				solution = idastarSearch();
				root = solution;
			}
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
			goal = new PState(goalGrid, (byte)(goalEmpty), (short)0, null);
			
			optimal = true;
			multi = false;
			
			PState solution = astarSearch();
			return solution;
		}
	}
	
	

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
					int f1 = s1.g + s1.h;
					int f2 = s2.g + s2.h;
					return f1 - f2;
				}
			});
		}
		else // Perform A* search with out an admissible heuristic. Improves speed of the search.
		{
			open = new PriorityQueue<PState>(10, new Comparator<PState>()
			{
				// Computes f(n) = h(n). Estimated cost to goal.
				@Override
				public int compare(PState s1, PState s2)
				{
					int f1 = s1.g + 2*s1.h;
					int f2 = s2.g + 2*s2.h;
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

	private PState idastarSearch()
	{
		int FOUND = -1;
		path = new LinkedList<PState>();
		path.push(root);

		int thresh = root.h;
		int t = Integer.MAX_VALUE;

		while(t != FOUND)
		{
			t = dfs(root, thresh);
			thresh = t;
		}

		return path.pop();
	}

	private int dfs(PState state, int thresh)
	{	
		int f;
		if(optimal)
			f = state.g + state.h;
		else
			f = state.g + 2*state.h;
		if(f > thresh)
			return f;
		if(state.isGoal(goal, multi))
			return -1;

		int min = Integer.MAX_VALUE;
		for(PState p: state.genSuccessors())
		{
			if(!path.contains(p))
			{	
				path.push(p);
				int t = dfs(p,thresh);
				if(t == -1)
					return -1;
				if(t < min)
					min = t;
				path.pop();
			}
		}

		return min;
	}

}