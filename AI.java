import java.util.List;

/**
 * @author Hop N Pham
 *
 * Base on the information that human or computer is the first mover then
 * the AI will be alpha/max or the beta/min player.  
 */
public class AI {

	public int myNodes; //expanded nodes

	/**
	 * Undo the move and clear the heuristic 0 = L = false, 1=R = true
	 * @param block the block
	 * @param row the row location
	 * @param col the col location
	 * @param theDirection the direction
	 * @param theGameBoard the board
	 */
	private void undoMove(GameBoard theGameBoard, int block, int row, int col, boolean theDirection) {
		theGameBoard.rotateBoard(block, theDirection);
		theGameBoard.addToBoard(row, col, '\u0000');
		theGameBoard.myHeuristic = 0; //reset heuristic
	}
	/**
	 * Handle the moves base on Alpha-Beta algorithm.
	 * @param depth the depth will expanded.
	 * @param theGameBoard the future gameboard states.
	 * @param alphaPlayer will be human or AI.
	 * @param alpha value of alpha base on calculating heuristic.
	 * @param beta value of beta base on calculating heuristic.
	 * @return returns list of posible moves.
	 */
	public int[] alphaBeta(int depth, GameBoard theGameBoard,
					boolean alphaPlayer, int alpha, int beta) {
		int heuristicValue = 0;
		int bestRow = -1;
		int bestCol = -1;
		int bestBlock = -1;
		int bestDir = -1;
		int row, col, block, dir;
		List<int[]> possibleMoves = theGameBoard.generateNodes(alphaPlayer);
		
		if(depth == 0 || possibleMoves.isEmpty()) {
			myNodes++;
			heuristicValue = theGameBoard.getHeuristicValue(alphaPlayer);
			return new int[] {heuristicValue, bestRow, bestCol};
		} else {
	         for (int[] move : possibleMoves) {
	        	 row = move[0];
	        	 col = move[1];
	        	 block = move[2];
	        	 dir = move[3];
	             if (alphaPlayer) {  // max player (alpha)
//	            	 System.out.print("Human(MAX) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 theGameBoard.addToBoard(row, col, theGameBoard.playerColor);
	            	 theGameBoard.rotateBoard(block, dir);
				     heuristicValue = alphaBeta(depth - 1, theGameBoard, false, alpha, beta)[0];
				     myNodes++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
				     if (heuristicValue > alpha) {
				    	 alpha = heuristicValue;
				    	 bestRow = row;
				    	 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
				     }
				     undoMove(theGameBoard,block,row,col,dir==0);
	             } else {  // min player (beta)
//	            	 System.out.println("Computer(MIN) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 theGameBoard.addToBoard(row, col, theGameBoard.player_Tow_Color);
	            	 theGameBoard.rotateBoard(block, dir);
	            	 heuristicValue = alphaBeta(depth - 1, theGameBoard, true, alpha, beta)[0];
	            	 myNodes++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
	            	 if (heuristicValue < beta) {
	            		 beta = heuristicValue;
	            		 bestRow = row;
	            		 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
	            	 }
	            	 undoMove(theGameBoard,block,row,col,dir==0);
				 }	             
	             if (alpha >= beta) { break; }//pruned off
	          }
	         
	         return new int[] {alphaPlayer?alpha:beta, bestRow, bestCol, bestBlock, bestDir};
	       }
	}
	
	/**
	 * Handle the moves base on Min-Max algorithm.
	 * @param depth the depth will expanded.
	 * @param theGameBoard the future gameboard states.
	 * @param maxPlayer will be human or AI.
	 * @return returns list of posible moves.
	 */
	public int[] minMax(int depth, GameBoard theGameBoard, boolean maxPlayer) {		
		int row, col, block, direction;
		int heuristic = 0, alpha_beta_replace = 0;
		List<int[]> possibleMoves = theGameBoard.generateNodes(maxPlayer);
		if (depth > 0 && !possibleMoves.isEmpty()){
			row = col = block = direction = -1;
	        for (int[] move : possibleMoves) {
	        	if (maxPlayer) {  // max player 
	        		alpha_beta_replace = Integer.MIN_VALUE;
//	            	System.out.print("Human(MAX) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	theGameBoard.addToBoard(move[0], move[1], theGameBoard.playerColor);
	            	theGameBoard.rotateBoard(move[2], move[3] == 1);
				    heuristic = minMax(depth - 1, theGameBoard, false)[0];
	            	myNodes++;
//				    System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
				    if (heuristic > alpha_beta_replace) {
				    	alpha_beta_replace = heuristic;
				    	row = move[0];
				    	col = move[1];
	            		block = move[2];
	            		direction = move[3];
				    }
	            	undoMove(theGameBoard,move[2],move[0],move[1],move[3]==0);
	            } else {  // min player
	            	alpha_beta_replace = Integer.MAX_VALUE;
//	            	System.out.println("Computer(MIN) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	theGameBoard.addToBoard(move[0], move[1], theGameBoard.player_Tow_Color);
	            	theGameBoard.rotateBoard(move[2], move[3] == 1);
	            	heuristic = minMax(depth - 1, theGameBoard, true)[0];
	            	myNodes++;
//				    System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
	            	if (heuristic < alpha_beta_replace) {
	            		alpha_beta_replace = heuristic;
	            		row = move[0];
	            		col = move[1];
	            		block = move[2];
	            		direction = move[3];
	            	}
	            	 undoMove(theGameBoard,move[2],move[0],move[1],move[3]==0);
				 }
	         }
	         return new int[]{alpha_beta_replace, row, col, block, direction};
		} else {
			myNodes++;
			return new int[] {theGameBoard.getHeuristicValue(maxPlayer), -1, -1};
		}
	}
}
