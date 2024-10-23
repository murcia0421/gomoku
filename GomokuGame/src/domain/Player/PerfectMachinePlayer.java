package domain.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class PerfectMachinePlayer extends MachinePlayer {

	private static int depth = 5;
	private static int childs = 8;
	private static ArrayList<String> patterns = new ArrayList<>();
	private static ArrayList<Integer> results = new ArrayList<>();
	static {
		patterns.add("111110");results.add(100000);
		patterns.add("-111111-1");results.add(100000);
		patterns.add("-1111110");results.add(100000);
		patterns.add("011110");results.add(1000);
		patterns.add("01111-1");results.add(1000);
		patterns.add("0101110");results.add(1000);
		patterns.add("0110110");results.add(1000);
		patterns.add("01110");results.add(200);
		patterns.add("010110");results.add(200);
		patterns.add("00111-1");results.add(20);
		patterns.add("01011-1");results.add(20);
		patterns.add("01101-1");results.add(20);
		patterns.add("100110");results.add(20);
		patterns.add("101010");results.add(20);
		patterns.add("-101110-1");results.add(20);
		patterns.add("00110");results.add(10);
		patterns.add("01010");results.add(10);
		patterns.add("010010");results.add(10);
		patterns.add("0011-1");results.add(5);
		patterns.add("00101-1");results.add(5);
		patterns.add("01001-1");results.add(5);
		patterns.add("-101010-1");results.add(5);
		patterns.add("-1111-1");results.add(-10);
		patterns.add("-11111-1");results.add(-15);
	}
	// 0: -1, -1 = arriba izquierda
	// 1: -1, 0 = arriba
	// 2: -1, 1 = arriba derecha
	// 3: 0, -1 = izquierda
	// 4: 0, 1 = derecha
	// 5: 1, -1 = abajo izquierda
	// 6: 1, 0 = abajo
	// 7: 1, 1 = abajo derecha
	public static int[][] moves = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 },
			{ 1, 0 }, { 1, 1 } };

	public static int[][] inverseMoves = new int[][] { { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, 1 }, { 0, -1 }, { -1, 1 },
			{ -1, 0 }, { -1, -1 } };

	public int size;

	@Override
	public int[] play() {
		try {
			Thread.sleep(timeRetard);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return miniMax();
	}

	@Override
	public int[] miniMax() {
		long startTime = System.nanoTime();

		int[][] state = makeActualState();
		TState actualState = new TState(state, new int[] { -1, -1 });

		int[] bestMove = maxValue(actualState, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
		int[] move = new int[] { bestMove[1], bestMove[2] };

		long endTime = System.nanoTime();

		long executionTime = (endTime - startTime) / 1_000_000;

		System.out.println("Tiempo de ejecucion de miniMax: " + executionTime + " ms");

		return move;
	}

	private int[][] makeActualState() {
		this.size = game.getSize();
		int[][] actualState = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (game.getTokenColor(i, j) == null)
					actualState[i][j] = 0;
				else if (game.getTokenColor(i, j) == color)
					actualState[i][j] = 1;
				else
					actualState[i][j] = -1;
			}
		}
		return actualState;
	}

	private int[] maxValue(TState state, int depth, Integer alpha, Integer beta) {
		if (state.hasWinner())
			return new int[] { 1000000, state.getActualMove()[0], state.getActualMove()[1] };
		if (depth == 0 ) {
			int[] actualMove = state.getActualMove();
			return new int[] { evaluate(state,1), actualMove[0], actualMove[1] };
		}
		int[][] bestMoves = getBestPossibleMoves(state, childs);
		int value = Integer.MIN_VALUE;
		int[] actualBest = new int[2];
		for (int i = 0; i < bestMoves.length; i++) {
			int[] move = bestMoves[i];
			TState newState = new TState(state.makeNewState(move), move);
			int result = minValue(newState, depth - 1, alpha, beta)[0];
			if (result > value) {
				value = result;
				actualBest = move;
			}
			if (value >= beta)
				return new int[] { value, move[0], move[1] };
			alpha = Math.max(alpha, value);
		}
		return new int[] { value, actualBest[0], actualBest[1] };
	}

	private int[] minValue(TState state, int depth, Integer alpha, Integer beta) {
		if (state.hasWinner()){
			return new int[] { 1000000, state.getActualMove()[0], state.getActualMove()[1] };
		}
		if (depth == 0) {
			int[] actualMove = state.getActualMove();
			return new int[] { evaluate(state,-1), actualMove[0], actualMove[1] };
		}
		int[][] bestMoves = getBestPossibleMoves(state, childs);
		int value = Integer.MAX_VALUE;
		int[] actualBest = new int[2];
		for (int i = bestMoves.length - 1; i >= 0; i--) {
			int[] move = bestMoves[i];
			TState newState = new TState(state.makeNewState(move), move);
			int result = maxValue(newState, depth - 1, alpha, beta)[0];
			if (result < value) {
				value = result;
				actualBest = move;
			}
			if (value <= alpha)
				return new int[] { value, move[0], move[1] };
			beta = Math.min(beta, value);
		}
		return new int[] { value, actualBest[0], actualBest[1] };
	}

	private int[][] getBestPossibleMoves(TState state, int numberOfMoves) {

		this.size = game.getSize();
		int[][] bestMoves1 = evaluateGame(state, false);
		int[][] bestMoves2 = evaluateGame(state, true);

		PriorityQueue<int[]> bestMoves = new PriorityQueue<>(Comparator.comparingInt((int[] a) -> a[0]).reversed());

		for (int l = 0; l < size; l++) {
			for (int m = 0; m < size; m++) {
				int value = (bestMoves1[l][m] + bestMoves2[l][m]) / 2;
				bestMoves.add(new int[] { value, l, m });
			}
		}

		int[][] best = new int[numberOfMoves][2];
		for (int i = 0; i < numberOfMoves; i++) {
			int[] actual = bestMoves.poll();
			best[i] = new int[] { actual[1], actual[2] };
		}
		return best;
	}

	private int[][] evaluateGame(TState state, boolean attack) {
		Node[][] nodeState = new Node[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int value = state.getValue(i, j);
				if (!attack)
					value *= -1;
				nodeState[i][j] = new Node(value, i, j);
			}
		}
		return evaluateGameMoves(nodeState);
	}

	private int[][] evaluateGameMoves(Node[][] state) {
		int[][] bestMoves = new int[size][size];
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				bestMoves[row][column] = 0;
			}
		}
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (state[row][column].getValue() > 0 && !state[row][column].isVisited()) {
					for (int i = 0; i < 8; i++) {
						if (beTheFirst(state, row, column, i)) {
							ArrayList<Node> path = dfs(state, bestMoves, row, column, i, new ArrayList<>());
							for (Node x : path) {
								x.setLastToken(-1);
								x.setBlocked(false);
							}
						}
					}
				}
			}
		}
		return bestMoves;
	}

	private int evaluate(TState state, int player) {
		Node[][] nodeState1 = new Node[size][size];
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int value = player*state.getValue(i, j);
				nodeState1[i][j] = new Node(value, i, j);
			}
		}
		Node[][] nodeState2 = new Node[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int value = (-1)*player*state.getValue(i, j);
				nodeState2[i][j] = new Node(value, i, j);
			}
		}
		int result1 = 0;
		int result2 = 0;
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				if (nodeState1[row][column].getValue() > 0 && !nodeState1[row][column].isVisited()) {
					for (int i = 0; i < 8; i++) {
						if (beTheFirst(nodeState1, row, column, i)) {
							ArrayList<Node> path = dfsEvaluation(nodeState1, row, column, i, new ArrayList<>());
							result1 += analizePath(path);
							for (Node x : path) {
								x.setLastToken(-1);
								x.setBlocked(false);
							}
						}
					}
				}
				if (nodeState2[row][column].getValue() > 0 && !nodeState2[row][column].isVisited()) {
					for (int i = 0; i < 8; i++) {
						if (beTheFirst(nodeState2, row, column, i)) {
							ArrayList<Node> path = dfsEvaluation(nodeState2, row, column, i, new ArrayList<>());
							int val = analizePath(path);
							if (-10<=val && val < 0) val = 1000;
							else if (-20<=val && val < -10) val = 10000;
							result2 += val;
							for (Node x : path) {
								x.setLastToken(-1);
								x.setBlocked(false);
							}
						}
					}
				}
			}
		}
		return result1-result2;
	}

	private ArrayList<Node> dfsEvaluation(Node[][] state, int i, int j, int direction, ArrayList<Node> path) {
		path.add(state[i][j]);

		int row = i + moves[direction][0];
		int column = j + moves[direction][1];

		state[i][j].setVisitedDirection(direction);
		state[i][j].setVisitedDirection(7-direction);
		if (state[i][j].getValue() < 0){}
		else if (verify(row, column) && !state[row][column].isVisitedDirection(direction)) {
			if (path.size() > 1 && path.get(path.size()-1).getValue() == 0){
				if (state[i][j].getValue() > 0)
					path = dfsEvaluation(state, row, column, direction, path);
			}
			else
				path = dfsEvaluation(state, row, column, direction, path);
		}
		else if (!verify(row, column))
			state[i][j].setBlocked(true);
		return path;
	}

	private int analizePath(ArrayList<Node> path){
		int result = 0;
		ArrayList<Integer> arr = new ArrayList<>();
		if (path.get(0).isBlocked())
			arr.add(-1);	
		for (Node node : path){
			arr.add(node.getValue());
		}
		if (path.get(path.size()-1).getValue() > 0 && path.get(path.size()-1).isBlocked())
			arr.add(-1);
		ArrayList<Integer> arrInversed = new ArrayList<>();
		for (int i = arr.size()-1; i >= 0; i--){
			arrInversed.add(arr.get(i));
		}
		String pattern = "";
		String reverse = "";
		for (int i = 0; i < arr.size(); i++){
			pattern += arr.get(i);
			reverse += arrInversed.get(i);
		}
		for (int i = 0; i < patterns.size(); i++){
			if (pattern.equals(patterns.get(i))) result += results.get(i);
			else if (reverse.equals(patterns.get(i))) result += results.get(i);
			else if (!path.get(0).isBlocked() && ("0"+pattern).equals(patterns.get(i))) 
				result += results.get(i);
			else if (!path.get(path.size()-1).isBlocked() && (pattern+"0").equals(patterns.get(i))) 
				result += results.get(i);
			
		}
		return result;
	}

	

	private ArrayList<Node> dfs(Node[][] state, int[][] bestMoves, int i, int j, int direction, ArrayList<Node> path) {
		path.add(state[i][j]);
		if (state[i][j].lastToken() + 5 == path.size()) {
		} else if (state[i][j].getValue() < 0) {
			state[i][j].setBlocked(true);
		} else {
			int row = i + moves[direction][0];
			int column = j + moves[direction][1];

			state[i][j].setVisitedDirection(direction);

			if (verify(row, column) && !state[row][column].isVisitedDirection(direction)) {
				if (state[i][j].getValue() > 0)
					state[row][column].setLastToken(path.size() - 1);
				else
					state[row][column].setLastToken(state[i][j].lastToken());
				path = dfs(state, bestMoves, row, column, direction, path);
			} else if (!verify(row, column))
				state[i][j].setBlocked(true);
		}

		int rang = Math.max(path.indexOf(state[i][j]) - 4, 0);
		int limit = Math.min(rang + 5, path.size());
		int index = state[i][j].lastToken();

		if (index != -1) {
			int distances = 0;
			int numTokens = 0;

			for (int k = rang; k < limit; k++) {
				int lt = path.get(k).lastToken();
				int i1 = path.get(k).getRow();
				int j1 = path.get(k).getColumn();

				if ((path.get(k).getValue() > 0) && (lt == -1 || !(lt < rang && lt == k - 1))) {
					distances += (5 - calculateDistance(i, j, i1, j1));
					numTokens += path.get(k).getValue();
				}
			}

			if (state[i][j].getValue() == 0) {
				if (numTokens == 3)
					numTokens++;
				if (numTokens == 4)
					numTokens+=2;
				if (path.get(limit - 1).isBlocked() || path.get(0).isBlocked())
					bestMoves[i][j] += Math.sqrt(Math.pow(distances, numTokens));
				else
					bestMoves[i][j] += Math.pow(distances, numTokens);
			}
		}
		return path;
	}

	public boolean beTheFirst(Node[][] state, int row, int column, int direction) {
		int[] move = inverseMoves[direction];
		boolean flag = true;

		for (int i = 1; i < 5 && flag; i++) {
			int r = row + (move[0] * i);
			int c = column + (move[1] * i);
			if (verify(r, c) && state[r][c].getValue() > 0)
				flag = false;
			else if (verify(r, c) && state[r][c].getValue() < 0) {
				state[row][column].setBlocked(true);
				break;
			}
		}
		return flag;
	}

	public boolean verify(int r, int c) {
		boolean flag = false;
		if (0 <= r && r < size && 0 <= c && c < size)
			flag = true;
		return flag;

	}

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
//Verified
class TState {
	private int[][] state;
	private int[] actualMove;

