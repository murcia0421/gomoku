package domain.Player;

import java.util.ArrayList;

import domain.Board;
 
 
 
/**

* The {@code AggressiveMachinePlayer} class represents a machine player

* with an aggressive playing strategy. It extends the {@link MachinePlayer}

* class and implements a mini-max algorithm to make moves defensively.

*

* <p>The aggressive strategy involves evaluating defensive moves on the game board

* to prevent the opponent from forming winning combinations. The player calculates

* the best moves based on opponent token positions and distances, considering

* horizontal, vertical, and diagonal directions.</p>

*

* <p>The class overrides the {@link #play()} and {@link #miniMax()} methods to

* implement the aggressive playing strategy. It also provides methods for evaluating

* defensive moves and updating the best moves based on opponent positions.</p>

*

* <p>This class assumes a game structure with a {@link Board} and other related

* game elements. It uses a simple heuristic to calculate the distance between

* positions on the board for move evaluations.</p>

*

* <p>Usage:</p>

* <pre>

* {@code

* AggressiveMachinePlayer aggressivePlayer = new AggressiveMachinePlayer();

* int[] bestMove = aggressivePlayer.play();

* }

* </pre>

*

* @author Mateo Foreo Fuentes, Juan Daniel Murcia

* @version 2.0

*/

public class AggressiveMachinePlayer extends MachinePlayer {
 
	public int[][] bestMoves;

