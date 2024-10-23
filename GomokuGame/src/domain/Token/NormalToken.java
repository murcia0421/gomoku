package domain.Token;

import java.awt.Color;

import domain.AlertPlay;
import domain.GomokuException;

/**
 * The NormalToken class represents a normal token used in the Gomoku game. It extends the Token class
 * and is associated with the standard behavior of a normal token.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */

public class NormalToken extends Token {

	/**
     * Constructs a NormalToken object with the specified color, row, and column coordinates.
     *
     * @param color  The color of the token.
     * @param row    The row coordinate of the token.
     * @param column The column coordinate of the token.
     */
    public NormalToken(Color color, int row, int column) {
        super(color, row, column);
    }

    /**
     * Performs the action associated with a normal token. Detaches the token from alert observers.
     */
    public void act() {
        // Detach from alert observers
        AlertPlay.dettach(this);
    }
    
    /**
     * Validates whether placing the given token is a valid move on the board.
     *
     * @param token The token to be validated.
     * @return True if the move is valid, false otherwise.
     * @throws GomokuException If an exception occurs during the validation process.
     */
	public boolean valid(Token token) throws GomokuException {
		if (token == null) {
			return true;
		}
		throw new GomokuException(GomokuException.INVALID_MOVE_OVERLAP);
	}
    
}
