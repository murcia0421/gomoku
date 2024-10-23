package domain.Square;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;

/**
 * The NormalSquare class represents a normal square on the game board. It extends the Square class
 * and is associated with the standard behavior of a normal square in a Gomoku game.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class NormalSquare extends Square {

    /**
     * Constructs a NormalSquare object with the specified board, row, and column coordinates.
     *
     * @param board  The board to which the square belongs.
     * @param row    The row coordinate of the square.
     * @param column The column coordinate of the square.
     */
    public NormalSquare(Board board, int row, int column) {
        super(board, row, column);
    }

    /**
     * Performs the action associated with a normal square. Detaches the square from alert observers,
     * sets its token, and increases the turn count on the board.
     * 
     * @throws GomokuException 
     */
    public void act() throws GomokuException {
    	AlertPlay.dettach(this);
		setToken(token);
		
		board.increaseTurn();
    }
}
