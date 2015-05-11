public class Main {
    public static void main(String[] args) throws Exception {
        String name = "UCBGarrettNathan";
        String eval = "a";
        if (args.length > 0)
            eval = args[0];
        if (args.length > 1)
            name = args[1];
        Agent a = new Agent(name, eval);
        a.playGame();
    }
}