	public TState(int[][] state, int[] actualMove) {
		this.state = state;
		this.actualMove = actualMove;
	}

	

	public int[][] getState() {
		return state;
	}

	public int getValue(int i, int j) {
		return state[i][j];
	}

	public int[] getActualMove() {
		return actualMove;
	}

	public int[][] makeNewState(int[] move) {
		int[][] newState = new int[state.length][state.length];
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state.length; j++) {
				newState[i][j] = state[i][j] * (-1);
			}
		}
		newState[move[0]][move[1]] = -1;
		return newState;
	}

	public boolean hasWinner() {
        int x = actualMove[0];
        int y = actualMove[1];
		if (x==-1 || y==-1) return false;
        int player = state[x][y];
        if (isExactlyFive(x, y, 0, 1, player)) 
            return true;
        if (isExactlyFive(x, y, 1, 0, player)) 
            return true;
        if (isExactlyFive(x, y, 1, 1, player)) 
            return true;
        if (isExactlyFive(x, y, 1, -1, player)) 
            return true;
        return false;
    }

    // Verificar si hay exactamente cinco consecutivos en la dirección dada
    private boolean isExactlyFive(int x, int y, int dx, int dy, int player) {
        int totalCount = countConsecutive(x, y, dx, dy, player) + countConsecutive(x, y, -dx, -dy, player) - 1;
        return totalCount == 5;
    }

    // Método auxiliar para contar consecutivos en una dirección dada
    private int countConsecutive(int x, int y, int dx, int dy, int player) {
        int count = 0;
        while (x >= 0 && x < state.length && y >= 0 && y < state[0].length && state[x][y] == player) {
            count++;
            x += dx;
            y += dy;
        }
        return count;
    }
}

class Node {

	private int value;
	private boolean visited;
	private boolean[] visitedDirections;
	private int lastToken;
	private int row;
	private int column;
	private boolean blocked = false;

	public Node(int value, int row, int column) {
		this.value = value;
		this.visited = false;
		this.visitedDirections = new boolean[] { false, false, false, false, false, false, false, false };
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
		for (boolean x : visitedDirections) {
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