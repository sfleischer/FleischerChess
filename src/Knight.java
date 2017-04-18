import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Knight extends Piece{

	public static final int WORTH = 3;
	
	public Knight(Location loc, Polarity p){
		super(loc, WORTH, p);
		
		String path = "";
		
		switch(p){
		case White: 
			path = "knight_white.png";
			break;
		case Black:
			path = "knight_black.png";
			break;
		}
		
		image = Art.findImage(path);
	}
	
	private boolean isLocationValid(ChessModel model, Location loc){
		Piece p = model.getPiece(loc);
		if(Location.isLocationOutOfBounds(loc))
			return false;
		if(p == null)
			return true;
		else if(p.getPolarity() != side)
			return true;
		return false;
	}


	@Override
	public Set<Location> getPreliminaryMoves(ChessModel model) {
		Set<Location> set = new TreeSet<Location>();
		int direction;
		//kind of inefficient
		for(int i = 0; i < 4; i++){
			direction = i * 90;
			Location next1 = loc.getAdjacentLocation(direction);
			Location next2 = next1.getAdjacentLocation(direction);
			Location end1 = next2.getAdjacentLocation(direction + 90);
			Location end2 = next2.getAdjacentLocation(direction - 90);
			if(isLocationValid(model, end1))
				set.add(end1);
			if(isLocationValid(model, end2))
				set.add(end2);
		}
		return set;
	}
	
	@Override
	public Set<Location> getPremoveLocations() {
		Set<Location> set = new TreeSet<Location>();
		int direction;
		//kind of inefficient
		for(int i = 0; i < 4; i++){
			direction = i * 90;
			Location next1 = loc.getAdjacentLocation(direction);
			Location next2 = next1.getAdjacentLocation(direction);
			Location end1 = next2.getAdjacentLocation(direction + 90);
			Location end2 = next2.getAdjacentLocation(direction - 90);
			if(!Location.isLocationOutOfBounds(end1))
				set.add(end1);
			if(!Location.isLocationOutOfBounds(end2))
				set.add(end2);
		}
		return set;
	}
	
	@Override
	public Piece copy() {
		return new Knight(loc, side);
	}
}
