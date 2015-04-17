
public class TranspositionTable {
	private int alpha;
	private int beta;
	private int depth;
	private int score;
	
	public TranspositionTable(){
		alpha = 0;
		beta = 0;
		depth = 0;
		score = 0;
	}
	
	public TranspositionTable(int a, int b, int c, int d){
		alpha = a;
		beta = b;
		depth = c;
		score = d;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public int getAlpha(){
		return alpha;
	}
	
	public int getBeta(){
		return beta;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setScore(int a){
		score = a;
	}
	
	public void setBeta(int b){
		beta = b;
	}
	
	public void setDepth(int c){
		depth = c;
	}
	
	public void setAlpha(int d){
		alpha = d;
	}

}
