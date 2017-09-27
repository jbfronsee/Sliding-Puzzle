/**
 * Class representing a Tile on the puzzle. 
 * 
 * @author joshua
 *
 */
public class Tile
{
	public int x, y, width, height; //x and y positions in the image.
	public boolean empty;
	private final Location trueLoc; // True location of tile on the grid.
	
	/**
	 * Construct a Tile with trueLoc as it's location.
	 * 
	 * @param trueLoc
	 */
	public Tile(Location trueLoc)
	{
		if(trueLoc == null)
			trueLoc = new Location();
		
		this.trueLoc = trueLoc;
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		empty = false;
	}
	
	/**
	 * Construct a Tile and initialize all of its member variables.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param trueLoc
	 * @param empty
	 */
	public Tile(int x, int y, int width, int height, Location trueLoc, boolean empty)
	{
		if(trueLoc == null)
			trueLoc = new Location();
		
		this.trueLoc = trueLoc;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.empty = empty;
	}
	
	/**
	 * Gets the true (correct) location.
	 * 
	 * @return Location trueLoc
	 */
	public Location getTrueLoc()
	{
		return trueLoc;
	}
	
	/**
	 * Returns a copy of the Tile.
	 * 
	 * @return Tile
	 */
	public Tile getCopy()
	{
		Location loc = new Location(trueLoc.row, trueLoc.col, trueLoc.pos);
		return new Tile(x,y,width,height,loc,empty);
	}
}