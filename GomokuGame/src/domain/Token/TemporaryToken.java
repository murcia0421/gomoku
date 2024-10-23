	package domain.Token;
	
	import java.awt.Color;

import domain.AlertPlay;
import domain.Gomoku;
import domain.GomokuException;
	
	/**
	 * The TemporaryToken class represents a special type of token in a Gomoku game
	 * that disappears from the board after a certain number of turns.
	 * It extends the Token class and overrides the 'act()' method to implement its unique behavior.
	 * The token is attached to a player, and its existence is limited to a specified number of turns.
	 *
	 * @author Juan Daniel Murcia - Mateo Forero Fuentes
	 * @version 2.0
	 */
	public class TemporaryToken extends Token {
	
	    private Integer creationTurn;
	
	    /**
	     * Constructs a TemporaryToken with the specified color, row, and column.
	     *
	     * @param color  The color of the token.
	     * @param row    The row index of the token on the game board.
	     * @param column The column index of the token on the game board.
	     */
	    public TemporaryToken(Color color, int row, int column) {
	        super(color, row, column);
	    }
	
	    /**
	     * Performs the action associated with the temporary token.
	     * If the creation turn is not set, it sets it to the current turn.
	     * Otherwise, it checks whether the token should disappear after a certain number of turns and detaches it.
	     * @throws GomokuException 
	     */
	    @Override
	    public void act() throws GomokuException {
	        if (creationTurn == null) {
	        	player.increaseScore(100);
	            creationTurn = Gomoku.getGomoku().getTurn();
	        } else {
	            if (creationTurn + 3 == Gomoku.getGomoku().getTurn()) {
	                if (square!=null && square.getToken()==this) {
	                	AlertPlay.dettach(this);
	                	square.setToken(null);
	                }
	                	
	            }
	        }
	    }
	    
	    /**
	     * Validates whether the overlap with a specified token is valid.
	     * Overrides the valid method from the Token class.
	     * 
	     * @param token The token to check for overlap.
	     * @return True if the overlap is valid (null token), otherwise throws a GomokuException.
	     * @throws GomokuException If an invalid move overlap occurs.
	     */
	    public boolean valid(Token token) throws GomokuException {
			if (token == null) {
				return true;
			}
	        // Throws an exception indicating an invalid move overlap
			throw new GomokuException(GomokuException.INVALID_MOVE_OVERLAP);
		}
	}
