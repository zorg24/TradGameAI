import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.Queue;
import java.util.LinkedList;


public class Agent {
	LinkedList<Move> p1Queue;
    LinkedList<Move> p2Queue;
    int nodeVisited = 0 ;
    public Agent() {
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
    	
        name = "MyName";
        stdin = new Scanner(System.in);
    }

    public Agent(String name_) {
        name = name_;
        stdin = new Scanner(System.in);
    }

    public void playGame() {
        // Identify myself
        System.out.println("#name " + name);
        System.out.flush();

        // Wait for start of game
        waitForStart();

        // Main game loop
        for (; ; ) {
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

                //if (tokens.length == 5 && tokens[0] == "MOVE") {
                if (isValidMoveMessage(tokens)) {
                    // Translate to local coordinates and update our local state
                    Move m = state.translateToLocal(tokens);
                    state.applyMove(m);

                    // It is now my turn
                    switchCurrentPlayer();
                } else if (tokens.length == 4 && tokens[0].equals("FINAL") &&
                        tokens[2].equals("BEATS")) {
                    // Game over
                    if (tokens[1].equals(name) && tokens[3].equals(opp_name)) {
                        System.err.println("I, " + name + ", have won!");
                        System.err.flush();
                    } else if (tokens[3].equals(name) && tokens[1].equals(opp_name)) {
                        System.err.println("I, " + name + ", have lost.");
                        System.err.flush();
                    } else {
                        System.err.println(
                                "Did not find expected players in FINAL command.\n"
                                        + "Found '" + tokens[1] + "' and '" + tokens[3] + "'. "
                                        + "Expected '" + name + "' and '" + opp_name + "'.\n"
                                        + "Received message '" + server_msg + "'");
                        System.err.flush();
                    }
                    break;
                } else {
                    // Unknown command
                    System.err.println("Unknown command of '" + server_msg +
                            "' from the server");
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
//        ArrayList<Move> moves = new ArrayList<Move>();
//        state.getMoves(moves);
//        Move bestMove = moves.get(0);
//        for (Move m : moves) {
//            if (forwardDistance(m.from, m.to) > forwardDistance(bestMove.from, bestMove.to))
//                bestMove = m;
//        }
//        return bestMove;
    	nodeVisited = 0;
        Move m = new Move(0,0);
        Alarm timer = new Alarm(10);
        timer.start();
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int d = 1;
        System.err.println(turnNumber);
        if(turnNumber < 5){
        	if(state.getCurrentPlayer() == 1){
        		if(state.isValidMove(p1Queue.getFirst())){
        			turnNumber ++;
        			Move b = p1Queue.getFirst();
        			p1Queue.removeFirst();
        			return b;
        		}
        	}
        	if(state.getCurrentPlayer() == 2){
        		if(state.isValidMove(p2Queue.getFirst())){
        			turnNumber ++;
        			Move b = p2Queue.getFirst();
        			p2Queue.removeFirst();
        			return b;
        		}
        	}
        }
        for (; !timer.isDone(); d++) {
        	tt.clear();
            minimax(state, d, state.getCurrentPlayer(), m, timer, alpha, beta);
        }
        System.err.println("The depth is " + d);
        state.turnNumber ++;
        System.err.println("The number of nodes visited is : " + nodeVisited);
        return m;
    }

    private int minimax(ChineseCheckersState state, int depth, int playerNum, Move best_move, Alarm timer, int alpha, int beta) {
        if (state.getCurrentPlayer() == playerNum) {
            return max(state, depth, best_move, timer, alpha, beta);
        } else {
            return min(state, depth, best_move, timer, alpha, beta);
        }
    }
    
    private int max(ChineseCheckersState state, int depth, Move best_move, Alarm timer, int alpha, int beta) {
    	long hash = state.getHash();
    	if (depth == 0 || state.gameOver() || timer.isDone()) {
           // return state.eval();
        	int b = state.eval();
            if(b >= beta){
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 1));
            }
            else if(b <= alpha){
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 2));
            }
            else{
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 0));
            }
            nodeVisited ++;
            return b;
        }
        int v = Integer.MIN_VALUE;
        int best = Integer.MIN_VALUE;
        ArrayList<Move> mov = new ArrayList<Move>();
        state.getMoves(mov);
        for (Move m : mov) {
            hash = state.applyMove(m);
            TTEntry ttEntry = tt.get(hash);
            if(ttEntry != null && ttEntry.getDepth() >= depth){
            	if(DEBUG){
            		int tempA = 0;
            		int tempB = 0;
            		DEBUG = false;
            		System.err.println(depth);
            		System.err.println(ttEntry.getDepth());
            		int score = max(state, ttEntry.getDepth(), best_move, timer, tempA, tempB);
            		if(tempA <= ttEntry.getAlpha()){
            			System.err.println("Good");
            			System.err.println(tempA);
            			System.err.println(ttEntry.getAlpha());
            		}
            		else{
            			System.err.println("Bad");
            		}
            		if((tempB >= ttEntry.getBeta()) || ttEntry.getBeta() == Integer.MAX_VALUE){
            			System.err.println("Good");
            		}
            		else{
            			System.err.println("Bad");
            			System.err.println(tempB);
            			System.err.println(ttEntry.getBeta());
            		}
            	}
            	// if exact return that
            	// if upper current alpha > entry alpha
            	// if lower current beta < entry beta
            	// then those get those values
            	
            	//return
            	if(ttEntry.getBound() == 0){
            		return ttEntry.getScore();
            	}
            	
            	if(ttEntry.getBound() == 1 && alpha > ttEntry.getAlpha()){
            		alpha = ttEntry.getScore();
            	}
            	
            	if(ttEntry.getBound() == 2 && beta < ttEntry.getBeta()){
            		beta = ttEntry.getScore();
            	}
            	//if(ttEntry.getAlpha() > alpha){
            	//	alpha = ttEntry.getAlpha();
            	//}
            	if(alpha >= beta){
            		//System.err.println("We are hitting this");
            		state.undoMove(m);
            		return ttEntry.getScore();
            	}
            }
            v = Math.max(v, min(state, depth - 1, junkMove, timer, alpha, beta));
            state.undoMove(m);
            
            if(v > best){
            	best = v;
            }
            
            //if( v > alpha ){
            if(best > alpha){
                best_move.set(m);
                //alpha = v;
                alpha = best;
            }
            //if (v == beta or >, upper bound)
            //if (v == < alpha or > beta ) exact
            //if (v == alpha or >alpha, upper bound
            
            //exact
           // if(v > alpha && v < beta){
           // 	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
           // }
            //upper
           // else if(v == alpha || v < alpha ){
           // 	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
           // }
            //lower
           // else if(v == beta || v > beta ){
            //tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
            
/*            if(v >= beta){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
            }
            else if(v <= alpha){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
            }
            else{
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
            }*/
            
            //tt.put(hash, new TTEntry(alpha, beta, depth, v));
            if(beta <= alpha){
                if(v >= beta){
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
                }
                else if(v <= alpha){
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
                }
                else{
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
                }
            	//System.err.println("We are hitting this");
            	return v;
            }
            
        }
        if(v >= beta){
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
        }
        else if(v <= alpha){
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
        }
        else{
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
        }
        return v;
    }
    
    private int min(ChineseCheckersState state, int depth, Move best_move, Alarm timer, int alpha, int beta) {
        long hash = state.getHash();
    	if (depth == 0 || state.gameOver() || timer.isDone()) {
           // return state.eval();
        	int b = state.eval();
            if(b >= beta){
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 1));
            }
            else if(b <= alpha){
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 2));
            }
            else{
            	tt.put(hash, new TTEntry(alpha, beta, depth, b, 0));
            }
            nodeVisited ++;
            return b;
        }
        int best = 0;
        int v = Integer.MAX_VALUE;
        ArrayList<Move> mov = new ArrayList<Move>();
        state.getMoves(mov);
        for (Move m : mov) {
            hash = state.applyMove(m);
            TTEntry ttEntry = tt.get(hash);
            if(ttEntry != null && ttEntry.getDepth() >= depth){
            	
            	if(ttEntry.getBound() == 0){
            		return ttEntry.getScore();
            	}
            	
            	if(ttEntry.getBound() == 1 && alpha > ttEntry.getAlpha()){
            		alpha = ttEntry.getScore();
            	}
            	
            	if(ttEntry.getBound() == 2 && beta < ttEntry.getBeta()){
            		beta = ttEntry.getScore();
            	}
            	
            	//if(ttEntry.getBeta() < beta ){
            		//System.err.println("We are hitting this");
            	//	beta = ttEntry.getBeta();
            	//}	
            	
            	if(alpha >= beta){
            		//System.err.println("We are hitting this");
            		state.undoMove(m);
            		return ttEntry.getScore();
            	}
            }
           //System.err.println("We are hitting this");
            v = Math.min(v, max(state, depth - 1, junkMove, timer, alpha, beta));
            state.undoMove(m);
            if (v < best){
            	best = v;
            }
            //beta = Math.min(v, beta);
            beta = Math.min(best, beta);
/*            if(v >= beta){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
            }
            else if(v <= alpha){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
            }
            else{
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
            }*/
            
            
/*            if(v > alpha && v < beta){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
            }
            //upper
            else if(v == alpha || v < alpha ){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
            }
            //lower
            else if(v == beta || v > beta ){
            	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
            }*/
           // tt.put(hash, new TTEntry(alpha, beta, depth, v));
            if(beta <= alpha){
                if(v >= beta){
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
                }
                else if(v <= alpha){
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
                }
                else{
                	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
                }
            	return v;
            }
            
        }
        if(v >= beta){
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 1));
        }
        else if(v <= alpha){
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 2));
        }
        else{
        	tt.put(hash, new TTEntry(alpha, beta, depth, v, 0));
        }
        return v;
    }


