Gomoku AI (Java)
Project Overview

This project is a console-based Gomoku game implemented in Java featuring both two-player mode and single-player mode against an AI opponent.

Gomoku is a strategic board game where players take turns placing stones on a grid. The objective is to place five consecutive stones in a row, either horizontally, vertically, or diagonally.

This implementation includes an AI opponent powered by the Minimax algorithm with Alpha-Beta pruning, allowing the computer to make strategic decisions and block potential winning moves from the player.

The project was designed to demonstrate concepts in:

Object-Oriented Programming

Game Logic Design

Artificial Intelligence (Minimax)

Algorithm Optimization

Input Validation

Clean Code Structure

Game Features
Two Game Modes

• Two Player Mode – play locally against another human
• Single Player Mode – play against an AI opponent

Smart AI Opponent

The AI player uses:

• Minimax Algorithm
• Alpha-Beta Pruning
• Heuristic Board Evaluation
• Threat Detection (blocking opponent wins)
• Spiral (center-first) move ordering

This allows the AI to:

Block immediate player wins

Prefer central positions

Evaluate board strength before selecting moves

Input Validation

The game includes robust input validation to prevent crashes:

• Prevents invalid board positions
• Prevents selecting occupied cells
• Handles non-numeric inputs safely
• Validates player names and symbols

Technologies Used

Java

Object-Oriented Programming

Minimax Algorithm

Alpha-Beta Pruning

Heuristic Evaluation Functions

Project Structure
Gomoku/
│
├── Main.java        -> Program entry point
├── Game.java        -> Controls game flow and player turns
├── Board.java       -> Board state, win detection, AI logic
├── Player.java      -> Abstract player class
├── Human.java       -> Human player implementation
├── AI.java          -> AI player implementation
└── README.md
How the AI Works

The AI determines its move using three major steps.

1. Immediate Win Detection

Before using Minimax, the AI checks if it can win immediately.

Example:

AI:  b b b b _

If placing a stone completes five in a row, the AI plays that move instantly.

2. Immediate Threat Blocking

If the player is about to win, the AI blocks the move.

Example:

Player: w w w w _

The AI will place a stone in the empty space to prevent the win.

3. Minimax Decision Making

If no immediate threats exist, the AI uses Minimax.

Minimax simulates future moves and evaluates the board.

The algorithm considers:

AI move
├── Human response
├── AI response
├── ...

Each board state receives a score based on how favorable it is.

The AI then chooses the move with the highest evaluation score.

Alpha-Beta Pruning

Alpha-Beta pruning improves performance by cutting off branches of the search tree that cannot affect the final decision.

This reduces unnecessary calculations and speeds up the AI significantly.

Spiral Move Ordering

Instead of scanning the board randomly, the AI prioritizes moves near the center of the board.

This is done using a spiral search pattern starting from the center:

      4 4
3 3   5 5
2 2       6 6

This improves strategic play because strong Gomoku positions typically develop near the center.

How to Run the Project
1. Clone the repository
   git clone https://github.com/yourusername/gomoku-ai.git
2. Compile the program
   javac *.java
3. Run the program
   java Main
   Example Gameplay
   Welcome to Mansi and Sakshat's GOMOKU :)

1. Two Player Mode
2. One Player Mode

AI (b) is thinking...

AI played at row 5 column 5

Players take turns placing stones until:

• A player gets 5 in a row, or
• The board becomes full resulting in a draw.

Learning Outcomes

This project helped reinforce several important software engineering concepts:

Object-Oriented Programming

The program uses inheritance and abstraction to represent different player types.

Game State Management

The board state is tracked and updated after every move.

Artificial Intelligence

The AI player uses Minimax with Alpha-Beta pruning for decision making.

Defensive Programming

The program prevents crashes through strong input validation.

Future Improvements

Possible improvements for this project include:

• GUI version using JavaFX or Swing
• Adjustable AI difficulty levels
• Larger boards (15x15 standard Gomoku)
• Online multiplayer support
• Monte Carlo Tree Search AI