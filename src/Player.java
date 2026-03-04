public abstract class Player {
    protected char symbol; // 'b' or 'w'

    public Player(char symbol) {
        this.symbol = Character.toLowerCase(symbol);
    }

    public char getSymbol() {
        return symbol;
    }

    public abstract String getName();

    // Must return 0-based {row, col}
    public abstract int[] makeMove(Board board);
}