import java.util.ArrayList;


public class MoveHolder {
	private Move myMove;
	private double myScore;
	private int times;
//	private ArrayList<Double> scores;
	private double totalScore = 0.0;
	
	public MoveHolder(Move aMove, double compScore, double theScore){
		myMove = aMove;
		myScore = compScore;
		times = 1;
//		scores = new ArrayList<Double>();
//		scores.add(theScore);
		totalScore += theScore;
	}
	
	public double getScore(){
		return myScore;
	}
	
	public Move getMove(){
		return myMove;
	}
	
	public void computeScore(){
		myScore = (totalScore / times + Math.sqrt((2 * Math.log(Agent.totalSamples) / times)));
	}
	
	public void addScore(double score){
		times++;
		totalScore += score;
	}
	
	public int times(){
		return times;
	}
	
	public double getAverageScore(){
		return (totalScore / times);
	}

}