//    private int forwardDistance(int from, int to) {
//        int fromRow = from / 9;
//        int toRow = to / 9;
//        int fromCol = from % 9;
//        int toCol = to % 9;
//        int mult = 1;
//        if (state.getCurrentPlayer() == 2)
//            mult = -1;
//        return ((toRow + toCol) - (fromRow + fromCol))*mult;
//    }

    // Sends a msg to stdout and verifies that the next message to come in is it
    // echoed back. This is how the server validates moves
    private void printAndRecvEcho(String msg) {
        System.out.println(msg);
        System.out.flush();

        String echo_recv = readMessage();
        if (!msg.equals(echo_recv)) {
            System.err.println("Expected echo of '" + msg + "'. Received '" +
                    echo_recv + "'");
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
        for (; ; ) {
            String response = readMessage();
            String[] tokens = tokenize(response);

            if (tokens.length == 4 && tokens[0].equals("BEGIN") &&
                    tokens[1].equals("CHINESECHECKERS")) {
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
                    System.err.println(
                            "Did not find '" + name + "', my name, in the BEGIN command.\n"
                                    + "# Found '" + tokens[2] + "' and '" + tokens[3] + "'"
                                    + " as player names. Received message '" + response + "'");
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
                //TOOK THIS OUT, MAY NEED IN FUTURE
              //  if (!state.applyMove(m)) {
               //     System.err.println("Unable to apply move " + m);
                //    System.err.flush();
                //}
            } else if (response.equals("NEXTMOVE")) {
                Move m = nextMove();
                System.out.println(m.from + ", " + m.to);;
            } else if (response.equals("EVAL")) {
                System.out.println(state.eval());
            }
            else {
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
        current_player = (current_player == Players.player1) ? Players.player2 : Players.player1;
    }

    private boolean isValidStartGameMessage(String[] tokens) {
        return tokens.length == 4 && tokens[0].equals("BEGIN") &&
                tokens[1].equals("CHINESECHECKERS");
    }

    private boolean isValidMoveMessage(String[] tokens) {
        return tokens.length == 5 && tokens[0].equals("MOVE") &&
                tokens[1].equals("FROM") && tokens[3].equals("TO");
    }

    private TranspositionTable tt = new TranspositionTable();
    private ChineseCheckersState state = new ChineseCheckersState();

    private enum Players {player1, player2}
    public boolean DEBUG = false;
    private final Move junkMove = new Move(0, 0);
    private Players current_player;
    private Players my_player;
    private String name;
    private String opp_name;
    private Scanner stdin;

}
