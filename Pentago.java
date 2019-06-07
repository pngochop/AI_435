import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.*; 
/**
 * @author Hop N Pham
 * 
 * The driver class.
 * Pentago is a 2-player game played on	a 6x6 grid.
 * The players alternate turns.
 * The two players are referred	to here	as "W" and "B",	which also signifies the colors	of 
 * the	tokens	(white	and	black)	they	place	on	the	board.
 */
public class Pentago {
	//Depth	level for lookahead.
	public static final int DEPTH = 3;					
	//Default method for AI, otherwise MinMax
	public static final boolean ALPHA_BETA = true; 
	//The current gameboard state.
	public static GameBoard myGameBoard;	
	//The color of the current player.
	public static char currentPlayer;				
	
	/**
	 * Initializes the gameboard then controll the game state.
	 * 
	 * @param args command line arguments.
	 * @throws FileNotFoundException
	 */
	public static void main (String args[]) throws FileNotFoundException {
		Scanner scan = new Scanner(System.in);
		myGameBoard = new GameBoard();		
		init(scan);		
	}
	
	public static void init(Scanner theScanner) throws FileNotFoundException {		
		System.out.println("Welcome to Pentago Game!");
		System.out.print("What is your name?");
		String playerName = theScanner.nextLine();
		System.out.println(
				"To win: get five marbles in a row, in any direction.\n" +
				"Placing one marble: [Block]/[Position] [Block][Direction]\n" +
				"anywhere on the board and twisting any of the game blocks 90 degrees,\n"+
				"in either direction (Left/Right).\n" +
			    "You can place your marble on one game block and twist any other game block.\n" +
				"For example: 1/1 1L or 1/1 2R\n" +
				"   Block 1  Block 2\n" +
				"   +-------+-------+ \t +-------+-------+\r\n" + 
				"   | 1 2 3 | 1 2 3 | \t | . . . | . . . |\r\n" + 
				"   | 4 5 6 | 4 5 6 | \t | . . . | . . . |\r\n" + 
				"   | 7 8 9 | 7 8 9 | \t | 1 . . | . . . |\r\n" + 
				"   +-------+-------+ \t +-------+-------+\r\n" + 
				"   | 1 2 3 | 1 2 3 | \t | . . . | . . . |\r\n" + 
				"   | 4 5 6 | 4 5 6 | \t | . . . | . . . |\r\n" +  
				"   | 7 8 9 | 7 8 9 | \t | . . . | . . . |\r\n" +  
				"   +-------+-------+ \t +-------+-------+\n" +
			    "   Block 3  Block 4      After move 1/1 1L \n");
		
		boolean humanFirstPlay = false;
		char color = 'i', player_2_Color = 'i';
		System.out.print("What color would you like? (b or w) ");
		while (!Pattern.matches("[bBwW]", color+"")) {
			color = (theScanner.nextLine()).charAt(0);			
		}
		myGameBoard.playerColor = Character.toUpperCase(color);
		player_2_Color = (color == 'W' || color == 'w')?'B':'W';
		myGameBoard.player_Tow_Color = player_2_Color;
		
		if (new Random().nextInt(2) == 1) { //Human
			System.out.println("Player 1 " + playerName + ".");
			System.out.println("Player 2 Computer.");
			System.out.println("Player 1 Token Color " + color);
			System.out.println("Player 2 Token Color " + player_2_Color);
			humanFirstPlay = true;
			currentPlayer = myGameBoard.playerColor;
		} else {
			System.out.println("Player 1 Computer.");
			System.out.println("Player 2 " + playerName + ".");
			System.out.println("Player 1 Token Color " + player_2_Color);
			System.out.println("Player 2 Token Color " + color);
		}
		System.out.println(
			    "+-------+-------+\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "+-------+-------+\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "| . . . | . . . |\r\n" + 
			    "+-------+-------+");
		playGame(theScanner, humanFirstPlay);
		theScanner.close();
	}
	
	/**
	 * Controll the game base on the current player.
	 * @param theScanner to scan input from user.
	 * @param humanFirstPlay if human is first player.
	 */
	public static void playGame(Scanner theScanner, boolean humanFirstPlay) {
		String userMove;
		if(!humanFirstPlay) { //Computer move first
			currentPlayer = myGameBoard.player_Tow_Color;
			myGameBoard.addMove(new Random().nextInt(6), new Random().nextInt(6), currentPlayer); // Default First move of computer
			myGameBoard.printCurrentGame();
			humanFirstPlay = true;
		} 
		char stats = 'r';
		while (stats == 'r') { //game is runing
			if (humanFirstPlay) {
				currentPlayer = myGameBoard.playerColor;
				System.out.println("Player move: ");
				userMove = theScanner.nextLine();
				//re-check to edit code for input valid position
				while (!myGameBoard.isValid(userMove)) {
					System.out.print("Invalid Move! Please enter again: ");
					userMove = theScanner.nextLine();					
				}
				int[] myMove = myGameBoard.convertToActualPosition(userMove.charAt(0) - '0', userMove.charAt(2) - '0');
				myGameBoard.addMove(myMove[0], myMove[1], currentPlayer);
				//bock , direction
				myGameBoard.rotateBoard(userMove.charAt(4) - '0', (userMove.charAt(5)+"").equalsIgnoreCase("R"));
				
				myGameBoard.printCurrentGame();
				humanFirstPlay = false;

			}else {
				currentPlayer = myGameBoard.player_Tow_Color;
				computerMove();
				humanFirstPlay = true;
			}
			stats = myGameBoard.gameStats();
		}
		if (stats == 't') System.out.println("The game is tie!");
		else System.out.println("The "+ stats + " Token player is win!");
	}
	
	/**
	 * Handle the move of computer base on AI.
	 */
	public static void computerMove() {		
		AI aI = new AI();
		int[] pickMoveAI = null;
		if (ALPHA_BETA) { //choose alpha beta or min max algorithm
			pickMoveAI = aI.alphaBeta(DEPTH, myGameBoard, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
		} else {
			pickMoveAI = aI.minMax(DEPTH, myGameBoard, false);			
		}
		int[] move = myGameBoard.reverseToHumanFormat(pickMoveAI[1], pickMoveAI[2]);
		char dir = (pickMoveAI[4] == 0)?'L':'R';
		System.out.println("Computer moves " + move[0] + "/" + move[1] + " " + move[0] + dir);
		myGameBoard.addMove(pickMoveAI[1], pickMoveAI[2], currentPlayer);
		myGameBoard.rotateBoard(move[0], pickMoveAI[4] == 1);
		myGameBoard.printCurrentGame();
		//System.out.println(computer.nodesExpanded);
	}	
}