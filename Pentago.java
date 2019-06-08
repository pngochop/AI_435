import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Tony
 * 
 * This is the driver class for the Pentago game. The constants here
 * can be toggled to determine depth, AI algorithm of choice (alpha-beta vs.
 * min-max). Debug mode can be toggled. 
 */
public class Pentago {
	public static final int DEPTH = 3;				//how many moves the AI can foresee.
	public static final boolean DEBUG = false;
	public static final boolean MANUAL_FIRST_MOVE = false; //human always first
	public static final int AI_FIRSTMOVE_ROW = 0; 	//used if moves are set to random
	public static final int AI_FIRSTMOVE_COL = 0; 	//used if moves are set to random
	
	//alpha beta pruning. true will enable alpha beta pruning algorithm. false enables min max algorithm
	public static final boolean AB_PRUNE = true; 
	
	public static GameBoard myGameBoard;			//the current gameboard state.
	public static String myPlayerName;				//name of the human.
	public static boolean isGameOver;				
	public static char currentPlayer;				//the color of the current player.
	
	/**
	 * Main method that initializes the gameboard and calls 
	 * the introduction, which prints out the text for the game.
	 * 
	 * @param args command line arguments.
	 * @throws FileNotFoundException
	 */
	public static void main (String args[]) throws FileNotFoundException {
		myGameBoard = new GameBoard();
		Scanner myScanner = new Scanner(System.in);
		intro(myScanner);
		startGame(myScanner);
	}
	
	public static void intro(Scanner myScanner) {
		char color = '\u0000';
		System.out.println("Welcome to Pentago!");
		System.out.println("What is your name?");
		myPlayerName = myScanner.nextLine();
		System.out.println(
				"Hi " + myPlayerName + ", here are the rules:\n" +
				"  Objective: get 5 of your color in a row to win the game.\n"+
				"   Place your pieces with the syntax: b/p bd\n"+
				"   b=block p=position d=direction\n"+
				"\n" +
				"   Block 1  Block 2\n" +
				"   +-------+-------+\r\n" + 
				"   | 1 2 3 | 1 2 3 |\r\n" + 
				"   | 4 5 6 | 4 5 6 |\r\n" + 
				"   | 7 8 9 | 7 8 9 |\r\n" + 
				"   +-------+-------+\r\n" + 
				"   | 1 2 3 | 1 2 3 |\r\n" + 
				"   | 4 5 6 | 4 5 6 |\r\n" + 
				"   | 7 8 9 | 7 8 9 |\r\n" + 
				"   +-------+-------+\n" +
			    "   Block 3  Block 4\n");
		System.out.print("What color would you like? (b or w) ");
		while (color != 'B' && color != 'b' 
				&& color != 'W' && color != 'w') {
			color = (myScanner.nextLine()).charAt(0);
			myGameBoard.setPlayer1color(Character.toUpperCase(color));
		}
		if (color == 'W' || color == 'w') {
			myGameBoard.setPlayer2color('B');
		} else {
			myGameBoard.setPlayer2color('W');
		}
		System.out.println(
			    "Alright, here is your empty board! Good Luck "+ myPlayerName +"!\n" +
			    "You are Player 1 and the computer will be Player 2\n" +
			    "+-------+-------+\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "+-------+-------+\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "+-------+-------+");
	}
	
	/**
	 * Acquires player name, player color and determines
	 * which player goes first.  
	 * 
	 * @param myScanner scanner for user input.
	 * @throws FileNotFoundException
	 */
	public static void startGame(Scanner myScanner) throws FileNotFoundException {
		boolean isPlayer1First;
		
		Random rand = new Random();
		
		if (rand.nextInt(2) == 0) {
			System.out.println("You will move first.");
			isPlayer1First = true;
			currentPlayer = myGameBoard.getPlayer1color();
		} else {
			System.out.println("Player 2 will move first.");
			isPlayer1First = false;
		}
		playGame(myScanner, isPlayer1First);
		myScanner.close();
	}
	
	/**
	 * Driver method that will determine which is the current player,
	 * and handles the turn based game and prints out the board
	 * after each turn. 
	 * 
	 * @param myScanner scanner for user input for player move.
	 * @param player1First if human is first.
	 */
	public static void playGame(Scanner myScanner, boolean player1First) {
		String theMove;
		boolean isValid = false;
		int turnCount = 0;
		
		while (!isGameOver) {
			if (player1First) {
				currentPlayer = myGameBoard.getPlayer1color();
				System.out.println("Player 1 Move: ");
				theMove = myScanner.nextLine();
				
				do {
					if (myGameBoard.isValidSyntax(theMove)) {
						isValid = true;
						int[] myMove = myGameBoard.translateMove(theMove.charAt(0) - '0', theMove.charAt(2) - '0');
						myGameBoard.performMove(myMove[0], myMove[1], currentPlayer);
						
						myGameBoard.rotateBoard(myGameBoard.getBlockToRotate(theMove), myGameBoard.getDirection(theMove));
						
					} else {
						isValid = false;
						theMove = myScanner.nextLine();
					}
				} while (!isValid);
				
				myGameBoard.displayGameBoard();
				player1First = false;
				isGameOver = myGameBoard.isGameOver();
				if (isGameOver) {
					System.out.println("Player 1 wins");
				}
			} else if (turnCount == 0) { 
				currentPlayer = myGameBoard.getPlayer2color();
				playRandomMove();
				player1First = true;
			} else {
				currentPlayer = myGameBoard.getPlayer2color();
				playComputerMove();
				player1First = true;
				isGameOver = myGameBoard.isGameOver();
				if (isGameOver) {
					System.out.println("Player 2 wins");
				}
			}
			turnCount++;
		}
	}
	
	/**
	 * This method handles the computer's method calls to determine
	 * which will be the best move to make next.  
	 */
	public static void playComputerMove() {
		int[] bestAImove = null;
		int num;
		Computer computer = new Computer();
		if (AB_PRUNE) {
			bestAImove = computer.alphaBetaPrun(DEPTH, myGameBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
			num = computer.nodesExpanded;
		} else {
			bestAImove = computer.minMax(DEPTH, myGameBoard, false);
			num = computer.nodesExpanded;
		}
		int[] move = myGameBoard.reverseTranslate(bestAImove[1], bestAImove[2]);
		char dir;
		if (bestAImove[4] == 0) {
			dir = 'L';
		} else {
			dir = 'R';
		}
		System.out.println("Player 2 plays " + move[0] + "/" + move[1] + " " + (bestAImove[3] + 1) + dir + " with a heuristic value of: " + bestAImove[0]);
		myGameBoard.performMove(bestAImove[1], bestAImove[2], currentPlayer);
		myGameBoard.rotateBoard(bestAImove[3], bestAImove[4]);
		myGameBoard.displayGameBoard();
	}	
	
	/**
	 * If random is enabled, then computer will just play a completely
	 * unintelligent random move on the board.
	 */
	public static void playRandomMove() {
		Random rand = new Random();
		if (MANUAL_FIRST_MOVE) {
			myGameBoard.performMove(AI_FIRSTMOVE_ROW, AI_FIRSTMOVE_COL, currentPlayer);
		} else {
			myGameBoard.performMove(rand.nextInt(6), rand.nextInt(6), currentPlayer);
		}
		myGameBoard.displayGameBoard();
	}
}
