package domain.Token;



import java.awt.Color;

import domain.AlertPlay;
import domain.GomokuException;


/**
 * The HeavyToken class represents a specialized type of Token with additional behavior.
 * It extends the Token class and overrides the act() method to perform a specific action
 * when triggered by an observer.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class HeavyToken extends Token {

	
	/**
     * Constructs a HeavyToken with the specified color, row, and column.
     *
     * @param color  The color of the HeavyToken.
     * @param row    The row position of the HeavyToken on the game board.
     * @param column The column position of the HeavyToken on the game board.
     */
	public HeavyToken(Color color, int row, int column) {
		super(color, row, column);
		this.value=2;
		
	}

	
	/**
     * Overrides the act() method of the Token class. Detaches itself from the observer list,
     * shuffles the order of possible moves, and attempts to expand by playing a NormalToken in
     * one of the shuffled directions. If successful, updates the player's token count and decreases the turn count.
     */

	public void act() {
		AlertPlay.dettach(this);
		player.increaseScore(100);	
	}
	
	/**
	 * Validates if the specified token placement is valid on the game board.
	 * If the provided token is null, the placement is considered valid.
	 * Otherwise, an exception is thrown indicating that token overlap is not allowed.
	 *
	 * @param token The Token object to be validated for placement.
	 * @return true if the placement is valid (null token), false otherwise.
	 * @throws GomokuException If a non-null token is provided, indicating an invalid token overlap.
	 */
	public boolean valid(Token token) throws GomokuException {
		if (token == null) {
			return true;
		}
		throw new GomokuException(GomokuException.INVALID_MOVE_OVERLAP);
	}
}
