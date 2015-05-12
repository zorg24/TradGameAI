import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Function;

public class Agent {
	LinkedList<Move> p1Queue;
	LinkedList<Move> p2Queue;
	int nodeVisited = 0;

	public void init() {
		p1Queue = new LinkedList<Move>();
		p2Queue = new LinkedList<Move>();

		p1Queue.addFirst(new Move(3, 12));
		p1Queue.addLast(new Move(1, 21));
		p1Queue.addLast(new Move(18, 22));
		p1Queue.addLast(new Move(22, 31));
		p1Queue.addLast(new Move(0, 40));

		p2Queue.addFirst(new Move(77, 68));
		p2Queue.addLast(new Move(79, 59));
		p2Queue.addLast(new Move(62, 58));
		p2Queue.addLast(new Move(58, 49));
		p2Queue.addLast(new Move(80, 40));

		stdin = new Scanner(System.in);
	}

	public Agent() {
		init();
		name = "MyName";
	}

	Function<Move, Integer> playoutStrategy;

	public Agent(String name, String playout) {
		init();
		this.name = name;
		switch (playout) {
			case "a":
				playoutStrategy = this::randomHelper; break;
			case "b":
			default:
				playoutStrategy = this::randomHelper2; break;
		}
	}

	public void playGame() {
		// Identify myself
		System.out.println("#name " + name);
		System.out.flush();

		// Wait for start of game
		waitForStart();

		// Main game loop
		for (;;) {
			if (current_player == my_player) {
				// My turn

				// Check if game is over
				if (state.gameOver()) {
					System.err.println("I, " + name + ", have lost");
					System.err.flush();
					switchCurrentPlayer();
					continue;
				}

				// Determine next move
				Move m = nextMove();

				// Apply it locally
				state.applyMove(m);

				// Tell the world
				printAndRecvEcho(m.toString());

				// It is the opponents turn
				switchCurrentPlayer();
			} else {
				// Wait for move from other player
				// Get server's next instruction
				String server_msg = readMessage();
				String[] tokens = server_msg.split(" ");

				// if (tokens.length == 5 && tokens[0] == "MOVE") {
				if (isValidMoveMessage(tokens)) {
					// Translate to local coordinates and update our local state
					Move m = state.translateToLocal(tokens);
					state.applyMove(m);

					// It is now my turn
					switchCurrentPlayer();
				} else if (tokens.length == 4 && tokens[0].equals("FINAL")
						&& tokens[2].equals("BEATS")) {
					// Game over
					if (tokens[1].equals(name) && tokens[3].equals(opp_name)) {
						System.err.println("I, " + name + ", have won!");
						System.err.flush();
					} else if (tokens[3].equals(name)
							&& tokens[1].equals(opp_name)) {
						System.err.println("I, " + name + ", have lost.");
						System.err.flush();
					} else {
						System.err
								.println("Did not find expected players in FINAL command.\n"
										+ "Found '"
										+ tokens[1]
										+ "' and '"
										+ tokens[3]
										+ "'. "
										+ "Expected '"
										+ name
										+ "' and '"
										+ opp_name
										+ "'.\n"
										+ "Received message '"
										+ server_msg
										+ "'");
						System.err.flush();
					}
					break;
				} else {
					// Unknown command
					System.err.println("Unknown command of '" + server_msg
							+ "' from the server");
					System.err.print("Tokens: (" + tokens.length + ")");
					for (String s : tokens)
						System.err.print("'" + s + "' ");
					System.err.print("\n");
					System.err.flush();
				}
			}
		}
	}

	int turnNumber = 0;

	private Move nextMove() {
		// Somehow select your next move
		// ArrayList<Move> moves = new ArrayList<Move>();
		// state.getMoves(moves);
		// Move bestMove = moves.get(0);
		// for (Move m : moves) {
		// if (forwardDistance(m.from, m.to) > forwardDistance(bestMove.from,
		// bestMove.to))
		// bestMove = m;
		// }
		// return bestMove;
		nodeVisited = 0;
		Move m = new Move(0, 0);
		Alarm timer = new Alarm(10);
		timer.start();
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int d = 1;
		System.err.println(turnNumber);
		if (turnNumber < 5) {
			if (state.getCurrentPlayer() == 1) {
				if (state.isValidMove(p1Queue.getFirst())) {
					turnNumber++;
					Move b = p1Queue.getFirst();
					p1Queue.removeFirst();
					return b;
				}
			}
			if (state.getCurrentPlayer() == 2) {
				if (state.isValidMove(p2Queue.getFirst())) {
					turnNumber++;
					Move b = p2Queue.getFirst();
					p2Queue.removeFirst();
					return b;
				}
			}
		}
		//for (; !timer.isDone(); d++) {
			tt.clear();
			numTimes = 0 ;
			// minimax(state, d, state.getCurrentPlayer(), m, timer, alpha,
			// beta);
			//m = UCB1(timer);
			m = setTree(timer);
		//}
		//System.err.println("The depth is " + d);
		state.turnNumber++;
		//System.err.println("The number of nodes visited is : " + nodeVisited);
		System.err.println("The number of sims done is " + totalSamples);
		return m;
	}
	
