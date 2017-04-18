import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Node {
	public int worth; //the worth of the node
	private List<Node> children;
	private Node best; //the best node
	private Pair pair; //the pair that the node represents
	
	public Node(int worth, Pair p){
		this.worth = worth;
		pair = p;
		children = new LinkedList<Node>();
	}
	
	public void addNode(Node n){
		children.add(n);
	}
	
	/**
	 * computes the fitness and score of the current node
	 */
	public void evaluate(boolean maximize){
		
		//base case
		if(children.isEmpty()){
			return;
		}
		
		Node n = children.iterator().next();
		int max = n.worth;
		for(Node child : children){
			if(child == null)
				continue;
			child.evaluate(!maximize); 
			if(child.worth > max){
				n = child;
				max = child.worth;
			}
		}
		int diff = (int) (worth - max);

		best = n;
		worth = worth + diff;
	}
	
	public Pair getPair(){
		return pair;
	}
	
	public Node getBest(){
		return best;
	}
	
	@Override
	public String toString(){
		return "worth: " + worth;
	}
	
}
