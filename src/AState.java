import java.awt.Point;
import java.util.LinkedList;

import javax.swing.Timer;

/**
 * Struct-like class holding information about the current state of animation.
 * 
 * @author joshua
 *
 */
public class AState
{	
	public LinkedList<Byte> path; // Path to solution. 
	public Timer timer; // Timer that will enable animation.
	public Location loc; // Location on grid of moving tile.
	
	Point tilePos; // Position on panel tile is located.
	Tile tile; // The tile itself.
	
	Byte dir; // Direction of move.
	
	boolean finish; // Determines if animation is finished.
	
	// Goal x and y values.
	int gx;
	int gy;
	
	double animation; // Progress of animation.
	double rate; // Speed of progress update.
	
	public AState()
	{
		path = null;
		timer = null;
		loc = null;
		tilePos = null;
		tile = null;
		dir = -1;
		finish = true;
	}
}