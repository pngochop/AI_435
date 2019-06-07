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
	public char myStateBoard[][];
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
	public void addToBoard(int row, int col, char color) {
		this.myStateBoard[row][col] = color;
	}	
	
	/**
	 * Rotate block clockwise 3 time to get counterclockwise. 
	 * @param blockNum the block will rotate.
	 */
	public void rotateCounterClockwise(int blockNum) {
		rotateClockwise(blockNum);
		rotateClockwise(blockNum);
		rotateClockwise(blockNum);		
	}
	
	/**
	 * Rotate a block 90 degree clockwise.
	 * @param theBlock the block that will rotate.
	 */
	public void rotateClockwise(int theBlock) {
		char temp;
		int offsetRow = 0, offsetCol = 0; //default for block 1
		switch (theBlock) {
			case 2: offsetCol = 3; break;
			case 3: offsetRow = 3; break;
			case 4: offsetRow = 3; offsetCol = 3; break;
			default: break;
		}

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
	 * A method to simplify the call to rotate a quadrant of the board.
	 * if direction is 0, block rotates counter-clockwise, otherwise, 
	 * rotates clockwise.
	 * 
	 * @param block target quadrant.
	 * @param dir direction of rotation. 0=CounterClockwise 1=Clockwise
	 */
	public void rotateBoard(int block, int dir) {
		if (dir == 0) {
			rotateCounterClockwise(block);
		} else {
			rotateClockwise(block);
		}
	}
	
	/**
	 * Check and call method to rotate the block, 0 = False = L, 1 = True = R
	 * 
	 * @param theBlock block to rotate.
	 * @param theDirection true is Clockwise false and is CounterClockwise
	 */
	public void rotateBoard(int theBlock, boolean theDirection) {
		if (theDirection) {
			rotateClockwise(theBlock);
		} else {
			rotateCounterClockwise(theBlock);
		}
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
				if (myStateBoard[i][j] == '\u0000') {
					isFull = false;
					break;
				}
			}
		}	
		if (isFull) {
			status = 't'; //tie when gameboard fulle
		}
		String result = checkRow() + "" + checkCol() + checkDL_DR(1) + checkDL_DR(-1);
		if (result.contains("B") && result.contains("W")) return 't';
		else if (!result.contains("B") && result.contains("W")) status = 'W';
		else if (result.contains("B") && !result.contains("W")) status = 'B';
		return status;
	}
	
	/**
	 * Check Rows to find a a color has 5 in a row. 	 * 
	 * @return the color of winer or runable 'r'
	 */
	private char checkRow() {
		int count = 1;
		char preChar = 'i';
		char currentChar;
		for (int r = 0; r < 6; r++) {//row
			for (int c = 0; c < 6; c++) {//column
				currentChar = myStateBoard[r][c];
				if (currentChar == preChar) {
					count++;					
					if (count == 5 && currentChar != '\u0000') {
						return preChar; //the winer color
					}
				} else {
					count = 1;
					preChar = currentChar;
				}
			}
		}
		return 'r'; //no winer -> ranable
	}
	
	/**
	 * Check Columns to find a color has 5 in a row. 
	 * @return the color of winer or runable 'r'
	 */
	private char checkCol() {
		int count = 1;
		char preChar = 'i';
		char currentChar;
		for (int c = 0; c < 6; c++) {//column
			for (int r = 0; r < 6; r++) {//row
				currentChar = myStateBoard[r][c];
				if (currentChar == preChar) {
					count++;					
					if (count == 5 && currentChar != '\u0000') {
						return preChar; //the winer color
					}
				} else {
					count = 1;
					preChar = currentChar;
				}
			}
		}
		return 'r';
	}
	
	/**
	 * Check diagonally from top right to bottom left to find a color has 5 in a row. 
	 * @param direction the left  = 1 or right = -1 for direction 
	 * @return the color of winer or runable 'r'
	 */
	private char checkDL_DR(int direction) {
		int count = 1;
		char preChar = 'i', currentChar = 'i';
		int offset1 = 0, offset2 = 0;
		boolean inBound;
		for (int m = 0; m < 3; m++) {
			int j = 0;
			if (m == 1) {
				offset1 = 1;
			} else if (m == 2) {
				offset1 = 0;
				offset2 = 1;
			}
			for (int i = 0; i < 6; i++) {//rows
				inBound = false;
				if (i + offset1*direction < 6 && j + offset2 < 6) {
					currentChar = myStateBoard[i + offset1*direction][j + offset2];
					j++;
					inBound = true;
				}
				if (currentChar == preChar && inBound) {
					count++;
					
					if (count == 5 && currentChar != '\u0000') {
						return preChar;
					}
				} else {
					count = 1;
					preChar = currentChar;
				}
			}
		}
		
		return 'r';
	}
	
	/**
	 * Check valid the position.
	 * 
	 * @param row location.
	 * @param col location.
	 * @return whether the location is valid
	 */
	public boolean isPositionValid(int row, int col) {
		return myStateBoard[row][col] == '\u0000';		
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
	 * 
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
	 * Generate the future possibilities of game state.	 * 
	 * @param isAlpha the alpha player.
	 * @return a list of future moves.
	 */
	public List<int[]> generateNodes(boolean isAlpha) {
		List<int[]> nextMoves = new ArrayList<int[]>();
		for (int b = 0; b < 4; b++) {// block to rotate
			for(int r = 0; r < 6; r++) { //row
				for(int c = 0; c < 6; c++) { //column
					if(myStateBoard[r][c] == '\u0000') { //two posible direction
						nextMoves.add(new int[] {r, c, b, 0});
						nextMoves.add(new int[] {r, c, b, 1});
					}
				}
			}
		}
		return nextMoves;
	}
	
	/**
	 * Add a valid move into board game.
	 * 
	 * @param row location.
	 * @param col location.
	 * @param color of player.
	 */
	public void addMove(int row, int col, char color) {
		myStateBoard[row][col] = color;		
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
				
				if (myStateBoard[i][j] == '\u0000') {
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
	 * 
	 * @param theOther the gameboard.
	 * @return result of current state compare to other state.
	 */
	public boolean equals(GameBoard theOther) {
		return this.myStateBoard.equals(theOther.myStateBoard);
	}

	/**
	 * Adds up the heuristic: available spots left + points for how many
	 * pieces the player has in a row.
	 * 
	 * @param isAlphaPlayer whether the player in question is alpha.
	 * @return the heuristic score.
	 */
	public int getHeuristicValue(boolean isAlphaPlayer) {
		char previousChar = '$';
		char currentChar = '\u0000';
		char myColor;
		if (isAlphaPlayer) {
			myColor = playerColor;
		} else {
			myColor = player_Tow_Color;
		}
		int numAvailableWinningSpots = 0, countEmpty = 0, 
				offset1 = 0, offset2 = 0, count = 1;
		
		//horizontal
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				currentChar = myStateBoard[i][j];
				
				if (myStateBoard[i][j] == '\u0000' || currentChar == myColor) {
					countEmpty++;
					if (countEmpty == 5) {
						numAvailableWinningSpots++;
					} if (countEmpty == 6) {
						numAvailableWinningSpots++;
					}
				} else {
					countEmpty = 0;
				}
				
				if (currentChar == previousChar && currentChar != '\u0000') {
					count++;
					
					countNumInArow(currentChar, previousChar, count, isAlphaPlayer);
					
				} else {
					count = 1;
					previousChar = currentChar;
				}
			}
			countEmpty = 0;
			previousChar = '$';
		}
		countEmpty = 0;
		
		//vertical
		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 6; i++) {
				currentChar = myStateBoard[i][j];
				
				if (myStateBoard[i][j] == '\u0000' || currentChar == myColor) {
					countEmpty++;
					if (countEmpty == 5) {
						numAvailableWinningSpots++;
					} if (countEmpty == 6) {
						numAvailableWinningSpots++;
					}
				} else {
					countEmpty = 0;
				}

				if (currentChar == previousChar && currentChar != '\u0000') {
					count++;
					
					countNumInArow(currentChar, previousChar, count, isAlphaPlayer);
					
				} else {
					count = 1;
					previousChar = currentChar;
				}
			}
			countEmpty = 0;
			previousChar = '$';
		}
		countEmpty = 0;
		//bottom left to upper right
		for (int m = 0; m < 3; m++) {
			int j = 0;
			if (m == 1) { offset1 = 1; offset2 = 0;}
			if (m == 2) { offset1 = 0; offset2 = 1;}
			for (int i = 5; i >= 0; i--) {
				
				if (i - offset1 >= 0 && j + offset2 < 6) {
					currentChar = myStateBoard[i - offset1][j + offset2];
					if (currentChar == '\u0000' || currentChar == myColor) {
						countEmpty++;
						if (countEmpty == 5) {
							numAvailableWinningSpots++;
						} if (countEmpty == 6) {
							numAvailableWinningSpots++;
						}
					}else {
						countEmpty = 0;
					}
					if (currentChar == previousChar && currentChar != '\u0000') {
						count++;
						
						countNumInArow(currentChar, previousChar, count, isAlphaPlayer);
						
					} else {
						count = 1;
						previousChar = currentChar;
					}
				} 
				j++;
			}
			previousChar = '$';
			countEmpty = 0;
		}
		countEmpty = 0;
		offset1 = 0; 
		offset2 = 0;
		
		//upper left to bottom right
		for (int m = 0; m < 3; m++) {
			int j = 0;
			if (m == 1) { offset1 = 1; offset2 = 0;}
			if (m == 2) { offset1 = 0; offset2 = 1;}
			for (int i = 0; i < 6; i++) {
				if (i + offset1 < 6 && j + offset2 < 6) {
					currentChar = myStateBoard[i + offset1][j + offset2];
					if (currentChar == '\u0000' || currentChar == myColor) {
						countEmpty++;
						if (countEmpty == 5) {
							numAvailableWinningSpots++;
						} if (countEmpty == 6) {
							numAvailableWinningSpots++;
						}
					} else {
						countEmpty = 0;
					}
					
					if (currentChar == previousChar && currentChar != '\u0000') {
						count++;
						
						countNumInArow(currentChar, previousChar, count, isAlphaPlayer);
						
					} else { 
						count = 1;
						previousChar = currentChar;
					}
				} 
				j++;
			}
			previousChar = '$';
			countEmpty = 0;
		}
		return (numAvailableWinningSpots + myHeuristic);
	}
	
	/**
	 * Helper function for the getHeuristicValue to count how many pieces
	 * the player in question has in a row. This method also determines
	 * the point value given to 2, 3, 4, 5 pieces of one color in a row.
	 * 
	 * @param currentChar the current character.
	 * @param previousChar the previous character.
	 * @param count the count of how many of one character is in a row.
	 * @param isAlphaPlayer whether the current player is alpha or not.
	 */
	public void countNumInArow(char currentChar, char previousChar, int count, boolean isAlphaPlayer) {
		if (isAlphaPlayer) {
			if (count == 5 && player_Tow_Color == currentChar) {
				myHeuristic += 500;
			} else if (count == 4 && player_Tow_Color == currentChar) {
				myHeuristic += 50;
			} else if (count == 3 && player_Tow_Color == currentChar) {
				myHeuristic += 20;
			} else if (count == 2 && player_Tow_Color == currentChar) {
				myHeuristic += 10;
			} 
			
			if (count == 5 && playerColor == currentChar) {
				myHeuristic -= 500;
			} else if (count == 4 && playerColor == currentChar) {
				myHeuristic -= 50;
			} else if (count == 3 && playerColor == currentChar) {
				myHeuristic -= 20;
			} else if (count == 2 && playerColor == currentChar) {
				myHeuristic -= 10;
			} 
		} else {
			if (count == 5 && player_Tow_Color == currentChar) {
				myHeuristic -= 500;
			} else if (count == 4 && player_Tow_Color == currentChar) {
				myHeuristic -= 50;
			} else if (count == 3 && player_Tow_Color == currentChar) {
				myHeuristic -= 20;
			} else if (count == 2 && player_Tow_Color == currentChar) {
				myHeuristic -= 10;
			} 
			
			if (count == 5 && playerColor == currentChar) {
				myHeuristic += 500;
			} else if (count == 4 && playerColor == currentChar) {
				myHeuristic += 50;
			} else if (count == 3 && playerColor == currentChar) {
				myHeuristic += 20;
			} else if (count == 2 && playerColor == currentChar) {
				myHeuristic += 10;
			} 
		}
		
	}
}
