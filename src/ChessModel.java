import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * This class encapsulates the state of the board. In other words, it shows where each
 * piece lives on the board and who's turn it is to play. 
 * @author sfleischer
 *
 */
public class ChessModel {

	//constants
	public static final int QUEEN = 0;
	public static final int ROOK = 1;
	public static final int BISHOP = 2;
	public static final int KNIGHT = 3;
	
	//game state
	private Piece[][] board; //encapsulates the state of the board
	private Player white; //the player playing white
	private Player black; //the player playing black
	private Player player; //the current player who is moving
	private List<String> whiteNotation; //notates white's side
	private List<String> blackNotation; //notates black's side
	private King whiteKing; //useful for quickening check calculations
	private King blackKing; 
	private Piece passantable; //the pawn that can be captured by en passant
	//private Piece back; //a copy of the piece in case the user wants a takeback or
						//when the getLegalMoves method is calculating
	//private Piece current; //the most recent piece moved. Used for takebacks
	private Pawn promoting; //the pawn that's currently promoting
	List<MoveListener> moveListeners;
	Stack<Pair> moves; //keeps track of all the moves made in the game
	
	int white_points = 74;
	int black_points = 74;
	
	/**
	 * Creates a fresh chess board with all the pieces in their initial places
	 * @param white The white player 
	 * @param black The black player
	 */
	public ChessModel(Player white, Player black){
		board = new Piece[8][8];
		whiteNotation = new LinkedList<String>();
		blackNotation = new LinkedList<String>();
		moveListeners = new LinkedList<MoveListener>();
		moves = new Stack<Pair>();
		if(white.side == Polarity.White){
			this.white = white;
			this.black = black;
		} else {
			this.white = black;
			this.black = white;
		}
		player = this.white; //white goes first
		
		//instantiate pawns
		for(int i = 0; i < board.length; i++){
			board[1][i] = new Pawn(new Location(1, i), Polarity.White);
			board[6][i] = new Pawn(new Location(6, i), Polarity.Black);
		}
		
		board[0][0] = new Rook(new Location(0, 0), Polarity.White);
		board[0][1] = new Knight(new Location(0, 1), Polarity.White);
		board[0][2] = new Bishop(new Location(0, 2), Polarity.White);
		board[0][3] = new Queen(new Location(0, 3), Polarity.White);
		board[0][4] = new King(new Location(0, 4), Polarity.White);
		board[0][5] = new Bishop(new Location(0, 5), Polarity.White);
		board[0][6] = new Knight(new Location(0, 6), Polarity.White);
		board[0][7] = new Rook(new Location(0, 7), Polarity.White);
		
		board[7][0] = new Rook(new Location(7, 0), Polarity.Black);
		board[7][1] = new Knight(new Location(7, 1), Polarity.Black);
		board[7][2] = new Bishop(new Location(7, 2), Polarity.Black);
		board[7][3] = new Queen(new Location(7, 3), Polarity.Black);
		board[7][4] = new King(new Location(7, 4), Polarity.Black);
		board[7][5] = new Bishop(new Location(7, 5), Polarity.Black);
		board[7][6] = new Knight(new Location(7, 6), Polarity.Black);
		board[7][7] = new Rook(new Location(7, 7), Polarity.Black);
		
		whiteKing = (King) board[0][4];
		blackKing = (King) board[7][4];
	}
	
