
public class TTManager {
	public static TranspositionTable[] table;
	public static int tableSize;
	
	
	public TTManager(){
		tableSize = 10000;
	}
	
	public static void hash(int alpha, int beta, int depth, int score, long hash){
		//Hash some stuff
		TranspositionTable someTable = new TranspositionTable(alpha, beta, depth, score);
	//	table[(int) (hash % tableSize)] = someTable;
	}
	
	public static boolean getHash(int alpha, int beta, int depth, int score, long hash){
		TranspositionTable someTable = new TranspositionTable(alpha, beta, depth, score);
		//May need to check something else instead of them just being equal
		if(table[(int) (hash % tableSize)] == someTable){
			return true;
		}
		
		return false;
	}
	
	public static int returnHash(long hash){
		return table[(int) (hash % tableSize)].getScore();
	}
	
}
