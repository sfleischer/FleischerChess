import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AI extends Player{

	ConcurrentLinkedQueue<Node> qn;
	Board board; //need the board for repainting
	volatile int threadCount = 0;
	int totalThreads = 10;
	
	public AI(Polarity p) {
		super(p);
		qn = new ConcurrentLinkedQueue<Node>();
		canMove = false;
	}
	
	public void setBoard(Board b){
		board = b;
	}
	
	/**
	 * LAUNCH THE THREADS!!!
	 */
	@Override
	public void move() {
		
		AI ai = (AI) this;
		Runnable run = new Runnable(){
		
		
		@Override
		public void run() {
			Set<Piece> pieces = model.getPlayersPieces(side);
			Iterator<Piece> iter = pieces.iterator();
			threadCount = pieces.size();
			totalThreads = pieces.size();
			for(int i = 0; i < totalThreads; i++){
				int size = (int) (pieces.size() * 1.0 /totalThreads);
				int count = 0;
				Set<Piece> div = new TreeSet<Piece>();
				while(iter.hasNext() && count < size){
					div.add(iter.next().copy());
					count++;
				}
				
				Thread branch = new Thread(
						new Branch(div, model.copy(), side, qn, ai));
				branch.start();
			}
			
		}};
		Thread thread = new Thread(run);
		thread.run();
		System.out.println(totalThreads + " threads dispatched");
	}
	
	/**
	 * A thread will call this function when its finished
	 */
	public synchronized void finish(){
		threadCount--;
		if(threadCount == 0){
			Node best = qn.remove();
			double max = best.worth;
			while(!qn.isEmpty()){
				Node n = qn.remove();
				if(n.worth > max)
					best = n;
			}
			Pair p = best.getPair();
			model.movePiece(p.from.getLocation(), p.to.getLocation());
			System.out.println(p.from+ " " + p.to);
			qn.clear();
			board.repaint();	
		}
	}

	@Override
	public void promote() {
		model.promotePawn(ChessModel.QUEEN);
	}

}
