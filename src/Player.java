/**
 * The board is the main interface between the user and the program. The board will
 * ask the player class to handle certain situations (clicking and such).
 * @author sfleischer
 *
 */
public abstract class Player{
	protected ChessModel model; //cyclic def of model
	protected Polarity side; //the side of the player
	protected boolean canMove;
	
	public Player(Polarity p){
		side = p;
	}
	
	public boolean canMove(){
		return canMove;
	}
	
	public void setModel(ChessModel m){
		model = m;
	}
	
	/**
	 * The model can request the player to make a move
	 */
	public abstract void move();
	
	
	/**
	 * returns the integer code of the piece they want to promote
	 */
	public abstract void promote();

}
