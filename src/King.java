import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class King extends Piece{

	public static final int WORTH = 35;
	private boolean freshMove = true; //true when the king never moves and 
							  //false otherwise. Variable used for castling
	private boolean kingSideCastle = false;
	private boolean queenSideCastle = false;
	
	public King(Location loc, Polarity p){
		super(loc, WORTH, p);
		
		String path = "";
		
		switch(p){
		case White: 
			path = "king_white.png";
			break;
		case Black:
			path = "king_black.png";
			break;
		}
		
		image = Art.findImage(path);
	}
	
	@Override
	public Set<Location> getLegalMoves(ChessModel model) {
		Set<Location> set = super.getLegalMoves(model);
		//must check if king can castle
				if(checkKingsideCastling(model)){
					set.add(new Location(loc.row, loc.col+2));
				} 
				if(checkQueensideCastling(model)){
					set.add(new Location(loc.row, loc.col-2));
				}
				
				return set;
	}

	@Override
	public Set<Location> getPreliminaryMoves(ChessModel model) {
		Set<Location> set = new TreeSet<Location>();
		int direction = 0;
		//find king's immediate location
		for(int i = 0; i < 8; i++){
			direction = Location.NORTHEAST + i * 45;
			Location next = loc.getAdjacentLocation(direction);
			Piece p = model.getPiece(next);
			if(Location.isLocationOutOfBounds(next))
				continue;
			if(p == null)
				set.add(next);
			else if(p.getPolarity() != side)
				set.add(next);
		}
		return set;
	}
	
	@Override
	public Set<Location> getPremoveLocations(){
		Set<Location> set = new TreeSet<Location>();
		int direction = 0;
		//find king's immediate location
		for(int i = 0; i < 8; i++){
			direction = Location.NORTHEAST + i * 45;
			Location next = loc.getAdjacentLocation(direction);
			if(Location.isLocationOutOfBounds(next))
				continue;
			set.add(next);
		}
		
		//add castling option
		if(freshMove){
			set.add(new Location(loc.row, loc.col-2));
			set.add(new Location(loc.row, loc.col+2));
		}
		return set;
	}
	
	/**
	 * Checks whether castling is possible for the given side
	 * Precondition: The king is not in check
	 * @param side The side of the player (white or black)
	 * @return True if kingside castling is valid. False otherwise
	 */
	public boolean checkKingsideCastling(ChessModel model){
		//get the appropriate king first
		if(!freshMove){
			return false;
		}
		//collect the appropriate locations
		Location f1 = loc.getAdjacentLocation(Location.EAST);
		Location g1 = f1.getAdjacentLocation(Location.EAST);
		Location h1 = g1.getAdjacentLocation(Location.EAST);
		Piece fp = model.getPiece(f1);
		Piece gp = model.getPiece(g1);
		Piece hp = model.getPiece(h1);
		
		Rook rook;
		//make sure the spaces between the king and rook are blank
		if(fp == null && gp == null && hp != null && hp instanceof Rook){
			rook = (Rook) hp;
		} else {
			return false;
		}
		
		//if the rook already moved then no castling
		if(!rook.isFresh()){
			return false;
		}
		
		//if the empty spaces are threatened then no castling
		if(model.isLocationThreatened(loc, side) ||
				model.isLocationThreatened(f1, side) ||
				model.isLocationThreatened(g1, side))
			return false;
		
		return true;
	}
	
	/**
	 * Checks whether castling is possible for the given side
	 * @param side The side of the player (white or black)
	 * @return True if kingside castling is valid. False otherwise
	 */
	public boolean checkQueensideCastling(ChessModel model){
		//get the appropriate king first
		if(!freshMove){
			return false;
		}
		//collect the appropriate locations
		Location d1 = loc.getAdjacentLocation(Location.WEST);
		Location c1 = d1.getAdjacentLocation(Location.WEST);
		Location b1 = c1.getAdjacentLocation(Location.WEST);
		Location a1 = b1.getAdjacentLocation(Location.WEST);
		Piece dp = model.getPiece(d1);
		Piece cp = model.getPiece(c1);
		Piece bp = model.getPiece(b1);
		Piece ap = model.getPiece(a1);
		
		Rook rook;
		//make sure the spaces between the king and rook are blank
		if(dp == null && cp == null && bp == null && ap instanceof Rook){
			rook = (Rook) ap;
		} else {
			return false;
		}
		
		//if the rook already moved then no castling
		if(!rook.isFresh()){
			return false;
		}
		
		//if the empty spaces are threatened then no castling
		if(model.isLocationThreatened(loc, side) ||
				model.isLocationThreatened(d1, side) ||
				model.isLocationThreatened(c1, side))
			return false;
		
		return true;
	}
	
	@Override
	public void move(Location loc){
		if(super.loc.file - loc.file == 2) {
			queenSideCastle = true;
		} else if(super.loc.file - loc.file == -2) {
			kingSideCastle = true;
		} else {
			queenSideCastle = false;
			kingSideCastle = false;
		}
		super.move(loc);
		freshMove = false;
	}
	
	@Override
	public Piece copy() {
		King k = new King(loc, side);
		k.setFresh(freshMove);
		k.setKingSideCastle(kingSideCastle);
		k.setQueenSideCastle(queenSideCastle);
		return k;
	}
	
	public boolean isFresh(){
		return freshMove;
	}
	
	public boolean didKingSideCastle(){
		return kingSideCastle;
	}
	
	public boolean didQueenSideCastle(){
		return queenSideCastle;
	}
	
	public void setKingSideCastle(boolean b){
		kingSideCastle = b;
	}
	
	public void setQueenSideCastle(boolean b){
		queenSideCastle = b;
	}
	
	public void setFresh(boolean b){
		freshMove = b;
	}
}
