public class TTEntry {
	private int alpha;
	private int beta;
	private int depth;
	private int score;

	public TTEntry(){
		alpha = 0;
		beta = 0;
		depth = 0;
		score = 0;
	}
	
	public TTEntry(int alpha, int beta, int depth, int score){
		this.alpha = alpha;
		this.beta = beta;
		this.depth = depth;
		this.score = score;
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
