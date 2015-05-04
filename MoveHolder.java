import java.util.ArrayList;


public class MoveHolder {
	private Move myMove;
	private double myScore;
	private int times;
	private ArrayList<Double> scores;
	
	public MoveHolder(Move aMove, double compScore, double theScore){
		myMove = aMove;
		myScore = compScore;
		times = 1;
		scores = new ArrayList<Double>();
		scores.add(theScore);
	}
	
	public double getScore(){
		return myScore;
	}
	
	public Move getMove(){
		return myMove;
	}
	
	public void computeScore(){
		int temp = 0;
		for (double s : scores) {
			temp += s;
		}
		myScore = (temp / scores.size() + Math.sqrt((2 * Math.log(Agent.totalSamples) / scores.size())));
	}
	
	public void addScore(double score){
		times++;
		scores.add(score);
	}
	
	public int times(){
		return times;
	}

}
