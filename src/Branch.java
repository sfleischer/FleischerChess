import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Branch implements Runnable{

	ChessModel model; //this branch has their own model copies
	ConcurrentLinkedQueue<Node> qn;
	Set<Piece> pieces;
	Polarity side;
	Location from;
	Location to;
	int fitness;
	int maxDepth = 3;
	AI ai;
	
	/**
	 * Precondition: models are fitted with an appropriate player
	 * @param pieces The initial pieces that the branch can move
	 */
	public Branch(Set<Piece> p, ChessModel m, 
			Polarity s, ConcurrentLinkedQueue<Node> qn, AI ai){
		pieces = p;
		model = m;
		side = s;
		this.qn = qn;
		this.ai = ai;
	}
	
	@Override
	public void run() {
		Node alphaNode = new Node(0, null); //the alpha node does not contain a pair
		try{
			if(model.getPlayersPieces(side).size() <= 9)
				maxDepth = 4;
			createTree(pieces, alphaNode, side, 0); //creates the tree;
		} catch (NullPointerException e){}
		Node best = evaluate(alphaNode);
		if(best != null){
			qn.add(best);
			System.out.println("worth " + best.worth);
		}
		ai.finish();
		//System.out.println("Thread finished");
	}

	
	/**
	 * RECURSION ALERT
	 * Base Case: When the depth reaches 4. When the base case is reached
	 * the method compares the score with the current fitness. 
	 * If the score is better than the current fitness, then it will change
	 * the <code> from </code> and <code> to </code> instance variables.
	 * By the time the recursion is complete this branch will have the best
	 * <code> from </code> and <code> to </code> locations to move the piece.
	 * @param pieces The pieces that the branch is allowed to move
	 * @param model the new model
	 * @param depth the depth of the recursion
	 */

	
	//recursively creates a tree of nodes
	public void createTree(Set<Piece> pieces, Node head, Polarity side, int depth){
		//base case
		if(depth > maxDepth)
			return;
		
		//System.out.println("depth: " + depth);
		for(Piece p : pieces){
			int max_worth = -1000;
			Set<Location> moves = p.getLegalMoves(model);
			int spaces = moves.size();
			for(Location loc : moves){
				Piece from = p.copy();
				int worth = model.movePiece(p.getLocation(), loc);
				if(worth == -1)
					continue;
				worth = 2*worth + spaces/2; 
				if(p.getPolarity() == Polarity.White){
					worth = worth + model.getWhitePoints();
				} else {
					worth = worth + model.getBlackPoints();
				}
				Piece to = model.getPiece(loc).copy();
				if(worth <= max_worth){
					model.takeback();
					return; //completely prune this branch
				}
				max_worth = worth;
				Node n = new Node(worth, new Pair(from, to));
				head.addNode(n);
				Polarity opposite = Polarity.opposite(side);
				if(depth <= maxDepth)
					createTree(model.getPlayersPieces(opposite), n, opposite, depth + 1);
				model.takeback();
			}
		}
	}
	
	
	
	public Node evaluate(Node head){
		head.evaluate(true); //creates a cascade down the tree
		Node n = head.getBest();
		return n;
	}

}
