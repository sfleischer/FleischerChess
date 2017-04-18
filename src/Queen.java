import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Queen extends Piece{

	public static final int WORTH = 9;
	
	public Queen(Location loc, Polarity p){
		super(loc, WORTH, p);
		
		String path = "";
		
		switch(p){
		case White: 
			path = "queen_white.png";
			break;
		case Black:
			path = "queen_black.png";
			break;
		}
		
		image = Art.findImage(path);
	}

	@Override
	public Set<Location> getPreliminaryMoves(ChessModel model) {
		Set<Location> set = new TreeSet<Location>();
		int direction = 0;
		for(int i = 0; i < 8; i++){
			direction = Location.NORTHEAST + i * 45;
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
		for(int i = 0; i < 8; i++){
			direction = Location.NORTHEAST + i * 45;
			Location next = loc.getAdjacentLocation(direction);
			while(!Location.isLocationOutOfBounds(next)){
				set.add(next);
				next = next.getAdjacentLocation(direction);
			}
		}
		return set;
	}
	
	@Override
	public Piece copy() {
		return new Queen(loc, side);
	}
}
