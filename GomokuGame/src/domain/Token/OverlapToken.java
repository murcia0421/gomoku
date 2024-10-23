package domain.Token;

import java.awt.Color;
import java.util.Random;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;
import domain.Square.Square;

/**
 * The OverlapToken class represents a token with overlapping behavior in the Gomoku game.
 * It extends the Token class and is associated with special actions when overlapped with other tokens.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class OverlapToken extends Token {
	private Token otherToken;
	
	/**
     * Constructs an OverlapToken object with the specified color, row, and column coordinates.
     *
     * @param color  The color of the token.
     * @param row    The row coordinate of the token.
     * @param column The column coordinate of the token.
     */
	public OverlapToken(Color color, int row, int column) {
		super(color, row, column);
		
	}

	/**
     * Performs the action associated with an overlapping token. Detaches the token from alert observers,
     * increases the player's score, and performs special actions based on the type of the overlapping token.
     * 
     * @throws GomokuException If an exception occurs during the execution of the overlapping action.
     */
	public void act() throws GomokuException {
		AlertPlay.dettach(this);
		player.increaseScore(100);
		if (otherToken == null) {
			
		}
		else if(otherToken instanceof HeavyToken) {
            // Overlapping with a TemporaryToken: Move the TemporaryToken to a random empty square
			
			int r = row; int c = column;
			Square squareOther = otherToken.getSquare();
			player.deleteToken(r, c);
			Token other = new NormalToken(otherToken.getColor(),r,c);
			other.setPlayer(otherToken.getPlayer());
			squareOther.setToken(other);
			otherToken.getPlayer().setWinner(r,c);
		}
		else if(otherToken instanceof TemporaryToken) {
			Random random = new Random();
			Board board = square.getBoard();
	        int i = random.nextInt(0, board.getSize());
	        int j = random.nextInt(0, board.getSize());

	        // Find a random empty square on the board
	        while (board.getTokenColor(i, j) != null) {
	            i = random.nextInt(0, board.getSize());
	            j = random.nextInt(0, board.getSize());
	        }
	        board.playToken(otherToken, i, j);
	        board.decreaseTurn();
		}
		else {
            // Overlapping with other tokens: Increase player's score
			player.increaseScore(100);
		}
		
	}
	
	
	/**
     * Validates whether placing the given token is a valid move on the board, considering overlap rules.
     *
     * @param token The token to be validated.
     * @return True if the move is valid, false otherwise.
     * @throws GomokuException If an exception occurs during the validation process.
     */
	public boolean valid(Token token) throws GomokuException {
		if(token == null || !token.getColor().equals(this.color)) {
			if (token instanceof OverlapToken)
				throw new GomokuException(GomokuException.INVALID_OVERLAP);
			otherToken = token;
			return true;
		}
		else {
			throw new GomokuException(GomokuException.INVALID_OVERLAP_SAME);
		}
	}

}
