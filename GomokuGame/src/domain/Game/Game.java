package domain.Game;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Flow.*;

import org.reflections.Reflections;

import domain.Board;
import domain.GomokuException;
import domain.Time;
import domain.Player.Player;
import domain.Square.Square;
import domain.Token.Token;

/**
 * The abstract Game class defines the common structure and behavior for Gomoku games.
 * Subclasses must extend this class and implement the abstract methods to create specific games.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public abstract class Game implements Subscriber<Integer>,Serializable{


	private Board board;
	protected int size;
	protected int numTokens;
	protected  transient Subscription subscription;
	protected Time time;
	protected transient Thread t;
	protected int especialPercentageTokens;
	protected Player playerOne;
	protected Player playerTwo;
	protected String winner;
	protected int turn;
	public static Set<Class<? extends Game>> subTypes = null;
	
	/**
     * Retrieves a set of all classes that extends the Game class within the "domain" package.
     *
     * @return A set of Class objects representing the game subtypes.
     */
	public static Set<Class<? extends Game>> getGameSubtypes() {
		if (subTypes == null) {
			Reflections reflections = new Reflections("domain.Game");
	        Set<Class<? extends Game>> subTypesGet = reflections.getSubTypesOf(Game.class);
	        subTypes = subTypesGet;
		}
        return subTypes;
    }


	/**
	 * Constructs a Game object with the specified size and percentage of special elements.
	 *
	 * @param size               The size of the game board, indicating the dimensions.
	 * @param especialPercentage The percentage of special elements in the game.
	 */
	public Game(int size) {
		this.size = size;
		this.board = new Board(size);
		this.board.setGame(this);
		turn = 0;
		winner = null;
	}
	
	/**
	 * Sets the players for the game based on the specified player types.
	 *
	 * @param typePlayer1 The type of player 1, represented as a String.
	 * @param typePlayer2 The type of player 2, represented as a String.
	 * @throws ClassNotFoundException    If the specified player type classes are not found.
	 * @throws InstantiationException    If an instance of the player type cannot be created (abstract class or interface).
	 * @throws IllegalAccessException    If the player type's constructor is not accessible due to access modifiers.
	 * @throws IllegalArgumentException  If the provided arguments are not valid for the player type's constructor.
	 * @throws InvocationTargetException If an exception occurs while invoking the player type's constructor.
	 * @throws NoSuchMethodException     If a matching method is not found in the specified player type class.
	 * @throws SecurityException         If a security violation occurs during reflection.
	 */
	public void setPlayers(String typePlayer1, String typePlayer2)
	        throws ClassNotFoundException, InstantiationException, IllegalAccessException,
	        IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
	    String type = "domain.Player." + typePlayer1;
	    Class<?> clazz = Class.forName(type);
	    Constructor<?> constructor = clazz.getConstructor();
	    Object playerInstance = constructor.newInstance();
	    playerOne = (Player) playerInstance;

	    type = "domain.Player." + typePlayer2;
	    clazz = Class.forName(type);
	    constructor = clazz.getConstructor();
	    playerInstance = constructor.newInstance();
	    playerTwo = (Player) playerInstance;
	    
	    playerOne.setGame(this);
	    playerTwo.setGame(this);
	    
	}
	
	/**
	 * Sets the information for players in the game, including names and colors.
	 *
	 * @param nameOne  The name of player one.
	 * @param color1   The color associated with player one.
	 * @param nameTwo  The name of player two.
	 * @param color2   The color associated with player two.
	 */
	public void setPlayersInfo(String nameOne, Color color1, String nameTwo, Color color2) {
		playerOne.setInfo(nameOne, color1);
		playerTwo.setInfo(nameTwo, color2);
	}
	
	/**
	 * Sets the number of tokens that will have the players
	 * 
	 * @param numTokens The number of tokens for the game
	 */
	public void setNumTokens(int numTokens) {
		this.numTokens = numTokens;
	}
	
	/**
	 * Sets the limit time that will have the players
	 * 
	 * @param timeLimit the limit in seconds of the players -1 for inifinite time
	 * 
	 */
	public void setTimeLimit(int timeLimit) {
		this.time = new Time(timeLimit,this);
	}
	
	/**
	 * Sets the special information for the game, including the percentage of special tokens and squares.
	 * It initializes the special squares on the board, sets the percentage of special tokens,
	 * and starts the game.
	 *
	 * @param especialPercentageTokens  The percentage of special tokens to be used in the game.
	 * @param especialPercentageSquares The percentage of special squares on the board.
	 */
	public void setEspecialInfo(int especialPercentageTokens, int especialPercentageSquares) {
		board.setEspecialPercentageSquares(especialPercentageSquares);
		this.especialPercentageTokens = especialPercentageTokens;
		start();
	}
	
	/**
	 * Initiates a move in the game by instructing the current player (based on the turn count) to play.
	 * If the current player is an AI player, the returned ArrayList contains a string representing the type of token,
	 * and two integers indicating the row and column where the AI player chose to play. If the current player is a human player,
	 * the method returns null, indicating that no precalculated move is available.
	 *
	 * @return An ArrayList containing AI player move information or null for human players.
	 */
	public int[] play() {
		int[] info;
		if ((turn % 2) == 0) {	
			info = playerOne.play();
		} else {
			info = playerTwo.play();
		}
		return info;
	}
	
	/**
	 * Plays a move in the game by placing a token at the specified row and column for the current player.
	 * The player's turn alternates between Player One and Player Two.
	 *
	 * @param token  The player's token to be placed on the game board.
	 * @param row    The row where the player wants to place the token.
	 * @param column The column where the player wants to place the token.
	 * @throws GomokuException 
	 */
	public void play(int row, int column) throws GomokuException {

		if (board.verify(row, column)) {
			if ((turn % 2) == 0) {	
				playerOne.play(row, column);
			} else {
				playerTwo.play(row, column);
			}
		}
		else {
			throw new GomokuException(GomokuException.INVALID_MOVE_POSITION);
		}
	}
	
	public String getToken() {
		String token;
		if ((turn % 2) == 0) {
			token = playerOne.getToken();
		} else {
			token = playerTwo.getToken();
		}
		return token;
	}

	/**
	 * Plays the specified token at the given row and column on the game board.
	 *
	 * @param token  The token to be placed on the game board.
	 * @param row    The row where the token will be placed.
	 * @param column The column where the token will be placed.
	 * @throws GomokuException 
	 */
	public void playToken(Token token,int row, int column) throws GomokuException {
		
		board.playToken(token,row,column);
		
	}
	
	/**
	 * Sets the specified token at the given row and column on the game board.
	 *
	 * @param token  The token to be placed on the game board.
	 * @param row    The row where the token will be placed.
	 * @param column The column where the token will be placed.
	 * @throws GomokuException 
	 */
	public void setToken(Token token,int row, int column) throws GomokuException {
		board.setToken(token,row,column);
	}
	
	/**
	 * Retrieves the game board associated with the current game instance.
	 *
	 * @return The Board object representing the game board.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Retrieves the current turn number in the game.
	 *
	 * @return The current turn number.
	 */
	public int getTurn() {
		return turn;
	}

	
	/**
	 * Retrieves the size of the game board.
	 *
	 * @return The size of the game board.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Retrieves the name of the player who won the game.
	 *
	 * @return The name of the winner or null if there is no winner yet.
	 */ 
	public String getWinner() {
		return winner;
	}
	
	
	/**
	 * Sets the winner of the game based on the last move's coordinates.
	 *
	 * @param row    The row of the last move.
	 * @param column The column of the last move.
	 */
	public void setWinner(int row,int column) {
		String player = null;
		if ((turn % 2) != 0) 
			player = playerOne.getName();
		else 
			player = playerTwo.getName();
		if(board.validate(row, column))
			winner = player;
	}

	/**
	 * Retrieves information about player one in the game.
	 *
	 * @return The Player object representing player one.
	 */
	public Player getPlayerOne() {
		return playerOne;
	}

	/**
	 * Retrieves information about player two in the game.
	 *
	 * @return The Player object representing player two.
	 */
	public Player getPlayerTwo() {
		return playerTwo;
	}

	/**
	 * Retrieves the color of the token at the specified row and column on the game board.
	 *
	 * @param row    The row coordinate of the token.
	 * @param column The column coordinate of the token.
	 * @return The color of the token.
	 */
	public Color getTokenColor(int row, int column) {
		return board.getTokenColor(row, column);
	}

	/**
	 * Retrieves the map of player tokens for the current player's turn.
	 *
	 * @return A HashMap containing player names and their corresponding token counts.
	 */
	public HashMap<String, Integer> getPlayerTokens() {
		HashMap<String, Integer> res;
		if ((turn % 2) != 0) {
			res = playerOne.getMap();
		} else {
			res = playerTwo.getMap();
		}
		return res;
	}

	/**
	 * Retrieves the set of token subtypes used by player one in the game.
	 *
	 * @return A set containing the classes of token subtypes used by player one.
	 */
	public Set<Class<? extends Token>> getTokenSubtypes() {
		return playerOne.getTokenSubtypes();
	}
	
	/**
	 * Initializes the game by distributing normal and special tokens to players based on the provided percentages.
	 * Special tokens are distributed randomly among available types.
	 */
	protected void start() {
	    int numSpecials = (numTokens) * especialPercentageTokens / 100;
	    int num = numTokens - numSpecials;
	    playerOne.setQuantityTypeOfToken("NormalToken", num);
	    playerTwo.setQuantityTypeOfToken("NormalToken", num);
	    Random random = new Random();
	    String lastName = null;
	    int lastSum = 0;
	    num = 0;

	    // Distribute special tokens to players
	    for (Class<? extends Token> typeOfToken : Token.getTokenSubtypes()) {
	        String tokenName = typeOfToken.getSimpleName();
	        if (!tokenName.equals("NormalToken") && numSpecials != 0)
	            num = random.nextInt(numSpecials);

	        // Set the quantity of each token type for both players
	        if (!tokenName.equals("NormalToken")) {
	            playerOne.setQuantityTypeOfToken(tokenName, num);
	            playerTwo.setQuantityTypeOfToken(tokenName, num);
	            numSpecials -= num;
	            lastName = tokenName;
	            lastSum = num;
	        }
	    }
	    if (numSpecials != 0) {
	        playerOne.setQuantityTypeOfToken(lastName, numSpecials + lastSum);
	        playerTwo.setQuantityTypeOfToken(lastName, numSpecials + lastSum);
	    }
	    playerOne.addToken();
	    playerTwo.addToken();
	    t = new Thread(time);
	    t.start();
	}


	/**
	 * Retrieves the Square object at the specified row and column coordinates on the board.
	 *
	 * @param i The row coordinate of the Square.
	 * @param j The column coordinate of the Square.
	 * @return The Square object at the specified coordinates.
	 */
	public Square getSquare(int i, int j) {
	    return board.getSquare(i, j);
	}


	/**
	 * Increases the turn count in the game.
	 */
	public void increaseTurn() {
	    turn += 1;
	}

	/**
	 * Decreases the turn count in the game.
	 */
	public void decreaseTurn() {
	    turn -= 1;
	}


	/**
	 * Increases the quantity of a specific player's tokens based on the turn count.
	 *
	 * @param name The name of the player.
	 * @param i    The quantity to increase.
	 */
	public void increasePlayerQuantity(String name, int i) {
	    if (turn % 2 == 0) {
	        playerOne.increaseQuantityToken(name, i);
	    } else {
	        playerTwo.increaseQuantityToken(name, i);
	    }
	}


	/**
	 * Adds a token with the specified name to the current player's inventory based on the turn count.
	 * If the turn count is even, the token is added to Player One's inventory; otherwise, it is added to Player Two's inventory.
	 *
	 * @param name The name of the token to be added.
	 */
	public void addToken(String name) {
		if (turn % 2 == 0) {
	        playerOne.addToken(name);
	    } else {
	        playerTwo.addToken(name);
	    }
		
	}


	/**
	 * Sets a token with the specified name as the current player's token based on the turn count.
	 * If the turn count is even, the token is set as Player One's current token; otherwise, it is set as Player Two's current token.
	 *
	 * @param token The name of the token to be set as the current player's token.
	 */
	public void setPlayerToken(String token) {
		if (turn%2==0) {
			playerOne.addToken(token);
		}
		else {
			playerTwo.addToken(token);
		}
	}

	
	/**
	 * Sets the winner of the game based on the provided player's name.
	 *
	 * @param name The name of the player who won the game.
	 */
	public void setWinner(String name) {
		this.winner = name;
	}

	
	/**
	 * Retrieves the color of the opponent player based on the turn count.
	 * If the turn count is even, returns the color of Player Two; otherwise, returns the color of Player One.
	 *
	 * @return The Color object representing the color of the opponent player.
	 */
	public Color getOpponentColor() {
		if(turn%2 == 0)
			return playerTwo.getColor();
		else
			return playerOne.getColor();
	}
	
	/**
	 * Retrieves the Time object associated with the game.
	 *
	 * @return The Time object representing the time-related functionality of the game.
	 */
	public Time getTime() {
		return time;
	}
	
	
	/**
	 * Verifies if the specified coordinates (i, j) are within the bounds of the game board.
	 *
	 * @param i The row coordinate.
	 * @param j The column coordinate.
	 * @return true if the coordinates are valid, false otherwise.
	 */
	public boolean verify(int i, int j) {
		return board.verify(i, j);
	}


	/**
	 * Retrieves the value of the token at the specified coordinates (i, j) on the game board.
	 *
	 * @param i The row coordinate of the token.
	 * @param j The column coordinate of the token.
	 * @return The integer value representing the token.
	 */
	public int getTokenValue(int i, int j) {
		return board.getToken(i, j).getValue();
	}


	public void finish() {
		time.finish();
	}


	public String getToken(int i, int j) {
		return board.getToken(i, j).getClass().getSimpleName();
	}

}
