package domain.Square;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Set;

import org.reflections.Reflections;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;
import domain.Token.PlayToken;
import domain.Token.Token;

/**
 * The Square class represents a square on the game board in a Gomoku game.
 * It serves as the base class for different types of squares, such as normal squares and special squares.
 * Each square can contain a token, and specific actions are performed when playing tokens on squares.
 * Subclasses must implement the PlayToken interface and provide their own behavior for the 'act()' method.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public abstract class Square implements PlayToken, Serializable {

    protected Token token;
    public static Set<Class<? extends Square>> subTypes = null;
    protected Board board;
    protected int row;
    protected int column;

    /**
     * Gets the set of all subclasses of the Square class using reflection.
     *
     * @return The set of all subclasses of Square.
     */
    public static Set<Class<? extends Square>> getSquareSubtypes() {
        if (subTypes == null) {
            Reflections reflections = new Reflections("domain.Square");
            Set<Class<? extends Square>> subTypesGet = reflections.getSubTypesOf(Square.class);
            subTypes = subTypesGet;
        }
        return subTypes;
    }

    /**
     * Constructs a Square with the specified board, row, and column.
     *
     * @param board  The game board to which the square belongs.
     * @param row    The row index of the square.
     * @param column The column index of the square.
     */
    public Square(Board board, int row, int column) {
        this.board = board;
        this.row = row;
        this.column = column;
        this.token = null;
    }

    /**
     * Creates an instance of a Square based on the specified criteria.
     * If the square is not special, it creates a NormalSquare; otherwise, it randomly selects a special square type.
     *
     * @param board    The game board to which the square belongs.
     * @param row      The row index of the square.
     * @param column   The column index of the square.
     * @param especial A flag indicating whether the square is special.
     * @return The created Square instance.
     */
    public static Square createSquareInstance(Board board, int row, int column, boolean especial) {
        Square square = null;
        if (!especial) {
            square = new NormalSquare(board, row, column);
        } else {
        	Set<Class<? extends Square>> subtypes = Square.getSquareSubtypes();
            Random random = new Random();
            int range = random.nextInt(0, subtypes.size() - 1);
            int i = 0;
            for (Class<? extends Square> sq : subtypes) {
                if (!sq.getSimpleName().equals("NormalSquare")) {
                    if (i == range) {
                        try {
                            Constructor<?> cons = sq.getConstructor(Board.class, int.class, int.class);
                            Object obj = cons.newInstance(board, row, column);
                            square = (Square) obj;
                        } catch (NoSuchMethodException | SecurityException | InstantiationException |
                                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }
            }
        }
        return square;
    }

    /**
     * Sets the token on the square. If the token is not null, it attaches the square to the token and notifies observers.
     *
     * @param token The token to set on the square.
     * @throws GomokuException 
     */
    public void setToken(Token token) throws GomokuException {
        if (token != null) {
            this.token = token;
            token.setPosition(row, column);
            token.setSquare(this);
            AlertPlay.attach(token);
            AlertPlay.notifyObservers();
        } else {
            if (this.token != null) {
            	this.token.setSquare(null);
                this.token.setPosition(-1, -1);
            }
            this.token = token;
        }
    }

    /**
     * Plays the specified token on the square and attaches the square to the token, notifying observers.
     *
     * @param token The token to play on the square.
     * @throws GomokuException 
     */
    public void playToken(Token token) throws GomokuException {
    	if(token.valid(this.token)) {
    		this.token = token;
            AlertPlay.attach(this);
            AlertPlay.notifyObservers();
    	}
        
    }

    /**
     * Gets the color of the token on the square.
     *
     * @return The color of the token, or null if no token is present.
     */
    public Color getTokenColor() {
        Color color = null;
        if (token != null) {
            color = token.getColor();
        }
        return color;
    }

    /**
     * Gets the value of the token on the square.
     *
     * @return The value of the token, or 0 if no token is present.
     */
    public int getTokenValue() {
        if (token != null) {
            return token.getValue();
        }
        return 0;
    }

    /**
     * Performs the action associated with playing a token on the square.
     * Subclasses must provide their own implementation of this method.
     * @throws GomokuException 
     */
    @Override
    public abstract void act() throws GomokuException;

	public Board getBoard() {
		return board;
	}

	public Token getToken() {
		return token;
	}
}
