
public class MoveHolder {
	private Move myMove;
	private double myScore;
	private int times;
	
	public MoveHolder(Move aMove, int aScore){
		myMove = aMove;
		myScore = aScore;
		times = 1;
	}
	
	public double getScore(){
		return myScore;
	}
	
	public Move getMove(){
		return myMove;
	}
	
	public void addScore(int aScore){
		times ++;
		myScore = (myScore + Math.sqrt((2 * Math.log(Agent.totalSamples) / times)));
		myScore = myScore / times;
	}
	
	public int times(){
		return times;
	}

}
