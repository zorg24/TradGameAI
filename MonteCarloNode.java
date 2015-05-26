import java.util.ArrayList;


public class MonteCarloNode {
	private int numChildren;
	private int startLocation;
	public int payoff;
	public int samples;
	public MonteCarloNode parent = null;
	public Move theMove;


	public MonteCarloNode(int numChild, Move aMove, int location){
		numChildren = numChild;
		theMove = aMove;
		startLocation = location;
		payoff = 0;
		samples = 0;
	}


	public MonteCarloNode(int numChild, Move aMove, int location, int pay, MonteCarloNode par){
		numChildren = numChild;
		theMove = aMove;
		startLocation = location;
		payoff = 0;
		payoff += pay;
		samples = 1;
		parent = par;
	}
	
	public Move getMove2(){
		return theMove;
	}
	
	public MonteCarloNode getParent(){
		return parent;
	}
	
	public void setChildren(int child){
		numChildren = child;
	}
	
	public void startLocation(int location){
		startLocation = location;
	}
	
	public int getChildren(){
		return numChildren;
	}
	
	public int getStartLocation(){
		return startLocation;
	}
	
	public void addValue(int val){
		payoff += val;
		samples++;
	}
	
	public int getAvgValue(){
		return payoff / samples;
	}
	
	public double getValue(){
		return ((payoff / samples) + 100 * Math.sqrt((Math.log(Agent.totalSamples) / samples)));
	}
	
	public int totalVal(){
		return payoff;
	}
	
	public int numSamples(){
		return samples;
	}
		
}
