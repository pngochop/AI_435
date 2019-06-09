import java.util.List;

/**
 * @author Tony
 *
 * This class is the artifical-intelligence that will be
 * playing against the human. Since it is random who goes
 * first, the AI is not always going to be alpha/max or the
 * beta/min player and same goes with the human.  
 */
public class AI {

	public int nodesExpanded;		//Number of nodes expanded.
	private GameBoard gameBoard;	//the state of the current gameboard.
		
	public AI() {
		//Default constructor
	}
	
	public GameBoard getBoard() {
		return gameBoard;
	}
	
	/**
	 * The alpha-beta-pruning algorithm that the AI will follow
	 * to play the best move possible against the human. The
	 * strength of the AI depends on the depth which is the 
	 * number of moves that the computer can foresee.
	 * 
	 * @param depth number of moves that the computer can foresee.
	 * @param gameBoard the future gameboard states.
	 * @param alphaPlayer whether the AI is an alpha player.
	 * @param alpha the heuristic score of alpha player.
	 * @param beta the heuristic score of beta player.
	 * @return returns the coordinates of intelligent move.
	 */
	public int[] alphaBetaPrun(int depth, GameBoard gameBoard,
					boolean alphaPlayer, int alpha, int beta) {
		int heuristicValue = 0;
		int bestRow = -1;
		int bestCol = -1;
		int bestBlock = -1;
		int bestDir = -1;
		int row, col, block, dir;
		int compCount = 1;
		int humCount = 1;
		List<int[]> nextMoves = gameBoard.getChildren(alphaPlayer);
		
		if(depth == 0 || nextMoves.isEmpty()) {
			nodesExpanded++;
			heuristicValue = gameBoard.getHeuristicValue(alphaPlayer);
			return new int[] {heuristicValue, bestRow, bestCol};
		} else {
	         for (int[] move : nextMoves) {
	        	 row = move[0];
	        	 col = move[1];
	        	 block = move[2];
	        	 dir = move[3];
	             if (alphaPlayer) {  // max player (alpha)
//	            	 System.out.print("Human(MAX) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 gameBoard.addToBoard(row, col, gameBoard.player1color);
	            	 gameBoard.rotateBoard(block, dir);
				     heuristicValue = alphaBetaPrun(depth - 1, gameBoard, false, alpha, beta)[0];
				     nodesExpanded++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
				     if (heuristicValue > alpha) {
				    	 alpha = heuristicValue;
				    	 bestRow = row;
				    	 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
				     }
	            	 //undo move
				     if (dir == 0) {
		            	 gameBoard.rotateBoard(block, 1);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             } else {
		            	 gameBoard.rotateBoard(block, 0);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             }
				     gameBoard.clearHeuristic();
	             } else {  // min player (beta)
//	            	 System.out.println("AI(MIN) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 gameBoard.addToBoard(row, col, gameBoard.player2color);
	            	 gameBoard.rotateBoard(block, dir);
	            	 heuristicValue = alphaBetaPrun(depth - 1, gameBoard, true, alpha, beta)[0];
	            	 nodesExpanded++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
	            	 if (heuristicValue < beta) {
	            		 beta = heuristicValue;
	            		 bestRow = row;
	            		 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
	            	 }
	            	 //undo move
	            	 if (dir == 0) {
		            	 gameBoard.rotateBoard(block, 1);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             } else {
		            	 gameBoard.rotateBoard(block, 0);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             }
	            	 gameBoard.clearHeuristic();
				 }
	             //prune off branch
	             if (alpha >= beta) {
	            	 break;
	             }
	          }
	         if (alphaPlayer) {
	        	 return new int[]{alpha, bestRow, bestCol, bestBlock, bestDir};
	         } else {
	        	 return new int[]{beta, bestRow, bestCol, bestBlock, bestDir};
	         }
	       }
	}
	
	/**
	 * The min-max algorithm that the AI will follow to find the
	 * best possible move against the human. The strength of the
	 * AI move will depend on the depth.
	 * 
	 * @param depth number of moves that the computer can foresee.
	 * @param gameBoard future states of the gameboard.
	 * @param alphaPlayer whether the AI is alpha or beta player.
	 * @return returns the coordinates of intelligent move.
	 */
	public int[] minMax(int depth, GameBoard gameBoard, boolean alphaPlayer) {
		int heuristicValue = 0;
		int bestValue = 0;
		int bestRow = -1;
		int bestCol = -1;
		int bestBlock = -1;
		int bestDir = -1;
		int row, col, block, dir;
		int compCount = 1;
		int humCount = 1;
		List<int[]> nextMoves = gameBoard.getChildren(alphaPlayer);
		
		if(depth == 0 || nextMoves.isEmpty()) {
			nodesExpanded++;
			bestValue = gameBoard.getHeuristicValue(alphaPlayer);
			return new int[] {bestValue, bestRow, bestCol};
		} else {
	         for (int[] move : nextMoves) {
	        	 row = move[0];
	        	 col = move[1];
	        	 block = move[2];
	        	 dir = move[3];
	             if (alphaPlayer) {  // max player 
	            	 bestValue = Integer.MIN_VALUE;
//	            	 System.out.print("Human(MAX) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 gameBoard.addToBoard(row, col, gameBoard.player1color);
	            	 gameBoard.rotateBoard(block, dir);
				     heuristicValue = minMax(depth - 1, gameBoard, false)[0];
	            	 nodesExpanded++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
				     if (heuristicValue > bestValue) {
				    	 bestValue = heuristicValue;
				    	 bestRow = row;
				    	 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
				     }
	            	 //undo move
				     if (dir == 0) {
		            	 gameBoard.rotateBoard(block, 1);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             } else {
		            	 gameBoard.rotateBoard(block, 0);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             }
				     gameBoard.clearHeuristic();
	             } else {  // min player
	            	 bestValue = Integer.MAX_VALUE;
//	            	 System.out.println("AI(MIN) Move: "+"("+row+","+col+")"+block+"/"+dir+" " + humCount++ + "/" + nextMoves.size() + " | ");
	            	 gameBoard.addToBoard(row, col, gameBoard.player2color);
	            	 gameBoard.rotateBoard(block, dir);
	            	 heuristicValue = minMax(depth - 1, gameBoard, true)[0];
	            	 nodesExpanded++;
//				     System.out.println("Depth: "+ depth+ " Heuristic Value: " + heuristicValue + " ");
	            	 if (heuristicValue < bestValue) {
	            		 bestValue = heuristicValue;
	            		 bestRow = row;
	            		 bestCol = col;
	            		 bestBlock = block;
	            		 bestDir = dir;
	            	 }
	            	 //undo move
	            	 if (dir == 0) {
		            	 gameBoard.rotateBoard(block, 1);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             } else {
		            	 gameBoard.rotateBoard(block, 0);
		            	 gameBoard.addToBoard(row, col, '\u0000');
		             }
	            	 gameBoard.clearHeuristic();
				 }
	          }
	         if (alphaPlayer) {
	        	 return new int[]{bestValue, bestRow, bestCol, bestBlock, bestDir};
	         } else {
	        	 return new int[]{bestValue, bestRow, bestCol, bestBlock, bestDir};
	         }
		}
	}
}