	/**
	 * This is the alternate constructor that takes in an array of chess pieces. 
	 * This is kind of like a copy constructor. It is not a full constructor 
	 * because it does not initialize <code> player </code>,
	 * <code> white </code>, or <code> black </code> global variables
	 * It creates its own promoter. It does not initialize 
	 * @param model A 2D array of chess pieces to create a new ChessModel object with
	 */
	public ChessModel(Piece[][] model, Stack<Pair> moves){
		board = model;
		this.moves = moves;
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				Piece p = board[i][j];
				if(p != null && p instanceof King){
					if(p.getPolarity() == Polarity.White){
						whiteKing = (King) p;
						white_points += p.worth;
					} else
						blackKing = (King) p;
						white_points += p.worth;
				}
				
				if(p!= null){
					if(p.getPolarity() == Polarity.White){
						white_points += p.worth;
					} else{
						black_points += p.worth;
					}
			
				}
			}
		}
	}
	
	/**
	 * Copies the current chess board so that a child object is free to manipulate 
	 * the pieces
	 * @return A deep copy of the current ChessModel
	 */
	public ChessModel copy(){
		int row = board.length;
		int col = board[0].length;
		
		Piece[][] model = new Piece[row][col];
		for(int i = 0; i < row; i++){
			for(int j = 0; j < col; j++){
				if(board[i][j] == null)
					continue;
				model[i][j] = board[i][j].copy();
			}
		}
		
		LinkedList<Pair> list = new LinkedList<Pair>();
		Stack<Pair> stack = new Stack<Pair>();
		while(!stack.isEmpty()){
			Pair p = moves.pop();
			Piece to = p.to.copy();
			Piece from = p.from.copy();
			list.add(new Pair(to, from));
		}
		
		while(!list.isEmpty()){
			stack.add(list.remove(0));
		}
		return new ChessModel(model, stack);
	}
	
	/*********************************************************************************
	 ------------------------------- ACCESSOR METHODS --------------------------------
	 ********************************************************************************/
	
	/**
	 * 
	 * @param loc The location to check if a piece exists there
	 * @return True when there is a piece at the specified location and false
	 * otherwise
	 */
	public boolean doesPieceExistAt(Location loc){
		if(Location.isLocationOutOfBounds(loc))
			return false;
		return board[loc.getRow()][loc.getColumn()] != null;
	}
	
	/**
	 * Returns the location of the piece at the given location. The Piece could be
	 * null which represents an empty space on the board.
	 * @param loc The location to get the piece from
	 * @return The piece at that location
	 */
	public Piece getPiece(Location loc){
		if(Location.isLocationOutOfBounds(loc))
			return null;
		return board[loc.getRow()][loc.getColumn()];
	}
	
	/**
	 * Set the piece at a specific location on the board. Used for move legality
	 * calculations and pawn promotions. If a piece already exists at this location
	 * then the piece will be written over. It's also acceptable if p is null
	 * @param p The piece to place on the board
	 * @param loc The location to place the piece. The method assumes that
	 * piece.getLocation() is structurally equal to loc.
	 */
	public void setPiece(Piece p, Location loc){
		board[loc.row][loc.col] = p;
	}
	
	/**
	 * 
	 * @param row The row of the board to get the piece from
	 * @param col The column of the board to get the piece from
	 * @return The piece at that position on the board
	 */
	public Piece getPiece(int row, int col){
		return board[row][col];
	}
	
	/**
	 * 
	 * @return The location of the current piece
	 */
	public Location getCurrentLocation(){
		if(moves.isEmpty())
			return null;
		return moves.peek().to.getLocation();
	}
	/**
	 * 
	 * @return The location of the takeback piece
	 */
	public Location getTakebackLocation(){
		if(moves.isEmpty())
			return null;
		return moves.peek().from.getLocation();
	}
	

	public void addMoveListener(MoveListener m){
		moveListeners.add(m);
	}
	
	/**
	 * Can add a default player to the model. Useful for copy constructor
	 * @param p
	 */
	public void setPlayer(Player p){
		player = p;
	}
	
	public boolean canMove(){
		return player.canMove();
	}
	
	public int getWhitePoints(){
		return white_points;
	}
	
	public int getBlackPoints(){
		return black_points;
	}
	
	
	/*********************************************************************************
	 ----------------------------------- MOVEMENT ------------------------------------
	 ********************************************************************************/
	
	/**
	 * Moves the piece from the start location to the end location. It also logs
	 * the movement on the appropriate notation list.
	 * @param start The starting location of the piece
	 * @param end The ending location of the piece
	 * @return True if the move was successful. False if the move was unsuccessful.
	 * Some examples of unsuccessful moves include out-of-bounds locations or null
	 * pieces.
	 */
	public int movePiece(Location start, Location end){
		//check for any invalid arguments
		if(Location.isLocationOutOfBounds(start) || Location.isLocationOutOfBounds(end))
			return -1;
		
		Piece p = board[start.row][start.col];
		if(p == null)
			return -1;
		
		//move the piece
		Piece capture = board[end.row][end.col];
		board[start.row][start.col] = null;
		board[end.row][end.col] = p;
				
		//save a backup of the piece
		Piece back = p.copy();
		
		//tell the piece it moved
		p.move(end);
		//current = p;
		
		//log that move
		if(capture == null)
			moves.add(new Pair(back, p.copy()));
		else
			moves.add(new Pair(back, capture));
		
		//check for special moves
		if(p instanceof Pawn){
			Pawn pawn = (Pawn) p;
			checkEnPassantCapture(pawn);
			if(pawn.isFresh()) {
				passantable = pawn;
			} else {
				passantable = null; 
			}
			if(pawn.isPromotable() && player != null){
				promoting = pawn; //set this variable
				player.promote(); //notify handler that pawn is promoting
			}
		} else if(p instanceof King){
			castle((King) p);
			passantable = null;
		} else {
			//if there are any passantable pawns there aren't any now
			passantable = null;
		} 
		
		//switch players
		if(white != null){
			player = (player == white ? black : white);
			if(!this.isPlayerCheckmated(player.side))
				player.move();
		}
		
		if(moveListeners != null)
			dispatchMove(); //update the timers
		
		if(capture != null){
			if(capture.getPolarity() == Polarity.White){
				white_points -= capture.worth;
			} else {
				black_points -= capture.worth;
			}
			return capture.worth;
		}
		return 0;
	}
	
	public void castle(King king){
		if(king.didQueenSideCastle()){
			Location loc = king.getLocation();
			Piece rook = getPiece(loc.row, loc.col - 2);
			if(rook == null)
				return;
			Location oldRook = rook.getLocation();
			Location newRook = new Location(loc.row, loc.col + 1);
			rook.move(newRook);
			board[oldRook.row][oldRook.col] = null;
			board[newRook.row][newRook.col] = rook;
			
		} else if(king.didKingSideCastle()){
			Location loc = king.getLocation();
			Piece rook = getPiece(loc.row, loc.col + 1);
			if(rook == null)
				return;
			Location oldRook = rook.getLocation();
			Location newRook = new Location(loc.row, loc.col - 1);
			rook.move(newRook);
			board[oldRook.row][oldRook.col] = null;
			board[newRook.row][newRook.col] = rook;
		}
	}
	
	/**
	 * Given a moved pawn p, check to see if it made a valid en passant move.
	 * Kill the pawn that died.
	 * @param p
	 */
	public void checkEnPassantCapture(Pawn p){
		Location loc = p.getLocation();
		Polarity side = p.getPolarity();
		
		//kill the pawn if it is an en passant
		//note that the direction is backwards
		int direction;
		if(side == Polarity.White){
			direction = Location.SOUTH;
		}
		else{
			direction = Location.NORTH;
		}
		
		Piece n = getPiece(loc.getAdjacentLocation(direction));
		//we want to make sure n is reference equal with passantable
		if(n != null && n == passantable){
			killPiece(loc.getAdjacentLocation(direction));
		}
	}
	
	/**
	 * Takes the move back. It does nothing if there are no takebacks
	 */
	public void takeback(){
		// This keyboard is way better than my keyboard
		if(moves == null || moves.isEmpty()){
			return;
		}
		Pair p = moves.pop();
		Piece to = p.getTo();
		Piece from = p.getFrom();
		killPiece(to.getLocation());
		if(to.getPolarity() != from.getPolarity()){
			setPiece(to, to.getLocation()); //add the killed piece
			if(to.getPolarity() == Polarity.White)
				white_points += to.worth;
			else
				black_points += to.worth;
		}
		setPiece(from, from.getLocation());
		//current = back;
		//update king pointers
		if(from instanceof King && from.getPolarity() == Polarity.White ){
			whiteKing = (King) from;
			if(to instanceof King ){
				Location k = to.getLocation();
				int dist = from.getLocation().col - to.getLocation().col;
				if(dist == 2){
					Location n = new Location(k.row, k.col + 1);
					killPiece(new Location(k.row, k.col - 1));
					setPiece(new Rook(n, to.getPolarity()), n);
				} else if(dist == -2){
					Location n = new Location(k.row, k.col - 2);
					killPiece(new Location(k.row, k.col + 1));
					setPiece(new Rook(n, to.getPolarity()), n);
				}
			}
		} else if(from instanceof King){
			blackKing = (King) from;
		}
		
		//switch players
		if(white != null){
			player = (player == white ? black : white);
			player.move();
		}
		
		if(moveListeners != null)
			dispatchMove(); //update the timers
		
		//back = null;
	}
	
	/**
	 * Use this method when there is a promoting pawn. A user will have to enter
	 * the piece they wish to promote to and the AI will just promote to a queen
	 * every time.
	 * @param n The ID number of the piece the user wishes to promote to
	 */
	public void promotePawn(int n){
		//there must be a promoting pawn
		if(promoting == null)
			return;
		Location loc = promoting.getLocation();
		Polarity side = promoting.getPolarity();
		switch(n){
		case QUEEN:
			setPiece(new Queen(loc, side), loc);
			break;
		case ROOK:
			setPiece(new Rook(loc, side), loc);
			break;
		case BISHOP:
			setPiece(new Bishop(loc, side), loc);
			break;
		case KNIGHT:
			setPiece(new Knight(loc, side), loc);
			break;
		default:
			setPiece(new Queen(loc, side), loc);
			break;
		}
		
		promoting = null; //there is no promoting pawn. feed to garbage
	}

	
	
	/*********************************************************************************
	 ------------------------------- LOCATION METHODS --------------------------------
	 ********************************************************************************/
	
	/**
	 * This method is used to see if a space is threatened (ie pinned pieces or castling)
	 * @param loc The location to see see if its threatened
	 * @param side The polarity of the player
	 * @return True if it is threatened, and false otherwise
	 */
	public boolean isLocationThreatened(Location loc, Polarity side){
		for(Piece p : getPlayersPieces(Polarity.opposite(side))){
			Set<Location> moves = p.getPreliminaryMoves(this);
			if(moves.contains(loc))
				return true;
			
		}
		return false;
	}
	
	/**
	 * Recursively finds the enemy along the given direction
	 * @param dir The direction
	 * @param enemy The side of the enemy
	 * @return True if the enemy has been found
	 */
	public Piece findEnemy(Location loc, int dir, Polarity enemy){
		if(Location.isLocationOutOfBounds(loc))
			return null;
		Piece p = getPiece(loc);
		if(p == null)
			return findEnemy(loc.getAdjacentLocation(dir), dir, enemy);
		if(p.getPolarity() == enemy)
			return p;
		return null;
		
	}
	
	/**
	 * This method is used to check against pinned pieces and checks
	 * @param side The polarity of the player
	 * @return The location of the King piece
	 */
	public Location findLocationOfPlayerKing(Polarity side){
		King king;
		if(side == Polarity.White){
			king = whiteKing;
		} else {
			king = blackKing;
		}
		return king.getLocation();
	}
	
	/**
	 * This method is used to check for pawns that can be captured by en passant
	 * @return The location of the passant pawn. Can either be null or defined.
	 */
	public Location getPassantLocation(){
		if(passantable == null)
			return null;
		return passantable.getLocation();
	}
	
	/*********************************************************************************
	 ------------------------------- CHECKING METHODS --------------------------------
	 ********************************************************************************/
	
	/**
	 * 
	 * @param side The side to check the checking state of
	 * @return True if the player is in check and false otherwise
	 */
	public boolean isPlayerInCheck(Polarity side){
		//System.out.println(this);
		return isLocationThreatened(findLocationOfPlayerKing(side), side);
	}
	
	/**
	 * 
	 * @param side The side to check the checkmate state of
	 * @return True if the player is in checkmate and false otherwise
	 */
	public boolean isPlayerCheckmated(Polarity side){
		if(!isPlayerInCheck(side)){
			return false;
		} else{
			Set<Piece> set = getPlayersPieces(side);
			for(Piece p : set){
				if(!p.getLegalMoves(this).isEmpty())
					return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param side The side to check the stalemate state of
	 * @return True if the player is in stalemate and false otherwise
	 */
	public boolean isPlayerStalemated(Polarity side){
		if(isPlayerInCheck(side)){
			return false;
		} else {
			Set<Piece> set = getPlayersPieces(side);
			for(Piece p : set){
				if(!p.getLegalMoves(this).isEmpty())
					return false;
			}
		}
		return true;
	}
	
	/*********************************************************************************
	 -------------------------------- HELPER METHODS ---------------------------------
	 ********************************************************************************/
	
	/**
	 * This method is useful for finding checks, checkmates, and the legality of
	 * special moves.
	 * @param side The side of the player
	 * @return A Set of Pieces that belong to the player
	 */
	public Set<Piece> getPlayersPieces(Polarity side){
		Set<Piece> set = new TreeSet<Piece>();
		for(int row = 0; row < board.length; row++){
			for(int col = 0; col < board[0].length; col++){
				Piece p = board[row][col];
				if(p == null || p.getPolarity() != side)
					continue;
				else
					set.add(p);
			}
		}
		return set;
	}
	
	public Polarity getCurrentSide(){
		return (player == white) ? Polarity.White : Polarity.Black;
	}
	
	public void dispatchMove(){
		for(MoveListener ml : moveListeners){
			ml.moved();
		}
	}
	
	public void dispatchPause(){
		for(MoveListener ml : moveListeners){
			ml.pause();
		}
	}
	
	@Override
	public String toString(){
		String state = "";
		for(int row = 0; row < 8; row++){
			for(int col = 0; col < 8; col++){
				state = state + findLetter(board[row][col]) + "|";
			}
			state = state + "\n";
		}
		return state;
	}
	
	public void forceMove(){
		
	}
	
	/**
	 * Kills the piece at the given location
	 * @param loc
	 */
	public int killPiece(Location loc){
		Piece p = board[loc.row][loc.col];
		if(p == null)
			return 0;
		board[loc.row][loc.col] = null;
		return p.worth;
	}
	
	
	/**
	 * This method is used in the toString method for the model and for notating the 
	 * game
	 * @param p The piece to find the letter of
	 * @return The letter of the piece
	 */
	private String findLetter(Piece p){
		if(p instanceof Pawn)
			return "P"; //pawn does not have a letter
		else if(p instanceof Knight)
			return "N";
		else if(p instanceof Bishop)
			return "B";
		else if(p instanceof Rook)
			return "R";
		else if(p instanceof Queen)
			return "Q";
		else if(p instanceof King)
			return "K";
		else
			return " ";
	}
	
}