	private int numTimes = 0;

	private int minimax(ChineseCheckersState state, int depth, int playerNum,
			Move best_move, Alarm timer, int alpha, int beta) {
		if (state.getCurrentPlayer() == playerNum) {
			return max2(state, depth, best_move, timer, alpha, beta);
		} else {
			return min2(state, depth, best_move, timer, alpha, beta);
		}
	}

	private int max2(ChineseCheckersState state, int depth, Move best_move,
			Alarm timer, int alpha, int beta) {
		nodeVisited++;
		long hash = state.getHash();
		int best = Integer.MIN_VALUE;
		int v = Integer.MIN_VALUE;
		TTEntry ttEntry = tt.get(hash);
		if (ttEntry != null && ttEntry.getDepth() >= depth) {

			if (ttEntry.getBound() == 0) {
				return ttEntry.getScore();
			}

			if (ttEntry.getBound() == 1 && alpha < ttEntry.getScore()) {
				alpha = ttEntry.getScore();
			}

			if (ttEntry.getBound() == 2 && beta > ttEntry.getScore()) {
				beta = ttEntry.getScore();
			}
		}

		if (depth == 0 || state.gameOver() || timer.isDone()) {
			// return state.eval();
			v = state.eval();
			if (v <= alpha) {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
			} else if (v >= beta) {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
			} else {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
			}
			return v;

		}
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		for (Move m : mov) {
			state.applyMove(m);
			v = Math.max(v,
					min2(state, depth - 1, junkMove, timer, alpha, beta));
			state.undoMove(m);
			if (v > best) {
				best_move.set(m);
				best = v;
			}
			if (best > alpha) {
				alpha = best;
			}
			if (alpha >= beta) {
				break;
			}
		}
		if (best <= alpha) {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
		}
		if (best >= beta) {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
		} else {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
		}
		return best;
	}

	private int min2(ChineseCheckersState state, int depth, Move best_move,
			Alarm timer, int alpha, int beta) {
		nodeVisited++;
		long hash = state.getHash();
		int best = Integer.MAX_VALUE;
		int v = Integer.MAX_VALUE;
		TTEntry ttEntry = tt.get(hash);
		if (ttEntry != null && ttEntry.getDepth() >= depth) {

			if (ttEntry.getBound() == 0) {
				return ttEntry.getScore();
			}

			if (ttEntry.getBound() == 1 && alpha < ttEntry.getScore()) {
				alpha = ttEntry.getScore();
			}

			if (ttEntry.getBound() == 2 && beta > ttEntry.getScore()) {
				beta = ttEntry.getScore();
			}
		}

		if (depth == 0 || state.gameOver() || timer.isDone()) {
			// return state.eval();
			v = state.eval();
			if (v <= alpha) {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
			} else if (v >= beta) {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
			} else {
				tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
			}
			return v;

		}
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		for (Move m : mov) {
			state.applyMove(m);
			v = Math.min(v,
					max2(state, depth - 1, junkMove, timer, alpha, beta));
			state.undoMove(m);
			if (v > best) {
				best = v;
			}
			if (best < beta) {
				beta = best;
			}
			if (alpha >= beta) {
				break;
			}
		}
		if (best <= alpha) {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
		}
		if (best >= beta) {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
		} else {
			tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
		}
		return best;
	}

	public static int totalSamples;

