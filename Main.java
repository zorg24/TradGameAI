public class Main {
    public static void main(String[] args) throws Exception {
        Agent a = null;
        if (args.length > 0)
            a = new Agent("p2");
        else
            a = new Agent();
        a.playGame();
    }
}
