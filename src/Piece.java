
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class Piece implements Comparable<Piece>{
	protected Location loc; //location of the piece
	protected int worth; //the worth of the piece (Queen is 9, Bishop is 3, etc)
	protected Polarity side;
	protected BufferedImage image;
	
	/* Note that freshMove means different things for different pieces:
	 * For Pawn: freshMove is only true when it just moved two forward on the opening 
	 * move. It is immediately false when the opponent makes a move. This variable is
	 * used for en passant
	 * For King/Rook: freshMove is only true when the King or Rook did not make a move.
	 * As soon as it makes a move, freshMove is false. 
	 * For other pieces: freshMove has no significance.
	 */
	boolean freshMove = false; 
	
	/**
	 * 
	 * @param The location of the piece
	 */
	public Piece(Location location, int worth, Polarity p){
		loc = location;
		this.worth = worth;
		side = p;
	}
	
	
	public void draw(Graphics2D g2, int x, int y, int width, int height){
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D map = scaled.createGraphics();
        map.drawImage(image, 0, 0, width, width, null); 
        map.dispose();
		g2.drawImage(scaled, x, y, null);
	}
	
	
	/**
	 * 
	 * @return A list of legal moves that the piece can move.
	 */
	public Set<Location> getLegalMoves(ChessModel model){
		//find the preliminary moves
		Set<Location> moves = getPreliminaryMoves(model);
		if(moves.isEmpty())
			return moves;
		else{
			ChessModel temp = model.copy();
			Set<Location> legalMoves = new TreeSet<Location>();
			for(Location move : moves){
				//move the piece and check if its illegal
				if(temp.movePiece(loc, move) == -1){
					continue;
				}
				if(!temp.isPlayerInCheck(side)){
					legalMoves.add(move);
				}
				
				//move the piece back
				temp.takeback();
			}
			return legalMoves;
		}
	}
	
	/**
	 * The piece can generate a set of preliminary legal moves. However, not all 
	 * preliminary moves are necessarily legal (pinned pieces, etc).
	 * @param model The chess model so the piece can caluclate preliminary positions
	 * @return The set of preliminary locations
	 */
	public abstract Set<Location> getPreliminaryMoves(ChessModel model);
	
	
	/**
	 * When its the opponent's turn, the player should be able to click on a piece and then
	 * have the option of moving to anywhere on the board. Since this premove option
	 * does not take into consideration of the location of other pieces, it does not
	 * need a model.
	 * @return The set of locations that a piece can move to when its in premove mode
	 */
	public abstract Set<Location> getPremoveLocations();
	
	
	/**
	 * Copies the piece
	 * @return A deep copy of the piece
	 */
	public abstract Piece copy();
	
	
	/**
	 * Use this method to move a piece to a new location. Subclasses may override this
	 * method
	 * @param loc The location to move the piece to
	 */
	public void move(Location loc){
		this.loc = loc;
	}

	
	/**
	 * 
	 * @return The location of the current chess piece
	 */
	public Location getLocation(){
		return loc;
	}
	
	
	/**
	 * 
	 * @return The polarity of the piece (whether its white or black)
	 */
	public Polarity getPolarity(){
		return side;
	}
	
	/**
	 * 
	 * @return The width of a chess piece in pixels
	 */
	public static int getPieceImageWidth(){
		//choose a random image to get the width of
		//all images should have same width
		BufferedImage image = Art.findImage("queen_white.png");
		return image.getWidth();
	}
	
	/**
	 * 
	 * @return The height of a chess piece in pixels
	 */
	public static int getPieceImageHeight(){
		//choose a random image to get the height of
		//all images should have same height
		BufferedImage image = Art.findImage("queen_white.png");
		return image.getWidth();
	}
	
	@Override
	public int compareTo(Piece p){
		return loc.compareTo(p.getLocation());
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null)
			return false;
		if(o instanceof Piece){
			Piece p = (Piece) o;
			if(this.getClass().equals(p.getClass()) && p.compareTo(this) == 0)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return this.getClass() + " Location: " + loc + " Polarity " + side;
	}
	
}
