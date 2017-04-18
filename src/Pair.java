
public class Pair {
	public final Piece from;
	public final Piece to;
	
	/**
	 * Piece moves from location a to b
	 * @param a The location the piece is moving out of
	 * @param b The location the piece is moving into
	 */
	public Pair(Piece a, Piece b){
		from = a;
		to = b;
	}
	
	public Piece getFrom(){
		return from;
	}
	
	public Piece getTo(){
		return to;
	}
		

}
