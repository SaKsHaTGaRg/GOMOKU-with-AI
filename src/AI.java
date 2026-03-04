public class AI extends Player {
    private final String name = "AI";

    public AI(char symbol) {
        super(symbol);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int[] makeMove(Board board) {
        System.out.println("AI (" + symbol + ") is thinking...");
        return board.getBestMove(symbol, board.getOpponentSymbol(symbol));
    }
}