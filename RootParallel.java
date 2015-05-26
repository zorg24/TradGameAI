import java.util.ArrayList;


public class RootParallel extends Thread {
	private ChineseCheckersState state;
	private ArrayList<MonteCarloNode> MCTree;
	private Alarm timer;
	public static int totalSamples;
	private MonteCarloNode bestMove;
	public boolean done;
	
	public RootParallel(Alarm time, ChineseCheckersState aState){
		timer = time;
		state = aState;
		MCTree = new ArrayList<MonteCarloNode>();
	}
	
	public MonteCarloNode getMove(){
		return bestMove;
	}
	
	public void run(){
			totalSamples = 0;
			MCTree.clear();
			ArrayList<Move> mov = new ArrayList<Move>();
			state.getMoves(mov);
			MonteCarloNode MCNode = new MonteCarloNode(mov.size(), null, 1);
			MCTree.add(MCNode);
			for(int i = 0; i < mov.size(); i++){
				MCTree.add(null);
			}
			ArrayList<Move> temp = new ArrayList<Move>();
			int j = 1;
			for (Move m : mov) {
				int a = randomHelper2(m);
				state.applyMove(m);
				state.getMoves(temp);
				MonteCarloNode MCNode2 = new MonteCarloNode(mov.size(), m, MCTree.size(), a, MCTree.get(0));
				MCTree.set(j, MCNode2);
				for(int i = 0; i < temp.size(); i++){
					MCTree.add(null);
				}
				state.undoMove(m);
				j++;
			}
			bestMove = chooseNodeC(MCTree.get(0), timer);
			done = true;
		}

	public void chooseNodeB(MonteCarloNode aNode, Alarm timer){
		MonteCarloNode bestNode = MCTree.get(aNode.getStartLocation());
		for(int i = aNode.getStartLocation(); i < aNode.getChildren() + aNode.getStartLocation() ; i++){
			if(MCTree.get(i).getValue() > bestNode.getValue()){
				bestNode = MCTree.get(i);
			}
		}
			if(MCTree.get(bestNode.getStartLocation()) == null){
				state.applyMove(bestNode.getMove2());
				expand(bestNode, aNode);
				state.undoMove(bestNode.getMove2());
			}
			else{
				state.applyMove(bestNode.getMove2());
				chooseNodeB(bestNode, timer);
				state.undoMove(bestNode.getMove2());
			}
	}

	public MonteCarloNode chooseNodeC(MonteCarloNode aNode, Alarm timer){
		while(!timer.isDone()){
			MonteCarloNode bestNode = MCTree.get(aNode.getStartLocation());
			for(int i = aNode.getStartLocation(); i < aNode.getChildren() + aNode.getStartLocation() ; i++){
				if(MCTree.get(i).getValue() > bestNode.getValue()){
					bestNode = MCTree.get(i);
				}
			}
				if(MCTree.get(bestNode.getStartLocation()) == null){
					state.applyMove(bestNode.getMove2());
					expand(bestNode, aNode);
					state.undoMove(bestNode.getMove2());
				}
				else{
					state.applyMove(bestNode.getMove2());
					chooseNodeB(bestNode, timer);
					state.undoMove(bestNode.getMove2());
				}
		}
		return chooseMove();
	}

	public MonteCarloNode chooseMove(){
		MonteCarloNode bestNode = MCTree.get(MCTree.get(0).getStartLocation());
		for(int i = MCTree.get(0).getStartLocation(); i < MCTree.get(0).getChildren() + MCTree.get(0).getStartLocation() ; i++){
			if(MCTree.get(i).getAvgValue() > bestNode.getAvgValue()){
				bestNode = MCTree.get(i);
			}
		}
		return bestNode;
	}

	public void expand(MonteCarloNode theNode, MonteCarloNode theParent){
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		int j = theNode.getStartLocation();
		ArrayList<Move> temp = new ArrayList<Move>();
		for(Move m : mov){
			int a = randomHelper2(m);
			state.applyMove(m);
			state.getMoves(temp);
			MonteCarloNode MCNode2 = new MonteCarloNode(mov.size(), m, MCTree.size(), a, theParent);
			MCTree.set(j, MCNode2);
			MonteCarloNode atm = MCNode2.getParent();
			while(atm != null){
				atm.addValue(a);
				atm = atm.getParent();
			}
			theNode.setChildren(mov.size());
			for(int i = 0; i < temp.size() ; i++){
				MCTree.add(null);
			}
			state.undoMove(m);
			j++;
		}
	}
	
	private int randomHelper2(Move move) {
		totalSamples++;
		ChineseCheckersState s = new ChineseCheckersState(state);
		s.applyMove(move);
		ArrayList<Move> moves = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			if (s.gameOver()){
				return s.eval() - (i * 2);
			}
			s.getMoves(moves);
			if(moves.isEmpty()){
				return s.eval();
			}
			if (Math.random() > 0.1) {
				Move bestMove = moves.get(0);
				for (Move m : moves)
					if (s.forwardDistance(m) > s.forwardDistance(bestMove))
						bestMove = m;
				s.applyMove(bestMove);
			} else {
				s.applyMove(moves.get((int) (Math.random() * moves.size())));
			}
		}
		return s.eval();
	}
}