	private Move UCB1(Alarm timer) {
		totalSamples = 0;
		ArrayList<MoveHolder> movHold = new ArrayList<MoveHolder>();
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		for (Move m : mov) {
			int a = playoutStrategy.apply(m);
			double b = (a + Math.sqrt((2 * Math.log(totalSamples) / 1)));
			movHold.add(new MoveHolder(m, b, a));
		}
		while(!timer.isDone()){
			MoveHolder movHolderA = movHold.get(0);
			for(MoveHolder movH : movHold){
				if(movH.getScore() > movHolderA.getScore()){
					movHolderA = movH;
				}
			}
			int a = playoutStrategy.apply(movHolderA.getMove());
			movHolderA.addScore(a);
			movHolderA.computeScore();
		}
		
		MoveHolder movHolderA = movHold.get(0);
//		System.err.println(movHold.size());
		for (MoveHolder movH : movHold) {
//			System.err.println("The values of the moves are " + movH.getAverageScore());
			if (movH.getScore() > movHolderA.getScore()) {
//				System.err.println("The one being compared is" + movH.getAverageScore());
//				System.err.println("The one currently held is" + movHolderA.getAverageScore());
				movHolderA = movH;
			}
		}

		return movHolderA.getMove();
	}

	private int randomHelper(Move move) {
		totalSamples++;
		int me = state.getCurrentPlayer();
		ChineseCheckersState s = new ChineseCheckersState(state);
		s.applyMove(move);
		ArrayList<Move> moves = new ArrayList<>();
		while (!s.gameOver()) {
			s.getMoves(moves);
			if (Math.random() > 0.1) {
				Move bestMove = moves.get(0);
				for (Move m : moves) {
					if (s.forwardDistance(m) > s.forwardDistance(bestMove)) {
						bestMove = m;
					}
				}
				s.applyMove(bestMove);
			} else {
				s.applyMove(moves.get((int) (Math.random() * moves.size())));
			}
		}
		if (s.winner() == me) {
			return 1;
		}
		else {
			return 0;
		}
	}

