import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Rook extends Piece{

	public static final int WORTH = 5;
	boolean freshMove = true; //true when the rook never moves and 
	  					      //false otherwise. Variable used for castling
	
	public Rook(Location loc, Polarity p){
		super(loc, WORTH, p);
		
		String path = "";
		
		switch(p){
		case White: 
			path = "rook_white.png";
			break;
		case Black:
			path = "rook_black.png";
			break;
		}
		
		image = Art.findImage(path);
	}

	@Override
	public Set<Location> getPreliminaryMoves(ChessModel model) {
		Set<Location> set = new TreeSet<Location>();
		int direction = 0;
		for(int i = 0; i < 4; i++){
			direction = i * 90;
			Location next = loc.getAdjacentLocation(direction);
			Piece p = model.getPiece(next);
			while(p == null && !Location.isLocationOutOfBounds(next)){
				set.add(next);
				next = next.getAdjacentLocation(direction);
				p = model.getPiece(next);
			}
			if(p == null)
				continue;
			else if(p.getPolarity() != side)
				set.add(next);
		}
		return set;
	}
	
	@Override
	public Set<Location> getPremoveLocations() {
		Set<Location> set = new TreeSet<Location>();
		int direction = 0;
		for(int i = 0; i < 4; i++){
			direction = i * 90;
			Location next = loc.getAdjacentLocation(direction);
			while(!Location.isLocationOutOfBounds(next)){
				set.add(next);
				next = next.getAdjacentLocation(direction);
			}
		}
		return set;
	}
	
	@Override
	public void move(Location loc){
		super.move(loc);
		freshMove = false;
	}
	
	public boolean isFresh(){
		return freshMove;
	}
	
	@Override
	public Piece copy() {
		return new Rook(loc, side);
	}
}
