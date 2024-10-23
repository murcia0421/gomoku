package domain;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import domain.Game.Game;
import domain.Square.Square;
import domain.Token.Token;


/*
 * Represents the game board for a Gomoku (Five in a Row) game.
 * The board consists of squares, and players place tokens on the squares.
 * The class handles token placement, checking for winning conditions, and board configuration.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class Board  implements Serializable{

	private int size;
	private Square[][] boardSquares;
	private Token lastToken;
	private Game game;

	/**
	 * Constructs a Board object with the specified size and percentage of special
	 * squares.
	 *
	 * @param size                      The size of the board, indicating the
	 *                                  dimensions.
	 * @param percentageEspecialSquares The percentage of special squares on the
	 *                                  board.
	 */
	public Board(int size) {
		this.size = size;
		boardSquares = new Square[size][size];
		boolean[][] visited = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				visited[i][j] = false;
				boardSquares[i][j] = Square.createSquareInstance(this, i, j, false);
			}
		}
	}

	/**
	 * Sets the percentage of special squares on the board based on the specified
	 * special percentage. Special squares are randomly placed on the board while
	 * considering the provided percentage.
	 *
	 * @param especialPercentageSquares The percentage of special squares to be
	 *                                  placed on the board.
	 */
	public void setEspecialPercentageSquares(int especialPercentageSquares) {
		// Calculate the number of special squares based on the size and percentage.
		int numEspecialSquares = ((size * size) * especialPercentageSquares) / 100;

		// Initialize a boolean array to keep track of visited squares during special
		// square placement.
		boolean[][] visited = new boolean[size][size];

		// Randomly place special squares on the board.
		Random random = new Random();
		while (numEspecialSquares > 0) {
			int i = random.nextInt(0, size);
			int j = random.nextInt(0, size);

			// Check if the square at (i, j) has not been visited.
			if (!visited[i][j]) {
				// Set the square at (i, j) as a special square.
				boardSquares[i][j] = Square.createSquareInstance(this, i, j, true);

				// Mark the square as visited.
				visited[i][j] = true;

				// Decrement the count of remaining special squares.
				numEspecialSquares--;
			}
		}
	}


	/**
	 * Plays the specified token at the given row and column on the board.
	 *
	 * @param token  The token to be played on the board.
	 * @param row    The row where the token will be played.
	 * @param column The column where the token will be played.
	 * @throws GomokuException 
	 */
	public void playToken(Token token, int row, int column) throws GomokuException {
		lastToken = token;
		boardSquares[row][column].playToken(token);
	}

	
	public void setToken(Token token, int row, int column) throws GomokuException {
		boardSquares[row][column].setToken(token);
	}

	
	public Color getTokenColor(int row, int column) {
		if (verify(row, column)) {
			return boardSquares[row][column].getTokenColor();
		} else {
			return null;
		}
	}

	
	public int getSize() {
		return size;
	}

	/**
	 * Validates whether the move at the specified row and column results in a
	 * winning condition. Checks for diagonal, vertical, and horizontal winning
	 * moves.
	 *
	 * @param row    The row coordinate of the last move.
	 * @param column The column coordinate of the last move.
	 * @return True if the move results in a winning condition, false otherwise.
	 */
	public boolean validate(int row, int column) {
		boolean winner = false;
		if (verify(row, column)) {
			winner = diagonalMove(row, column) || diagonalIMove(row, column) || verticalMove(row, column)
					|| horizontalMove(row, column);
		}
		return winner;
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
	 * Checks for a winning condition along the diagonal line (from top-left to
	 * bottom-right) originating from the specified row and column.
	 *
	 * @param row    The row coordinate of the last move.
	 * @param column The column coordinate of the last move.
	 * @return True if a winning condition is met, false otherwise.
	 */
	private boolean diagonalMove(int row, int column) {
		boolean winner = false;
		Color moveColor = boardSquares[row][column].getTokenColor();
		if (moveColor != null) {
			int t = 0;
			int up = 0;
			while (verify(row - up - 1, column - up - 1)
					&& boardSquares[row - up - 1][column - up - 1].getTokenColor() != null
					&& boardSquares[row - up - 1][column - up - 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row - up - 1][column - up - 1].getTokenValue();
				up++;
			}
			int dw = 0;
			while (verify(row + dw + 1, column + dw + 1)
					&& boardSquares[row + dw + 1][column + dw + 1].getTokenColor() != null
					&& boardSquares[row + dw + 1][column + dw + 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row + dw + 1][column + dw + 1].getTokenValue();
				dw++;
			}
			if (t + boardSquares[row][column].getTokenValue() == 5) {
				System.out.println("Diagonal at: " + row + " " + column + "..." + up + " " + dw + "... color:"
						+ boardSquares[row][column].getTokenColor());
				winner = true;
			}
		}

		return winner;
	}

	/**
	 * Checks for a winning condition along the diagonal line (from top-right to
	 * bottom-left) originating from the specified row and column.
	 *
	 * @param row    The row coordinate of the last move.
	 * @param column The column coordinate of the last move.
	 * @return True if a winning condition is met, false otherwise.
	 */
	private boolean diagonalIMove(int row, int column) {
		boolean winner = false;
		Color moveColor = boardSquares[row][column].getTokenColor();
		if (moveColor != null) {
			int t = 0;
			int up = 0;
			while (verify(row - up - 1, column + up + 1)
					&& boardSquares[row - up - 1][column + up + 1].getTokenColor() != null
					&& boardSquares[row - up - 1][column + up + 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row - up - 1][column + up + 1].getTokenValue();
				up++;
			}
			int dw = 0;
			while (verify(row + dw + 1, column - dw - 1)
					&& boardSquares[row + dw + 1][column - dw - 1].getTokenColor() != null
					&& boardSquares[row + dw + 1][column - dw - 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row + dw + 1][column - dw - 1].getTokenValue();
				dw++;
			}
			if (t + boardSquares[row][column].getTokenValue() == 5) {
				System.out.println("Diagonal at: " + row + " " + column + "..." + up + " " + dw + "... color:"
						+ boardSquares[row][column].getTokenColor());
				winner = true;
			}
		}
		return winner;
	}

	/**
	 * Checks for a winning condition along the horizontal line originating from the
	 * specified row and column.
	 *
	 * @param row    The row coordinate of the last move.
	 * @param column The column coordinate of the last move.
	 * @return True if a winning condition is met, false otherwise.
	 */
	private boolean horizontalMove(int row, int column) {
		boolean winner = false;
		Color moveColor = boardSquares[row][column].getTokenColor();
		if (moveColor != null) {
			int t = 0;
			int lf = 0;
			while (verify(row, column - lf - 1) && boardSquares[row][column - lf - 1].getTokenColor() != null
					&& boardSquares[row][column - lf - 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row][column - lf - 1].getTokenValue();
				lf++;
			}

			int rg = 0;
			while (verify(row, column + rg + 1) && boardSquares[row][column + rg + 1].getTokenColor() != null
					&& boardSquares[row][column + rg + 1].getTokenColor().equals(moveColor)) {
				t += boardSquares[row][column + rg + 1].getTokenValue();
				rg++;
			}
			if (t + boardSquares[row][column].getTokenValue() == 5) {
				System.out.println("Horizontal at: " + row + " " + column + "... " + lf + " " + rg + "... color:"
						+ boardSquares[row][column].getTokenColor());
				winner = true;
			}
		}

		return winner;
	}

	/**
	 * Checks for a winning condition along the vertical line originating from the
	 * specified row and column.
	 *
	 * @param row    The row coordinate of the last move.
	 * @param column The column coordinate of the last move.
	 * @return True if a winning condition is met, false otherwise.
	 */
	private boolean verticalMove(int row, int column) {
		boolean winner = false;
		Color moveColor = boardSquares[row][column].getTokenColor();
		if (moveColor != null) {
			int up = 0;
			int t = 0;
			while (verify(row - up - 1, column) && boardSquares[row - up - 1][column].getTokenColor() != null
					&& boardSquares[row - up - 1][column].getTokenColor().equals(moveColor)) {
				t += boardSquares[row - up - 1][column].getTokenValue();
				up++;
			}
			int dw = 0;
			while (verify(row + dw + 1, column) && boardSquares[row + dw + 1][column].getTokenColor() != null
					&& boardSquares[row + dw + 1][column].getTokenColor().equals(moveColor)) {
				t += boardSquares[row + dw + 1][column].getTokenValue();
				dw++;
			}
			if (t + boardSquares[row][column].getTokenValue() == 5) {
				System.out.println("Vertical at: " + row + " " + column + "... " + up + " " + dw + "... color:"
						+ boardSquares[row][column].getTokenColor());
				winner = true;
			}
		}
		return winner;
	}

	
	public Square getSquare(int i, int j) {
		return boardSquares[i][j];
	}

	/**
	 * Increases the turn count in the associated Game instance.
	 */
	public void increaseTurn() {
		game.increaseTurn();
	}

	/**
	 * Decreases the turn count in the associated Game instance.
	 */
	public void decreaseTurn() {
		game.decreaseTurn();
	}

	/**
	 * Increases the quantity of a specific player's tokens in the associated Game
	 * instance.
	 *
	 * @param name The name of the player.
	 * @param i    The quantity to increase.
	 */
	public void increasePlayerQuantity(String name, int i) {
		game.increasePlayerQuantity(name, i);
	}

	
	public int getTurn() {
		return game.getTurn();
	}
	
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Token getLastToken() {
		return lastToken;
	}

	public void addToken(String name) {
		game.addToken(name);
	}

	public Token getToken(int i, int j) {
		return boardSquares[i][j].getToken();
	}
	
	/**
	 * Retrieves the positions of opponent tokens on the board with the specified opponent color.
	 * Iterates through the board's squares, identifies those with the opponent's token color,
	 * and records their positions.
	 *
	 * @param opponentColor The color of the opponent's tokens to search for.
	 * @return An ArrayList of int arrays representing the positions (row, column) of opponent tokens.
	 */
	public ArrayList<int[]> getOpponentTokenPositions(Color opponentColor) {
        ArrayList<int[]> opponentTokenPositions = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (boardSquares[row][column].getTokenColor() != null &&
                        boardSquares[row][column].getTokenColor().equals(opponentColor)) {
                    opponentTokenPositions.add(new int[]{row, column});
                }
            }
        }

        return opponentTokenPositions;
    }

}
