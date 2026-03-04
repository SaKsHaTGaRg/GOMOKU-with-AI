import java.util.InputMismatchException;
import java.util.Scanner;

public class Human extends Player {
    private final String name;
    private final Scanner scanner;

    public Human(String name, char symbol) {
        super(symbol);
        this.name = name;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int[] makeMove(Board board) {
        while (true) {
            try {
                System.out.println(name + " (" + symbol + ") enter row (1-9): ");
                int row = scanner.nextInt();
                System.out.println(name + " (" + symbol + ") enter col (1-9): ");
                int col = scanner.nextInt();

                if (row < 1 || row > 9 || col < 1 || col > 9) {
                    System.out.println("Invalid range. Enter values 1 to 9.");
                    continue;
                }

                row--; col--; // to 0-based

                if (!board.isCellEmpty(row, col)) {
                    System.out.println("That spot is taken. Try again.");
                    continue;
                }

                // important: tell board what the last human move was (helps AI)
                board.setLastHumanMove(row, col);

                return new int[]{row, col};
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Enter numbers only.");
                scanner.nextLine(); // clear buffer
            }
        }
    }
}