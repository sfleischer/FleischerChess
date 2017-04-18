import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Pawn extends Piece{

	public static final int WORTH = 1;
	int moveCount = 0; //the number of times the pawn moved (important for first move)
	boolean freshMove = false; //only true if the pawn moved two forward. For en passant.
	boolean promoted = false; //only true when the pawn reaches the last rank
	
	public Pawn(Location loc, Polarity p){
		super(loc, WORTH, p);
		
		String path = "";
		
		switch(p){
		case White: 
			path = "pawn_white.png";
			break;
		case Black:
			path = "pawn_black.png";
			break;
		}
		
		image = Art.findImage(path);
	}

	@Override
	public Set<Location> getPreliminaryMoves(ChessModel model) {
		Set<Location> set = new TreeSet<Location>();
		int direction; //the direction of the pawn moving forward
		//freshMove = false;
		
		if(freshMove){
			//System.out.println("pawn out at " + loc + " " + side);
			freshMove = false;
		}
		
		if(side == Polarity.White){
			direction = Location.NORTH;
		} else {
			direction = Location.SOUTH;
		}
		
		//pawn can move one forward if there is no piece in front of it
		Location next = loc.getAdjacentLocation(direction);
		Piece p = model.getPiece(next);
		if(p == null)
			set.add(next);
		
		for(int i = -1; i < 2; i+=2){
			//check for diagonal kill
			Location diagonal = loc.getAdjacentLocation(direction + 45*i);
			p = model.getPiece(diagonal);
			if(p != null && p.getPolarity() != side) //short circuit
				set.add(diagonal);
			
			//check for en passant
			Location passant = loc.getAdjacentLocation(direction + 90*i);
			p = model.getPiece(passant);
			if(p != null && p.getPolarity() != side && p instanceof Pawn){
				Pawn pawn = (Pawn) p;
				if(pawn.getLocation().equals(model.getPassantLocation()))
					set.add(diagonal); //add the diagonal, not passant
			}
		}

		//pawn can move two spaces forward on first move
		if(moveCount == 0 && !model.doesPieceExistAt(next)){
			Location two = next.getAdjacentLocation(direction);
			Piece n = model.getPiece(two);
			if(n == null)
				set.add(two);
		}
		return set;
	}
	
	@Override
	public Set<Location> getPremoveLocations() {
		Set<Location> set = new TreeSet<Location>();
		int direction; //the direction of the pawn moving forward
		
		if(side == Polarity.White){
			direction = Location.NORTH;
		} else {
			direction = Location.SOUTH;
		}
		
		//pawn can move one forward
		Location next = loc.getAdjacentLocation(direction);
		set.add(next);
		
		for(int i = -1; i < 2; i+=2){
			//check for diagonal kill
			Location diagonal = loc.getAdjacentLocation(direction + 45*i);
			set.add(diagonal);
		}

		//pawn can move two spaces forward on first move
		if(moveCount == 0){
			Location two = next.getAdjacentLocation(direction);
			set.add(two);
		}
		return set;
	}
	
	@Override
	public void move(Location end){
		if(Math.abs(loc.rank - end.rank) == 2){
			freshMove = true; //pawn can be captured by en passant
			moveCount++;
		} else if(moveCount == 5){
			promoted = true; //pawn reached the last rank!
			
		}
		loc = end;
		moveCount++;
	}
	
	/**
	 * Only use this for copy constructor
	 * @param m
	 */
	public void setMoveCount(int m){
		moveCount = m;
	}
	
	public boolean isFresh(){
		return freshMove;
	}
	
	public boolean isPromotable(){
		return promoted;
	}
	
	@Override
	public Piece copy() {
		Pawn p = new Pawn(loc, side);
		p.setMoveCount(moveCount);
		return p;
	}
	
}
