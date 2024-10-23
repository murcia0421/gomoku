package domain.Square;

import java.awt.Color;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;

/**
 * The MineSquare class represents a specialized type of Square that, when activated,
 * removes tokens from the adjacent squares and increases the turn count on the game board.
 * It extends the Square class and overrides the act() method to perform this specific action.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class MineSquare extends Square {

	 /**
     * Constructs a MineSquare with the specified board, row, and column.
     *
     * @param board  The game board to which the MineSquare belongs.
     * @param row    The row position of the MineSquare on the game board.
     * @param column The column position of the MineSquare on the game board.
     */
	public MineSquare(Board board,int row, int column) {
		super(board,row,column);
	}

	
	/**
     * Overrides the act() method of the Square class. Detaches the MineSquare from the observer list,
     * sets its token, and removes tokens from the adjacent squares. Finally, increases the turn count on the game board.
	 * @throws GomokuException 
     */
	public void act() throws GomokuException {
		AlertPlay.dettach(this);
		setToken(token);
		for(int i = row - 1; i <= row + 1;i++) {
			for(int j = column - 1; j <= column + 1;j++) {
				Color c = board.getTokenColor(i, j);
				if (c != null) {
					if (c.equals(token.getColor())) {
						token.getPlayer().increaseScore(-50);
					}
					else {
						token.getPlayer().increaseScore(100);
					}
				}
			}
		}
		for(int i = row - 1; i <= row + 1;i++) {
			for(int j = column - 1; j <= column + 1;j++) {
				if(board.verify(i,j))
					board.setToken(null, i, j);
			}
		}
		board.increaseTurn();
	}
}
