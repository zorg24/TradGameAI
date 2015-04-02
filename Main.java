public class Main {
    public static void main(String[] args) throws Exception {
        Agent a = new Agent();
        a.playGame();
//        System.setOut(new PrintStream(new GZIPOutputStream(new FileOutputStream("cc.txt.gz"))));
//        ChineseCheckersState c = new ChineseCheckersState();
//        System.out.println(c.dumpState());
//        ArrayList<Move> moves= new ArrayList<Move>();
//        Random r = new Random(0);
//        while(c.winner() == -1) {
//            c.getMoves(moves);
//            System.out.println(moves.size() + " moves found");
//            System.out.println(Arrays.toString(moves.toArray()));
//            Move m = moves.get(r.nextInt(moves.size()));
//            System.out.println(m + "!!!");
//            c.applyMove(m);
//        }
//        System.out.println(c.winner() + " is the winner!!!!");
    }
}
