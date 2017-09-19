public class Tile
{
	public int x, y, width, height;
	public boolean empty;
	private final Location trueLoc; // True location of tile on the grid.
	
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
	
	public Location getTrueLoc()
	{
		return trueLoc;
	}
	
	public Tile getCopy()
	{
		Location loc = new Location(trueLoc.row, trueLoc.col, trueLoc.pos);
		return new Tile(x,y,width,height,loc,empty);
	}
}