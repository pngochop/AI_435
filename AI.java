import java.util.List;

/**
 * @author Hop N Pham
 *
 * Base on the information that human or computer is the first mover then
 * the AI will be alpha/max or the beta/min player.  
 */
public class AI {

	private int nodesExpanded;
	/**
	 * @return total nodes expanded based on the depth.
	 */
	public int getNodes() {
		return nodesExpanded;
	}
	/**
	 * Handle the moves base on Min-Max algorithm.
	 * @param depth the depth will expanded.
	 * @param theGameBoard the future gameboard states.
	 * @param maxPlayer will be human or AI.
	 * @return returns list of posible moves.
	 */
	public int[] minMax(int depth, GameBoard theGameBoard, boolean maxPlayer) {		
		int row = -1, col = -1, block = -1, direction = -1;
		int heuristic = 0, heuristic_replace = 0;
		List<int[]> possibleMoves = theGameBoard.generateNodes(maxPlayer);
		if (depth > 0 && !possibleMoves.isEmpty()){
	        for (int[] move : possibleMoves) { //[row, col, block, direction]
	        	char color; 
	        	if (maxPlayer) {  // max player
	        		heuristic_replace = Integer.MIN_VALUE;
	        		color = theGameBoard.playerColor;
	        	} else {
	        		heuristic_replace = Integer.MAX_VALUE;
	        		color = theGameBoard.player_Tow_Color;
	        	}
	        	theGameBoard.addMove(move[0], move[1], color);
            	theGameBoard.rotateBoard(move[2], move[3] == 1);
			    heuristic = minMax(depth - 1, theGameBoard, !maxPlayer)[0];
	        	if (maxPlayer && heuristic > heuristic_replace) { //update min max value
				   	heuristic_replace = heuristic;
				}else if (!maxPlayer && heuristic < heuristic_replace) {
	            		heuristic_replace = heuristic;
	            }
	        	nodesExpanded++;
	        	row = move[0];
		    	col = move[1];
        		block = move[2];
        		direction = move[3];
	        	undoMove(theGameBoard,move[2],move[0],move[1],move[3]==0);
	         }
	         return new int[]{heuristic_replace, row, col, block, direction};
		}
		nodesExpanded++;
		return new int[] {theGameBoard.totalHeuristic(maxPlayer)};		
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
	public int[] alphaBeta(int depth, GameBoard theGameBoard, boolean alphaPlayer, int alpha, int beta) {
		int heuristic = 0;
		int row = -1, col = -1, block = -1, direction = -1;
		List<int[]> possibleMoves = theGameBoard.generateNodes(alphaPlayer);		
		if (depth > 0 && !possibleMoves.isEmpty()){
			for (int[] move : possibleMoves) {
				char alpha_Beta_Color = (alphaPlayer)?theGameBoard.playerColor:theGameBoard.player_Tow_Color;
	            theGameBoard.addMove(move[0], move[1], alpha_Beta_Color);
	            theGameBoard.rotateBoard(move[2], move[3] == 1);
				heuristic = alphaBeta(depth - 1, theGameBoard, !alphaPlayer, alpha, beta)[0];
				if (alphaPlayer && heuristic > alpha) {
				   	alpha = heuristic;
				} else if (!alphaPlayer && heuristic < beta) {
	            	beta = heuristic;	            		
				}	            	
	            undoMove(theGameBoard,move[2],move[0],move[1],move[3]==0); //block row col direction
				nodesExpanded++;
	            row = move[0];
		    	col = move[1];
        		block = move[2];
        		direction = move[3];        
	            if (alpha >= beta) { break; } //pruned off
			}	         
			return new int[] {alphaPlayer?alpha:beta, row, col, block, direction};
		}
		nodesExpanded++;
		return new int[] {theGameBoard.totalHeuristic(alphaPlayer)};
	}
	
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
		theGameBoard.addMove(row, col, '\u0000');
		theGameBoard.myHeuristic = 0;
	}
}