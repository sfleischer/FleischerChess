
/**
 * This is an immutable location class
 * @author sfleischer
 *
 */
public class Location implements Comparable<Location>{
	public final int row; //keeps the chess location of the piece (row)
	public final int col;
	public final int rank;
	public final int file;
	
	//cardinal constants
	public static final int NORTH = 90;
	public static final int EAST = 0;
	public static final int WEST = 180;
	public static final int SOUTH = 270;
	public static final int NORTHEAST = 45;
	public static final int NORTHWEST = 135;
	public static final int SOUTHWEST = 225;
	public static final int SOUTHEAST = 315;
	
	/**
	 * 
	 * @param row
	 * @param column
	 */
	public Location(int row, int column){
		this.row = row;
		this.col = column;
		rank = row + 1;
		file = col + 1;
	}
	
	public Location(String loc){
		char[] arr = loc.toLowerCase().toCharArray();
		col = (int) arr[0] - (int) 'a';
		row = Integer.parseInt(loc.substring(1)) - 1;
		rank = row + 1;
		file = col + 1;
	}
	
	public static boolean isLocationOutOfBounds(Location loc){
		return loc.row < 0 || loc.row > 7 || loc.col < 0 || loc.col > 7;
	}
	
	public Location getAdjacentLocation(int direction){
		direction = (360 + direction) % 360;
		switch(direction){
			case NORTH: return new Location(row + 1, col);
			case SOUTH: return new Location(row - 1, col);
			case EAST: return new Location(row, col + 1);
			case WEST: return new Location(row, col - 1);
			case NORTHEAST: return new Location(row + 1, col + 1);
			case NORTHWEST: return new Location(row + 1, col - 1);
			case SOUTHEAST: return new Location(row - 1, col + 1);
			case SOUTHWEST: return new Location(row - 1, col - 1);
		}
		return null;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return col;
	}
	
	public String getNotation(){
		char letter = (char) (col + (int) 'a');
		return "" + letter + rank;
	}
	
	@Override
	public String toString(){
		return "(" + row + ", " + col + ")";
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(!o.getClass().equals(this.getClass()))
			return false;
		Location loc = (Location) o;
		return loc.row == row && loc.col == col;
	}

	@Override
	public int compareTo(Location loc) {
		int dy = 8 * (this.row - loc.row);
		int dx = (this.col - loc.col);
		return dx + dy;
	}
	
}
