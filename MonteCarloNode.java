import java.util.ArrayList;


public class MonteCarloNode {
	private int numChildren;
	private MoveHolder myMove;
	private int startLocation;
	public int payoff;
	public int samples;
	//private ArrayList<Integer> myLocations;
	//first child location
	//total payoff
	//total number of samples
	
	public MonteCarloNode(int numChild, MoveHolder aMove, int location){
		numChildren = numChild;
		myMove = aMove;
		startLocation = location;
		payoff = 0;
		samples = 0;
	}
	
	public MoveHolder getMove(){
		return myMove;
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
	
	public int getValue(){
		return payoff / samples;
	}
	
	public int totalVal(){
		return payoff;
	}
	
	public int numSamples(){
		return samples;
	}
	
	public void addPayoff(int pay, int samp){
		payoff += pay;
		samples += samp;
	}
		
}
