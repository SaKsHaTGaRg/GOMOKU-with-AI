import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    private Player player1;
    private Player player2;
    private final Board board;
    private final Scanner scanner;

    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
    }

    public void selectMode() {
        System.out.println("Welcome to Mansi and Sakshat's GOMOKU :)");
        System.out.println("Please select the mode you wish to play");
        System.out.println("1. Two Player Mode --- Press 1");
        System.out.println("2. One Player Mode --- Press 2");

        int mode = getValidInt(1, 2);

        if (mode == 1) startTwoPlayerMode();
        else startOnePlayerMode();
    }

    private int getValidInt(int min, int max) {
        while (true) {
            try {
                int v = scanner.nextInt();
                if (v >= min && v <= max) return v;
                System.out.println("Enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Numbers only.");
                scanner.nextLine();
            }
        }
    }

    private String getValidName(String prompt) {
        scanner.nextLine(); // clear newline after ints
        while (true) {
            System.out.println(prompt);
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name can’t be empty.");
                continue;
            }
            // allow spaces too (portfolio-friendly)
            if (!name.matches("[a-zA-Z ]+")) {
                System.out.println("Name must be letters only (spaces allowed).");
                continue;
            }
            return name;
        }
    }

    private char getValidSymbol(String prompt) {
        while (true) {
            System.out.println(prompt);
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.equals("b") || s.equals("w")) return s.charAt(0);
            System.out.println("Invalid symbol. Enter b or w.");
        }
    }

    public void startTwoPlayerMode() {
        System.out.println("Welcome to Two Player Mode!");

        String n1 = getValidName("First Player's Name:");
        String n2 = getValidName("Second Player's Name:");
        char s1 = getValidSymbol("First Player's Symbol (b for black, w for white): ");
        char s2 = (s1 == 'b') ? 'w' : 'b';

        System.out.println("Second Player's Symbol: " + s2);

        player1 = new Human(n1, s1);
        player2 = new Human(n2, s2);

        playLoop();
    }

    public void startOnePlayerMode() {
        System.out.println("Welcome to One Player Mode!");

        String n1 = getValidName("Your Name:");
        char humanSymbol = getValidSymbol("Choose your symbol (b/w): ");
        char aiSymbol = (humanSymbol == 'b') ? 'w' : 'b';

        System.out.println("AI symbol: " + aiSymbol);

        player1 = new Human(n1, humanSymbol);
        player2 = new AI(aiSymbol);

        playLoop();
    }

    private void playLoop() {
        boolean gameOver = false;

        // black always goes first
        Player current = (player1.getSymbol() == 'b') ? player1 : player2;

        while (!gameOver) {
            board.printBoard();

            int[] move = current.makeMove(board);
            board.makeMove(move[0], move[1], current.getSymbol());

            if (board.checkWin(current.getSymbol())) {
                board.printBoard();
                System.out.println(current.getName() + " (" + current.getSymbol() + ") wins!");
                gameOver = true;
            } else if (board.isBoardFull()) {
                board.printBoard();
                System.out.println("It's a draw!");
                gameOver = true;
            } else {
                current = (current == player1) ? player2 : player1;
            }
        }
    }
}