	public static int[][] moves = new int[][]{{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

	public static int[][] inverseMoves = new int[][] {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};

	public Node[][] state;

	public int size;

	/**

     * Overrides the play method from the parent class to implement the aggressive

     * playing strategy using the minimax algorithm.

     *

     * @return An array representing the best move based on the aggressive strategy.

     */

	@Override
	public int[] play() {

		try {

			Thread.sleep(timeRetard);

		} catch (InterruptedException e) {

			e.printStackTrace();

		}

		return miniMax();

	}


	/**

     * Implements the minimax algorithm for move evaluation.

     *

     * @return An array representing the best move based on the minimax algorithm.

     */

	@Override

	public int[] miniMax() {

		this.size = game.getSize();		

		evaluateMoveDefensively();

		int[] bestMove = evaluateMovesDefensively();
 
		return bestMove;

	}


	/**

     * Evaluates defensive moves on the board and returns the best move based

     * on the minimax algorithm.

     *

     * @param board The game board to be evaluated.

     * @return An array representing the best defensive move.

     */

	private int[] evaluateMovesDefensively() {

		for (int row = 0; row < size; row++) {

			for (int column = 0; column < size; column++) {

				if (state[row][column].getValue()>0 && !state[row][column].isVisited()) {

					for (int i=0;i<8;i++) {

						if(beTheFirst(row,column,i)) {

							ArrayList<Node> path = dfs(row,column,i,new ArrayList<>());

							for(Node x : path) { x.setLastToken(-1); x.setBlocked(false);}

						}

					}

				}

			}

		}

		int[] bestMove = null;

		int bestEvaluation = Integer.MIN_VALUE;

		for (int l = 0; l < size; l++) {

			for (int m = 0; m < size; m++) {

				if(state[l][m].getValue() == 0 && (game != null && game.getTokenColor(l, m)==null)) {

					int evaluation = bestMoves[l][m];

					if(evaluation > bestEvaluation) {

						bestMove = new int[]{l,m};

						bestEvaluation = evaluation;

					}

				}

			}

		}

		return bestMove;

	}


	/**

     * Initializes the evaluation of defensive moves by setting up the bestMoves matrix.

     *

     * @param board The game board to be evaluated.

     */

	private void evaluateMoveDefensively() {

		bestMoves = new int[size][size];

		state = new Node[size][size];  

		for (int i = 0; i < size; i++) {

			for (int j = 0; j < size; j++) {

				bestMoves[i][j] = 0;

				int value=0;

				if (game.getTokenColor(i, j) != null) {

					value = game.getTokenValue(i, j);

					if (game.getTokenColor(i, j)==color) value = -value;

				}

				state[i][j] = new Node(value,i,j);

			}

		}


	}


	/**

     * Retrieves the positions of opponent tokens on the game board.

     *

     * @return An ArrayList of int arrays representing the positions of opponent tokens.

     */

	private ArrayList<Node> dfs(int i, int j, int direction, ArrayList<Node> path) {

		path.add(state[i][j]);

		if (state[i][j].lastToken()+5 == path.size()) {

		}

		else if (state[i][j].getValue()<0) {

			state[i][j].setBlocked(true);

		}

		else {

			int row = i+moves[direction][0];

			int column = j+moves[direction][1];

			state[i][j].setVisitedDirection(direction);

			if(verify(row,column) && !state[row][column].isVisitedDirection(direction)) {

				if(state[i][j].getValue() > 0) {

					state[row][column].setLastToken(path.size()-1);

				}

				else {

					state[row][column].setLastToken(state[i][j].lastToken());

				}

				path =  dfs(row,column,direction,path);

			}

			else if (!verify(row,column))state[i][j].setBlocked(true);

		}

		int rang = Math.max(path.indexOf(state[i][j])-4,0);

		int limit = Math.min(rang+5, path.size());

		int index = state[i][j].lastToken();

		if(index!=-1) {

			int distances = 0;

			int numTokens = 0;

			for(int k = rang; k < limit;k++) {

				int lt = path.get(k).lastToken();

				int i1 = path.get(k).getRow();

				int j1 = path.get(k).getColumn();

				if ((path.get(k).getValue() > 0) && (lt == -1 || !(lt < rang && lt == k-1))) {

					distances += (5-calculateDistance(i,j,i1,j1));

					numTokens += path.get(k).getValue();

				}

			}

			if(state[i][j].getValue() == 0 ) {

				if (numTokens>=3)numTokens++;

				if (path.get(limit-1).isBlocked() || path.get(0).isBlocked())bestMoves[i][j] += Math.sqrt(Math.pow(distances, numTokens));

				else bestMoves[i][j] += Math.pow(distances, numTokens);

			}

		}

		return path;

	}

	public boolean beTheFirst(int row,int column,int direction) {

		int[] move = inverseMoves[direction];

		boolean flag = true;

		for(int i = 1; i < 5 && flag;i++) {

			int r = row+(move[0]*i);

			int c = column+(move[1]*i);

			if(verify(r,c) && state[r][c].getValue()>0) {

				flag =  false;

			}

			else if(verify(r,c) && state[r][c].getValue()<0) {

				state[row][column].setBlocked(true);

				break;

			}

			else if(!verify(r,c)) {

				state[row][column].setBlocked(true);

				break;

			}

		}

		return flag;

	}

	/**

	 * Verifies if the specified row and column coordinates are within the valid

	 * bounds of the board.

	 *

	 * @param r The row coordinate.

	 * @param c The column coordinate.

	 * @return True if the coordinates are valid, false otherwise.

	 */

	public boolean verify(int r, int c) {

		boolean flag = false;

		if (0 <= r && r < size && 0 <= c && c < size)

			flag = true;

		return flag;

	}


	/**

     * Calculates the distance between two positions on the board.

     *

     * @param row1 The row index of the first position.

     * @param col1 The column index of the first position.

     * @param row2 The row index of the second position.

     * @param col2 The column index of the second position.

     * @return The distance between the two positions.

     */

	private double calculateDistance(int row1, int col1, int row2, int col2) {

		double dis;

		if (Math.abs(row1 - row2) == 1 && Math.abs(col1 - col2) == 1)

			dis = 1;

		else if (Math.abs(row1 - row2) == 2 && Math.abs(col1 - col2) == 2)

			dis = 2;

		else if (Math.abs(row1 - row2) == 3 && Math.abs(col1 - col2) == 3)

			dis = 3;

		else if (Math.abs(row1 - row2) == 4 && Math.abs(col1 - col2) == 4)

			dis = 4;

		else if (Math.abs(row1 - row2) == 5 && Math.abs(col1 - col2) == 5)

			dis = 5;

		else

			dis = Math.sqrt(Math.pow(row1 - row2, 2) + Math.pow(col1 - col2, 2));

		return dis;

	}

}
 
 
class Node{

	private int value;

	private boolean visited;

	private boolean[] visitedDirections;

	private int lastToken;

	private int row;

	private int column;

	private boolean blocked = false;

	public Node(int value,int row, int column){

		this.value = value;

		this.visited = false;

		this.visitedDirections = new boolean[]{false,false,false,false,false,false,false,false};

		this.lastToken = -1;

		this.row = row;

		this.column = column;

	}

	public boolean isBlocked() {

		return blocked;

	}
 
	public void setBlocked(boolean b) {

		blocked = b;

	}
 
	public boolean isVisitedDirection(int direction) {

		return visitedDirections[direction];

	}
 
	public void setLastToken(int lastToken) {

		this.lastToken = lastToken;

	}
 
	public int getRow() {

		return row;

	}
 
	public int getColumn() {

		return column;

	}
 
	public void setVisitedDirection(int direction) {

		visitedDirections[direction] = true;

		visited = true;

		for(boolean x:visitedDirections) {

			if (!x) {

				visited = false;

				break;

			}

		}

	}
 
	public int getValue() {

		return value;

	}
 
	public int lastToken() {

		return lastToken;

	}
 
	public boolean isVisited() {

		return visited;

	}
 
	public void setValue(int value) {

		this.value = value;

	}
 
}