package domain.Token;

import java.awt.Color;
import java.io.Serializable;
import java.util.Set;
import org.reflections.Reflections;

import domain.GomokuException;
import domain.Player.Player;
import domain.Square.Square;

/**
 * The Token class is an abstract class representing a generic game token in a Gomoku game.
 * It serves as the base class for various token types and provides common attributes and methods.
 * Subclasses of Token define specific behaviors for different types of tokens.
 *
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public abstract class Token implements PlayToken, Serializable {

    protected Color color;
    protected int row;
    protected int column;
    protected Player player;
    protected int value;
    protected Square square;
    public static Set<Class<? extends Token>> subTypes = null;

    /**
     * Retrieves the set of token subclasses through reflection.
     *
     * @return A set of Class objects representing subclasses of Token.
     */
    public static Set<Class<? extends Token>> getTokenSubtypes() {
        if (subTypes == null) {
            Reflections reflections = new Reflections("domain.Token");
            Set<Class<? extends Token>> subTypesGet = reflections.getSubTypesOf(Token.class);
            subTypes = subTypesGet;
        }
        return subTypes;
    }

    /**
     * Constructs a Token with the specified color, row, and column.
     *
     * @param color  The color of the token.
     * @param row    The row index of the token on the game board.
     * @param column The column index of the token on the game board.
     */
    public Token(Color color, int row, int column) {
        this.color = color;
        this.row = row;
        this.column = column;
        value = 1;
    }

    /**
     * Retrieves the color of the token.
     *
     * @return The color of the token.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the player associated with the token.
     *
     * @param player The player associated with the token.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public void setSquare(Square square) {
        this.square = square;
    }

    /**
     * Sets the position (row and column) of the token on the game board.
     *
     * @param row    The row index of the token.
     * @param column The column index of the token.
     */
    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Retrieves the row index of the token on the game board.
     *
     * @return The row index of the token.
     */
    public int getRow() {
        return row;
    }

    /**
     * Retrieves the column index of the token on the game board.
     *
     * @return The column index of the token.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Retrieves the value associated with the token.
     *
     * @return The value of the token.
     */
    public int getValue() {
        return value;
    }
    
    public Player getPlayer() {
    	return player;
    }
    
    public abstract boolean valid(Token token) throws GomokuException;

	public Square getSquare() {
		return square;
	}
}