	private int randomHelper2(Move move) {
		totalSamples++;
		ChineseCheckersState s = new ChineseCheckersState(state);
		s.applyMove(move);
		ArrayList<Move> moves = new ArrayList<>();
		numTimes++ ;
		for (int i = 0; i < 50; i++) {
			if (s.gameOver()){
				return s.eval();
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
	
	private ArrayList<MonteCarloNode> MCTree = new ArrayList<MonteCarloNode>();
	public Move setTree(Alarm timer){
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
			double b = (a + Math.sqrt((2 * Math.log(totalSamples) / 1)));
			state.applyMove(m);
			state.getMoves(temp);
			MonteCarloNode MCNode2 = new MonteCarloNode(temp.size(), new MoveHolder(m, b, a), MCTree.size(), a, MCTree.get(0));
			MCTree.set(j, MCNode2);
			for(int i = 0; i < temp.size(); i++){
				MCTree.add(null);
			}
			state.undoMove(m);
			j++;
		}
		return chooseNodeC(MCTree.get(0), timer);
	}
	
/*	public Move chooseNodeA(Alarm timer){
		MonteCarloNode bestNode = MCTree.get(1);
		//Check the children not the entire thing!!!!!
		for(int i = MCTree.get(0).getStartLocation(); i < MCTree.get(0).getChildren() + MCTree.get(0).getStartLocation() ; i++){
			if(bestNode.getValue() > MCTree.get(i).getValue()){
				bestNode = MCTree.get(i);
			}
		}
		if(MCTree.get(bestNode.getStartLocation()) == null){
			state.applyMove(bestNode.getMove().getMove());
			expand(bestNode, MCTree.get(0));
			Move a = chooseNodeC(bestNode, timer);
			state.undoMove(bestNode.getMove().getMove());
			return a;
		}
		else{
			state.applyMove(bestNode.getMove().getMove());
			Move a = chooseNodeC(bestNode, timer);
			state.undoMove(bestNode.getMove().getMove());
			return a;
		}
	}*/
	
	public void chooseNodeB(MonteCarloNode aNode, Alarm timer){
		MonteCarloNode bestNode = MCTree.get(aNode.getStartLocation());
		for(int i = aNode.getStartLocation(); i < aNode.getChildren() + aNode.getStartLocation() ; i++){
			if(bestNode.getValue() > MCTree.get(i).getValue()){
				bestNode = MCTree.get(i);
			}
		}
			if(MCTree.get(bestNode.getStartLocation()) == null){
				state.applyMove(bestNode.getMove().getMove());
				expand(bestNode, aNode);
				state.undoMove(bestNode.getMove().getMove());
			}
			else{
				state.applyMove(bestNode.getMove().getMove());
				chooseNodeB(bestNode, timer);
				state.undoMove(bestNode.getMove().getMove());
			}
	}
	
	public Move chooseNodeC(MonteCarloNode aNode, Alarm timer){
		while(!timer.isDone()){
			MonteCarloNode bestNode = MCTree.get(aNode.getStartLocation());
			for(int i = aNode.getStartLocation(); i < aNode.getChildren() + aNode.getStartLocation() ; i++){
				if(bestNode.getValue() > MCTree.get(i).getValue()){
					bestNode = MCTree.get(i);
				}
			}
				if(MCTree.get(bestNode.getStartLocation()) == null){
					state.applyMove(bestNode.getMove().getMove());
					expand(bestNode, aNode);
					state.undoMove(bestNode.getMove().getMove());
				}
				else{
					state.applyMove(bestNode.getMove().getMove());
					chooseNodeB(bestNode, timer);
					state.undoMove(bestNode.getMove().getMove());
				}
		}
		return chooseMove();
	}
	
	public Move chooseMove(){
		MonteCarloNode bestNode = MCTree.get(1);
		for(int i = MCTree.get(0).getStartLocation(); i < MCTree.get(0).getChildren() + MCTree.get(0).getStartLocation() ; i++){
			if(bestNode.getAvgValue() > MCTree.get(i).getAvgValue()){
				bestNode = MCTree.get(i);
			}
		}
		return bestNode.getMove().getMove();
	}
	
/*	public void expand2(MonteCarloNode theNode, MonteCarloNode theParent){
		//System.err.println("Getting to expand");
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		int j = theNode.getStartLocation();
		ArrayList<Move> temp = new ArrayList<Move>();
		for(Move m : mov){
			int a = randomHelper2(m);
			double b = (a + Math.sqrt((2 * Math.log(totalSamples) / 1)));
			state.applyMove(m);
			state.getMoves(temp);
			MonteCarloNode MCNode2 = new MonteCarloNode(temp.size(), new MoveHolder(m, b, a), MCTree.size() - 1, a);
			MCTree.add(j, MCNode2);
			theParent.addValue(a);
			for(int i = MCNode2.getStartLocation(); i < temp.size() + MCNode2.getStartLocation() ; i++){
				MCTree.add(i, null);
			}
			state.undoMove(m);
			j++;
		}
		//System.err.println("J is " + (j - theNode.getStartLocation()));
		//System.err.println("Should be " + theNode.getChildren());
	}*/
	
	public void expand(MonteCarloNode theNode, MonteCarloNode theParent){
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		int j = theNode.getStartLocation();
		ArrayList<Move> temp = new ArrayList<Move>();
		for(Move m : mov){
			int a = randomHelper2(m);
			double b = (a + Math.sqrt((2 * Math.log(totalSamples) / 1)));
			state.applyMove(m);
			state.getMoves(temp);
			MonteCarloNode MCNode2 = new MonteCarloNode(temp.size(), new MoveHolder(m, b, a), MCTree.size() - 1, a, theParent);
			MCTree.set(j, MCNode2);
			MonteCarloNode atm = theParent;
			while(atm != null){
				atm.addValue(a);
				atm = atm.getParent();
			}
			theNode.setChildren(mov.size());
			//theParent.addValue(a);
			for(int i = 0; i < temp.size() ; i++){
				MCTree.add(null);
			}
			state.undoMove(m);
			j++;
		}
	}
	
	private Move UCB1Tree(Alarm timer, ArrayList<MonteCarloNode> theTree) {
		ArrayList<MoveHolder> movHold = new ArrayList<MoveHolder>();
		ArrayList<Move> mov = new ArrayList<Move>();
		state.getMoves(mov);
		theTree.add(mov.size(), null);
		
		for (Move m : mov) {
			int a = randomHelper2(m);
			double b = (a + Math.sqrt((2 * Math.log(totalSamples) / 1)));
			//movHold.add(new MoveHolder(m, b, a));
		}
		while(!timer.isDone()){
			MoveHolder movHolderA = movHold.get(0);
			for(MoveHolder movH : movHold){
				if(movH.getScore() > movHolderA.getScore()){
					movHolderA = movH;
				}
			}
			int a = randomHelper2(movHolderA.getMove());
			movHolderA.addScore(a);
			movHolderA.computeScore();
		}
		
		MoveHolder movHolderA = movHold.get(0);
		for (MoveHolder movH : movHold) {
			if (movH.getAverageScore() > movHolderA.getAverageScore()) {
				movHolderA = movH;
			}
		}

		return movHolderA.getMove();
	}

	// private int forwardDistance(int from, int to) {
	// int fromRow = from / 9;
	// int toRow = to / 9;
	// int fromCol = from % 9;
	// int toCol = to % 9;
	// int mult = 1;
	// if (state.getCurrentPlayer() == 2)
	// mult = -1;
	// return ((toRow + toCol) - (fromRow + fromCol))*mult;
	// }

	// Sends a msg to stdout and verifies that the next message to come in is it
	// echoed back. This is how the server validates moves
	private void printAndRecvEcho(String msg) {
		System.out.println(msg);
		System.out.flush();

		String echo_recv = readMessage();
		if (!msg.equals(echo_recv)) {
			System.err.println("Expected echo of '" + msg + "'. Received '"
					+ echo_recv + "'");
			System.err.flush();
		}
	}

	// Reads a line, up to a newline from the server
	private String readMessage() {
		return stdin.nextLine().trim();
	}

	private String[] tokenize(String s) {
		return s.split(" ");
	}

	private void waitForStart() {
		for (;;) {
			String response = readMessage();
			String[] tokens = tokenize(response);

			if (tokens.length == 4 && tokens[0].equals("BEGIN")
					&& tokens[1].equals("CHINESECHECKERS")) {
				// Found BEGIN GAME message, determine if we play first
				if (tokens[2].equals(name)) {
					// We go first!
					opp_name = tokens[3];
					my_player = Players.player1;
					break;
				} else if (tokens[3].equals(name)) {
					// They go first
					opp_name = tokens[2];
					my_player = Players.player2;
					break;
				} else {
					System.err.println("Did not find '" + name
							+ "', my name, in the BEGIN command.\n"
							+ "# Found '" + tokens[2] + "' and '" + tokens[3]
							+ "'" + " as player names. Received message '"
							+ response + "'");
					System.err.flush();
					System.out.println("#quit");
					System.out.flush();
				}
			} else if (response.equals("DUMPSTATE")) {
				System.out.println(state.dumpState());
				System.out.flush();
			} else if (tokens[0].equals("LOADSTATE")) {
				String newState = response.substring(10);
				if (!state.loadState(newState)) {
					System.err.println("Unable to load '" + newState + "'");
					System.err.flush();
				}
			} else if (response.equals("LISTMOVES")) {
				ArrayList<Move> moves = new ArrayList<Move>();
				state.getMoves(moves);
				for (Move m : moves) {
					System.out.print(m.from + ", " + m.to + "; ");
				}
				System.out.print("\n");
				System.out.flush();
			} else if (tokens[0].equals("MOVE")) {
				Move m = state.translateToLocal(tokens);
				// TOOK THIS OUT, MAY NEED IN FUTURE
				// if (!state.applyMove(m)) {
				// System.err.println("Unable to apply move " + m);
				// System.err.flush();
				// }
			} else if (response.equals("NEXTMOVE")) {
				Move m = nextMove();
				System.out.println(m.from + ", " + m.to);
				;
			} else if (response.equals("EVAL")) {
				System.out.println(state.eval());
			} else {
				System.err.println("Unexpected message '" + response + "'");
				System.err.flush();
			}
		}

		// Game is about to begin, restore to start state in case
		// DUMPSTATE/LOADSTATE/LISTMOVES
		// were used
		state.reset();

		// Player 1 goes first
		current_player = Players.player1;
	}

	private void switchCurrentPlayer() {
		current_player = (current_player == Players.player1) ? Players.player2
				: Players.player1;
	}

	private boolean isValidStartGameMessage(String[] tokens) {
		return tokens.length == 4 && tokens[0].equals("BEGIN")
				&& tokens[1].equals("CHINESECHECKERS");
	}

	private boolean isValidMoveMessage(String[] tokens) {
		return tokens.length == 5 && tokens[0].equals("MOVE")
				&& tokens[1].equals("FROM") && tokens[3].equals("TO");
	}

	private TranspositionTable tt = new TranspositionTable();
	private ChineseCheckersState state = new ChineseCheckersState();

	private enum Players {
		player1, player2
	}

	public boolean DEBUG = false;
	private final Move junkMove = new Move(0, 0);
	private Players current_player;
	private Players my_player;
	private String name;
	private String opp_name;
	private Scanner stdin;

}
