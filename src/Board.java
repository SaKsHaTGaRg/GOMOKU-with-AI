import java.util.ArrayList;
import java.util.List;

public class Board {
    private final char[][] grid;
    private static final int SIZE = 9;

    private int lastHumanMoveRow = -1;
    private int lastHumanMoveCol = -1;

    public Board() {
        grid = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) grid[r][c] = ' ';
        }
    }

    public void setLastHumanMove(int row, int col) {
        lastHumanMoveRow = row;
        lastHumanMoveCol = col;
    }

    public char getOpponentSymbol(char s) {
        s = Character.toLowerCase(s);
        return (s == 'b') ? 'w' : 'b';
    }

    public boolean isCellEmpty(int row, int col) {
        return grid[row][col] == ' ';
    }

    public void makeMove(int row, int col, char symbol) {
        grid[row][col] = Character.toLowerCase(symbol);
    }

    public boolean isBoardFull() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == ' ') return false;
        return true;
    }

    public void printBoard() {
        System.out.print("   ");
        for (int col = 1; col <= SIZE; col++) System.out.print(col + "   ");
        System.out.println();

        System.out.print("  ┌");
        for (int col = 0; col < SIZE - 1; col++) System.out.print("───┬");
        System.out.println("───┐");

        for (int r = 0; r < SIZE; r++) {
            System.out.print((r + 1) + " │");
            for (int c = 0; c < SIZE; c++) {
                System.out.print(" " + grid[r][c] + " │");
            }
            System.out.println();

            if (r < SIZE - 1) {
                System.out.print("  ├");
                for (int col = 0; col < SIZE - 1; col++) System.out.print("───┼");
                System.out.println("───┤");
            }
        }
        System.out.println("  └" + "───┴".repeat(SIZE - 1) + "───┘");
    }

    // ---------------- WIN CHECK ----------------
    public boolean checkWin(char symbol) {
        symbol = Character.toLowerCase(symbol);

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] != symbol) continue;
                if (checkDir(r, c, 0, 1, symbol)) return true;  // →
                if (checkDir(r, c, 1, 0, symbol)) return true;  // ↓
                if (checkDir(r, c, 1, 1, symbol)) return true;  // ↘
                if (checkDir(r, c, 1, -1, symbol)) return true; // ↙
            }
        }
        return false;
    }

    private boolean checkDir(int r, int c, int dr, int dc, char s) {
        for (int i = 0; i < 5; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) return false;
            if (grid[nr][nc] != s) return false;
        }
        return true;
    }

    // ---------------- AI: BEST MOVE ----------------
    public int[] getBestMove(char aiSymbol, char humanSymbol) {
        aiSymbol = Character.toLowerCase(aiSymbol);
        humanSymbol = Character.toLowerCase(humanSymbol);

        // 1) Immediate win
        int[] win = findImmediateWin(aiSymbol);
        if (win != null) return win;

        // 2) Immediate block
        int[] block = findImmediateWin(humanSymbol);
        if (block != null) return block;

        // 3) Minimax (spiral candidate ordering)
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        List<int[]> moves = generateCandidateMovesSpiral();

        // If board is empty, go center
        if (moves.isEmpty()) return new int[]{SIZE / 2, SIZE / 2};

        for (int[] mv : moves) {
            int r = mv[0], c = mv[1];
            if (grid[r][c] != ' ') continue;

            grid[r][c] = aiSymbol;
            int score = minimax(3, Integer.MIN_VALUE, Integer.MAX_VALUE, false, aiSymbol, humanSymbol);
            grid[r][c] = ' ';

            if (score > bestScore) {
                bestScore = score;
                bestMove = new int[]{r, c};
            }
        }

        // fallback
        if (bestMove[0] == -1) return firstEmpty();
        return bestMove;
    }

    private int[] firstEmpty() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == ' ') return new int[]{r, c};
        return new int[]{0, 0};
    }

    // find a move that completes 5 in a row for symbol
    private int[] findImmediateWin(char symbol) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] != ' ') continue;
                grid[r][c] = symbol;
                boolean win = checkWin(symbol);
                grid[r][c] = ' ';
                if (win) return new int[]{r, c};
            }
        }
        return null;
    }

    // ---------------- SNAIL/SPIRAL MOVE ORDERING ----------------
    // Generates candidate moves in spiral order from center, but only near existing stones.
    private List<int[]> generateCandidateMovesSpiral() {
        List<int[]> spiral = spiralOrderCells();

        // If board is empty, return center only
        if (countStones() == 0) return new ArrayList<>();

        // Candidate filter: only moves within distance 2 of any placed stone (speeds minimax a lot)
        List<int[]> candidates = new ArrayList<>();
        for (int[] cell : spiral) {
            int r = cell[0], c = cell[1];
            if (grid[r][c] != ' ') continue;
            if (isNearAnyStone(r, c, 2)) candidates.add(cell);
        }

        // Extra bias: also add neighbors of last human move first (strong defense feel)
        if (lastHumanMoveRow != -1 && lastHumanMoveCol != -1) {
            List<int[]> nearLast = new ArrayList<>();
            for (int dr = -2; dr <= 2; dr++) {
                for (int dc = -2; dc <= 2; dc++) {
                    int nr = lastHumanMoveRow + dr;
                    int nc = lastHumanMoveCol + dc;
                    if (nr < 0 || nr >= SIZE || nc < 0 || nc >= SIZE) continue;
                    if (grid[nr][nc] == ' ') nearLast.add(new int[]{nr, nc});
                }
            }
            // put nearLast at the front if not already in list
            for (int[] m : nearLast) {
                if (!containsMove(candidates, m)) candidates.add(0, m);
            }
        }

        return candidates;
    }

    private boolean containsMove(List<int[]> list, int[] m) {
        for (int[] x : list) if (x[0] == m[0] && x[1] == m[1]) return true;
        return false;
    }

    private int countStones() {
        int cnt = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] != ' ') cnt++;
        return cnt;
    }

    private boolean isNearAnyStone(int r, int c, int dist) {
        for (int rr = Math.max(0, r - dist); rr <= Math.min(SIZE - 1, r + dist); rr++) {
            for (int cc = Math.max(0, c - dist); cc <= Math.min(SIZE - 1, c + dist); cc++) {
                if (grid[rr][cc] != ' ') return true;
            }
        }
        return false;
    }

    // Spiral order starting from center (snail)
    private List<int[]> spiralOrderCells() {
        List<int[]> order = new ArrayList<>();
        int cx = SIZE / 2;
        int cy = SIZE / 2;

        order.add(new int[]{cx, cy});

        int step = 1;
        int r = cx, c = cy;

        while (order.size() < SIZE * SIZE) {
            // right step
            for (int i = 0; i < step; i++) { c++; addIfInside(order, r, c); }
            // down step
            for (int i = 0; i < step; i++) { r++; addIfInside(order, r, c); }
            step++;
            // left step
            for (int i = 0; i < step; i++) { c--; addIfInside(order, r, c); }
            // up step
            for (int i = 0; i < step; i++) { r--; addIfInside(order, r, c); }
            step++;
        }
        return order;
    }

    private void addIfInside(List<int[]> order, int r, int c) {
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) order.add(new int[]{r, c});
    }

    // ---------------- MINIMAX + EVAL ----------------
    private int minimax(int depth, int alpha, int beta, boolean maximizing, char ai, char human) {
        if (checkWin(ai)) return 100000;
        if (checkWin(human)) return -100000;
        if (depth == 0 || isBoardFull()) return evaluate(ai, human);

        List<int[]> moves = generateCandidateMovesSpiral();
        if (moves.isEmpty()) moves = spiralOrderCells();

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] mv : moves) {
                int r = mv[0], c = mv[1];
                if (grid[r][c] != ' ') continue;

                grid[r][c] = ai;
                int val = minimax(depth - 1, alpha, beta, false, ai, human);
                grid[r][c] = ' ';

                best = Math.max(best, val);
                alpha = Math.max(alpha, val);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] mv : moves) {
                int r = mv[0], c = mv[1];
                if (grid[r][c] != ' ') continue;

                grid[r][c] = human;
                int val = minimax(depth - 1, alpha, beta, true, ai, human);
                grid[r][c] = ' ';

                best = Math.min(best, val);
                beta = Math.min(beta, val);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    // Heuristic evaluation:
    // - rewards open lines for AI
    // - penalizes open lines for human
    private int evaluate(char ai, char human) {
        int score = 0;

        // center bonus
        score += centerControl(ai) - centerControl(human);

        // line scoring
        score += scoreLines(ai, human);
        score -= scoreLines(human, ai);

        return score;
    }

    private int centerControl(char s) {
        int bonus = 0;
        int center = SIZE / 2;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] != s) continue;
                int dist = Math.abs(r - center) + Math.abs(c - center);
                bonus += Math.max(0, 6 - dist); // closer to center => more
            }
        }
        return bonus;
    }

    private int scoreLines(char me, char opp) {
        int total = 0;
        // check all 5-length windows in 4 directions
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                total += windowScore(r, c, 0, 1, me, opp);   // →
                total += windowScore(r, c, 1, 0, me, opp);   // ↓
                total += windowScore(r, c, 1, 1, me, opp);   // ↘
                total += windowScore(r, c, 1, -1, me, opp);  // ↙
            }
        }
        return total;
    }

    private int windowScore(int r, int c, int dr, int dc, char me, char opp) {
        int meCount = 0;
        int oppCount = 0;
        int empty = 0;

        // must fit 5
        int endR = r + 4 * dr;
        int endC = c + 4 * dc;
        if (endR < 0 || endR >= SIZE || endC < 0 || endC >= SIZE) return 0;

        for (int i = 0; i < 5; i++) {
            char cell = grid[r + i * dr][c + i * dc];
            if (cell == me) meCount++;
            else if (cell == opp) oppCount++;
            else empty++;
        }

        // blocked window (both players present)
        if (meCount > 0 && oppCount > 0) return 0;

        // scoring (open-ended approximation for 9x9)
        if (meCount == 5) return 50000;
        if (meCount == 4 && empty == 1) return 5000;
        if (meCount == 3 && empty == 2) return 500;
        if (meCount == 2 && empty == 3) return 50;
        if (meCount == 1 && empty == 4) return 5;

        // if only opponent pieces are present in this window, we handle by subtracting in evaluate()
        return 0;
    }

    // ---------------- OPTIONAL: loadBoard ----------------
    // This does: compute best move + place it + print.
    public void loadBoard(char aiSymbol, char humanSymbol) {
        int[] mv = getBestMove(aiSymbol, humanSymbol);
        makeMove(mv[0], mv[1], aiSymbol);
        System.out.println("AI played at row " + (mv[0] + 1) + ", column " + (mv[1] + 1));
        printBoard();
    }
}