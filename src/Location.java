public class Location
{
	// Row and col for location pos is for ordering.
	public int row, col, pos; 
	
	public Location()
	{
		row = 0;
		col = 0;
		pos = 0;
	}
	
	public Location(int row, int col, int pos)
	{
		this.row = row;
		this.col = col;
		this.pos = pos;
	}
}