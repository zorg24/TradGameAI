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
	
	public MonteCarloNode(int numChild, MoveHolder aMove, int location, int pay){
		numChildren = numChild;
		myMove = aMove;
		startLocation = location;
		payoff += pay;
		samples = 1;
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
	
	public int getAvgValue(){
		return payoff / samples;
	}
	
	public double getValue(){
		return (payoff / samples + Math.sqrt((2 * Math.log(Agent.totalSamples) / samples)));
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
