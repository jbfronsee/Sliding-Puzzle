/**
 * A class representing a location on a 2D grid numbered from 
 * right to left, up and down from 0 to n.
 * 
 * @author joshua
 *
 */
public class Location
{
	// Row and col for location pos is for ordering.
	public int row, col, pos; 
	
	/**
	 * Default constructor initializes all members to 0.
	 */
	public Location()
	{
		row = 0;
		col = 0;
		pos = 0;
	}
	
	/**
	 * Initializes members based on arguments.
	 * 
	 * @param row
	 * @param col
	 * @param pos
	 */
	public Location(int row, int col, int pos)
	{
		this.row = row;
		this.col = col;
		this.pos = pos;
	}
}