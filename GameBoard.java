import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;  
/**
 * @author Hop N Pham
 * 
 * Pentago game board class. 
 * Controlls states and moving.
 */
public class GameBoard {
	private char myStateBoard[][];
	public int myHeuristic;		
	public char playerColor;
	public char player_Tow_Color;	
	
	/**
	 * Initializes the state of 2D array game board.
	 */
	public GameBoard() {
		myStateBoard = new char[6][6];
	}
	/**
	 * Set the location that a player moved 
	 * @param row location.
	 * @param col location.
	 * @param color 'W' or 'B'.
	 */
	public void addMove(int row, int col, char color) {
		myStateBoard[row][col] = color;
	}

	/**
	 * Call helper method to check the result of game.
	 * 
	 * @return stats of game t is Tie, r is runing, or color of winer.
	 */
	public char gameStats() {
		boolean isFull = true;
		char status = 'r'; //runable
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (myStateBoard[i][j] < 10) {
					isFull = false;
					break;
				}
			}
		}	
		if (isFull) {
			status = 't'; //tie when gameboard fulle
		}
		String result = checkRow() + "" + checkCol() + checkDL() + checkDR();
		if (result.contains("B") && result.contains("W")) return 't';
		else if (!result.contains("B") && result.contains("W")) status = 'W';
		else if (result.contains("B") && !result.contains("W")) status = 'B';
		return status;
	}	
	/**
	 * Rotate a block 90 degree clockwise.
	 * @param theBlock the block that will rotate.
	 */
	public void rotateClockwise(int theBlock) {
		char temp;
		int offsetRow = getOffsetRow(theBlock);
		int offsetCol = getOffsetCol(theBlock);
		temp = myStateBoard[offsetRow][offsetCol]; //move corner around
		myStateBoard[offsetRow][offsetCol] = myStateBoard[2 + offsetRow][offsetCol];
		myStateBoard[2 + offsetRow][offsetCol] = myStateBoard[2 + offsetRow][2 + offsetCol];
		myStateBoard[2 + offsetRow][2 + offsetCol] = myStateBoard[offsetRow][2 + offsetCol];
		myStateBoard[offsetRow][2 + offsetCol] = temp;
		temp = myStateBoard[offsetRow][1 + offsetCol]; //move center of edges around
		myStateBoard[offsetRow][1 + offsetCol] = myStateBoard[1 + offsetRow][offsetCol];
		myStateBoard[1 + offsetRow][offsetCol] = myStateBoard[2 + offsetRow][1 + offsetCol];
		myStateBoard[2 + offsetRow][1 + offsetCol] = myStateBoard[1 + offsetRow][2 + offsetCol];
		myStateBoard[1 + offsetRow][2 + offsetCol] = temp;
		
	}
	
	/**
	 * Rotate a block 90 degree counterclockwise.
	 * @param theBlock the block that will rotate.
	 */
	public void rotateCounterClockwise(int theBlock) {
		char temp;
		int offsetRow = getOffsetRow(theBlock);
		int offsetCol = getOffsetCol(theBlock);
		temp = myStateBoard[offsetRow][offsetCol]; //move corner around
		myStateBoard[offsetRow][offsetCol] = myStateBoard[offsetRow][offsetCol + 2];
		myStateBoard[offsetRow][offsetCol + 2] = myStateBoard[2 + offsetRow][2 + offsetCol];
		myStateBoard[2 + offsetRow][2 + offsetCol] = myStateBoard[offsetRow + 2][offsetCol];
		myStateBoard[offsetRow + 2][offsetCol] = temp;
		temp = myStateBoard[offsetRow][1 + offsetCol]; //move center of edges around
		myStateBoard[offsetRow][1 + offsetCol] = myStateBoard[1 + offsetRow][offsetCol + 2];
		myStateBoard[1 + offsetRow][offsetCol + 2] = myStateBoard[2 + offsetRow][1 + offsetCol];
		myStateBoard[2 + offsetRow][1 + offsetCol] = myStateBoard[1 + offsetRow][offsetCol];
		myStateBoard[1 + offsetRow][offsetCol] = temp;		
	}

	/**
	 * Get the offset of the actual column base on the block format.
	 * @param theBlock the block
	 * @return offset column
	 */
	private int getOffsetCol(int theBlock) {
		return (theBlock == 4 || theBlock == 2)?3:0;
	}
	
	/**
	 * Get the offset of the actual row base on the block format.
	 * @param theBlock the block
	 * @return offset row
	 */
	private int getOffsetRow(int theBlock) {
		return (theBlock == 4 || theBlock == 3)?3:0;
	}
	/**
	 * Check and call method to rotate the block, 0 = False = L, 1 = True = R
	 * 
	 * @param theBlock block to rotate.
	 * @param theDirection true is Clockwise false and is numbererClockwise
	 */
	public void rotateBoard(int theBlock, boolean theDirection) {
		if (theDirection) {
			rotateClockwise(theBlock);
		} else {
			rotateCounterClockwise(theBlock);
		}
	}
	
	/**
	 * Check valid the position.
	 * 
	 * @param row location.
	 * @param col location.
	 * @return whether the location is valid
	 */
	public boolean isPositionValid(int row, int col) {
		return myStateBoard[row][col] < 10;		
	}
	
	/**
	 * Convert the user format to actural position.
	 * 
	 * @param block the quadrant.
	 * @param position in the block.
	 * @return returns position in matrix 6x6.
	 */
	public int[] convertToActualPosition(int block, int position) {
		switch (block) {
			case 1:
				return new int[] {(position - 1) / 3, (position - 1) % 3};
			case 2:
				return new int[] {(position - 1) / 3, (position - 1) % 3 + 3};
			case 3:
				return new int[] {(position - 1) / 3 + 3, (position - 1) % 3};
			default:
				return new int[] {(position - 1) / 3 + 3, (position - 1) % 3 + 3};
		}
	}
	
	/**
	 * Take actual position then return in user coordinate format.
	 * 
	 * @param row location in actual array.
	 * @param col location in actual array.
	 * @return returns the user coordinate format.
	 */
	public int[] reverseToHumanFormat(int row, int col) {
		int block = 0, position = 0;	
		if (row < 3) {
			block = (col < 3)?1:2;
		} else {
			block = (col < 3)?3:4;
		}
		position = (col % 3) + (3 * (row % 3)) + 1;
		return new int[] {block,position};
	}

	/**
	 * Check valid for the input of user.
	 * @param theMove the user input string.
	 * @return true if valid otherwise false.
	 */
	public boolean isValid(String theMove) {
		if (Pattern.matches("[1-4]/[1-9] [1-4][rRlL]", theMove)) {
			int[] movePosition = convertToActualPosition(theMove.charAt(0) - '0',theMove.charAt(2) - '0');
			return isPositionValid(movePosition[0],movePosition[1]);
		}
		return false;
	}
	/**
	 * Calculate the total heuristic of cols.
	 * @param color the target color
	 * @param isAlpha is the alpha
	 * @return the total heuristic for the cols.
	 */
	private int getColHeuristic(char color, boolean isAlpha) {
		char preColor = 1, currentColor = 0;
		int score = 0, validSpot = 0; 
		int number = 1;
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 6; c++) {
				currentColor = myStateBoard[c][r];				
				if (myStateBoard[c][r] < 10 || currentColor == color) {
					if (validSpot == 6 || validSpot == 5) score++;
					validSpot++;					
				} else validSpot = 0;
				if (currentColor == preColor && currentColor > 10) {
					numColor(number, isAlpha, currentColor);
					number++;					
				} else {
					preColor = currentColor;
					number = 1;
				}
			}
			validSpot = 0;
			preColor = 1;
		}
		return score;
	}
	/**
	 * Calculate the total heuristic of rows.
	 * @param color the target color
	 * @param isAlpha is the alpha
	 * @return the total heuristic for the rows.
	 */
	private int getRowHeuristic(char color, boolean isAlpha) {
		char preColor = 1, currentColor = 0;
		int score = 0, validSpot = 0, number = 1;
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 6; c++) {
				currentColor = myStateBoard[r][c];				
				if (myStateBoard[r][c] < 10 || currentColor == color) {
					if (validSpot == 6 || validSpot == 5)					
					score++;
					validSpot++;
				} else 	validSpot = 0;				
				if (currentColor == preColor && currentColor > 10) {										
					numColor(number, isAlpha, currentColor);	
					number++;
				} else {
					preColor = currentColor;
					number = 1;
				}
			}
			validSpot = 0;
			preColor = 1;
		}		
		return score;
	}
	/**
	 * Calculate the total of heuristic for all directions.
	 * @param isAlpha is alpha.
	 * @return the total heuristic value.
	 */
	public int totalHeuristic(boolean isAlpha) {		
		char color = (isAlpha)?playerColor:player_Tow_Color;
		int score = getRowHeuristic(color,isAlpha) + getColHeuristic(color,isAlpha);
		score += getDLScore(color,isAlpha) + getDRScore(color,isAlpha);
		return (score + myHeuristic);
	}
	/**
	 * Heuristic base on the number of color in row.
	 * @param number of color in row.
	 * @param isAlpha is the alpha player.
 	 * @param curColor the current character.
	 * @param preColor the previous character.
	 */
	private void numColor(int number, boolean isAlpha, char color) {
		if (isAlpha) numAlpha(color,number);
		else  numBeta(color,number);
	}
	private void numBeta(char color, int number) {
		switch (number) {
			case 5: myHeuristic += (color == player_Tow_Color)?-50:50;
				break;
			case 4: myHeuristic += (color == player_Tow_Color)?-5:5;
				break;
			case 3: myHeuristic += (color == player_Tow_Color)?-2:2;
				break;
			case 2: myHeuristic += (color == player_Tow_Color)?-1:1;
				break;
			default: break;
		}	
	}
	private void numAlpha(char color, int number) {
		switch (number) {
			case 5: myHeuristic += (color == playerColor)?-50:50;
				break;
			case 4: myHeuristic += (color == playerColor)?-5:5;
				break;
			case 3: myHeuristic += (color == playerColor)?-2:2;
				break;
			case 2: myHeuristic += (color == playerColor)?-1:1;
				break;
			default: break;
		}	
	}
	
	private int getDLScore (char color, boolean isAlpha) {
		char preColor = 1, currentColor = 0;
		int score = 0, validSpot = 0; 
		int offset1 = 0, offset2 = 0, number = 1;
		//upper left to bottom right
		for (int size = 0; size < 3; size++) {			
			if (size == 1) { offset1 = 1; offset2 = 0;}
			if (size == 2) { offset1 = 0; offset2 = 1;}
			int col  = 0;
			for (int i = 0; i < 6; i++) {
				if (i + offset1 < 6 && col  + offset2 < 6) {
					currentColor = myStateBoard[i + offset1][col  + offset2];
					if (currentColor < 10 || currentColor == color) {
						validSpot++;
						if (validSpot == 6 || validSpot == 5) score++; 
					} else validSpot = 0;					
					if (currentColor == preColor && currentColor > 10) {
						number++;						
						numColor(number, isAlpha, currentColor);						
					} else { 						
						preColor = currentColor;
						number = 1;
					}
				} col += 1;
			}
			preColor = 1;
			validSpot = 0;
		}
		return score;
	}
	
	private int getDRScore(char color, boolean isAlpha) {
		char preColor = 1, currentColor = 0;
		int score = 0, validSpot = 0; 
		int offset1 = 0, offset2 = 0, number = 1;
		for (int size = 0; size < 3; size++) {			
			if (size == 1) { offset1 = 1; offset2 = 0;}
			if (size == 2) { offset1 = 0; offset2 = 1;}
			int col = 0;
			for (int i = 5; i >= 0; i--) {				
				if (i - offset1 >= 0 && col + offset2 < 6) {
					currentColor = myStateBoard[i - offset1][col + offset2];
					if (currentColor < 10 || currentColor == color) {
						validSpot++;
						if (validSpot == 6 || validSpot == 5) score++; 
					} else validSpot = 0;
					if (currentColor == preColor && currentColor > 10) {
						number++;						
						numColor(number, isAlpha, currentColor);						
					} else {						
						preColor = currentColor;
						number = 1;
					}
				} col++;
			}
			preColor = 1;
			validSpot = 0;
		}
		return score;		
	}
	
	/**
	 * Check Cols to find a a color has 5 in a row. 	 * 
	 * @return the color of winer or runable 'r'
	 */
	private char checkCol() {		
		char preChar = 0, currentChar = 0;
		int number = 1;
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 6; c++) {
				currentChar = myStateBoard[c][r];
				if (currentChar == preChar) {
					number++;					
					if (number == 5 && currentChar > 10) {
						return preChar; //the winer color
					}
				} else {
					preChar = currentChar;
					number = 1;
				}
			}
		}
		return 'r';
	}	
	/**
	 * Check Columns to find a color has 5 in a row. 
	 * @return the color of winer or runable 'r'
	 */
	/**
	 * Check diagonally from top right to bottom left to find a color has 5 in a row. 
	 * @return the color of winer or runable 'r'
	 */
	private char checkDL() {
		char preChar = 0, currentChar = 0;
		int number = 1, offset1 = 0, offset2 = 0;
		boolean range;
		for (int size = 0; size < 3; size++) {
			if (size == 1) {offset1 = 1;}
			else if (size == 2) {offset1 = 0;	offset2 = 1;}
			int col = 0;
			for (int i = 0; i <= 5; i++) {
				range = false;
				if (i + offset1 <= 5 && col + offset2 <= 5) {
					range = true;
					currentChar = myStateBoard[i + offset1][col + offset2];					
					col++;
				}
				if (currentChar == preChar && range) {
					number++;
					if (number == 5 && currentChar > 10) {
						return preChar;
					}
				} else {
					preChar = currentChar;
					number = 1;
				}
			}
		}
		
		return 'r';
	}
	
	/**
	 * Check diagonally from top left to bottom right to find a color has 5 in a row. 
	 * @return the color of winer or runable 'r'
	 */
	private char checkDR() {
		int number = 1;
		char preChar = 0, currentChar = 0;
		int offset1 = 0, offset2 = 0;
		boolean range = false;
		for (int size = 0; size < 3; size++) {			
			if (size == 1) { offset1 = 1; offset2 = 0;}
			else if (size == 2) { offset1 = 0; offset2 = 1;}
			int col = 0;
			for (int i = 5; i >= 0; i--) {//rows check
				range = false;
				if (i - offset1 >= 0 && col + offset2 < 6) {
					range = true;					
					currentChar = myStateBoard[i - offset1][col + offset2];
					col++;
				}
				if (currentChar == preChar && range) {
					number++;
					if (number == 5 && currentChar > 10) {
						return preChar;
					}
				} else {
					preChar = currentChar;
					number = 1;
				}
			}
		}
		return 'r';
	}
	
	/**
	 * Check Rows to find a a color has 5 in a row. 	 * 
	 * @return the color of winer or runable 'r'
	 */
	private char checkRow() {		
		char preChar = 0, currentChar = 0;
		int number = 1;
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 6; c++) {
				currentChar = myStateBoard[r][c];
				if (currentChar == preChar) {
					number++;					
					if (number == 5 && currentChar > 10) {
						return preChar; //the winer color
					}
				} else {
					preChar = currentChar;
					number = 1;					
				}
			}
		}
		return 'r'; //no winer -> ranable
	}
	
	/**
	 * Generate the future possibilities of game state.
	 * @param isAlpha the alpha player.
	 * @return a list of future moves.
	 */
	public List<int[]> generateNodes(boolean isAlpha) {
		List<int[]> nextMoves = new ArrayList<int[]>();
		for (int b = 0; b < 4; b++) {// block to rotate
			for(int r = 0; r < 6; r++) { //row
				for(int c = 0; c < 6; c++) { //column
					if(myStateBoard[r][c] < 10) { //two posible direction
						nextMoves.add(new int[] {r, c, b, 0});
						nextMoves.add(new int[] {r, c, b, 1});
					}
				}
			}
		}
		return nextMoves;
	}
	
	/**
	 * Print the current state of the game.
	 */
	public void printCurrentGame() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (i % 3 == 0 && j == 0) {
					System.out.println("+-------+-------+");
				} 
				if (j == 0 || j == 3) {
					System.out.print("| ");
				}
				
				if (myStateBoard[i][j] < 10) {
					System.out.print(". ");
				} else {
					System.out.print(myStateBoard[i][j] + " ");	
				}				
				if (j == 5) {
					System.out.println("|");
				}
			}
		}
		System.out.println("+-------+-------+"); 
	}

	/**
	 * Compare to other gameboard.
	 * @param theOther the gameboard.
	 * @return result of current state compare to other state.
	 */
	public boolean equals(GameBoard theOther) {
		return this.myStateBoard.equals(theOther.myStateBoard);
	}
}
