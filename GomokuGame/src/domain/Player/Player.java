package domain.Player;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.reflections.Reflections;

import domain.GomokuException;
import domain.Game.Game;
import domain.Token.Token;

/**
  * The abstract class Player represents a participant in the Gomoku game.
 * It provides the basic structure for player behavior and token management.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public abstract class Player implements Serializable{
    private String name;
    protected Color color;
    protected HashMap<String, Integer> quantities;
    protected Game game;
    protected ArrayDeque<String> tokens;
    private int score;
    private int maxScore=1;

    // Static variable to store player subtypes
    public static Set<Class<? extends Player>> subTypes = null;

    /**
     * Retrieves the set of player subtypes using reflection.
     *
     * @return The set of player subtypes.
     */
    public static Set<Class<? extends Player>> getPlayerSubtypes() {
        if (subTypes == null) {
            Reflections reflections = new Reflections("domain.Player");
            Set<Class<? extends Player>> subTypesGet = reflections.getSubTypesOf(Player.class);
            subTypes = subTypesGet;
        }
        return subTypes;
    }

    /**
     * Constructor for the Player class.
     */
    public Player() {
        quantities = new HashMap<>();
        tokens =  new ArrayDeque<>();
    }

    /**
     * Sets the name and color information for the player.
     *
     * @param playerName The name of the player.
     * @param color      The color associated with the player.
     */
    public void setInfo(String playerName, Color color) {
        name = playerName;
        this.color = color;
    }

    /**
     * Sets the quantity of a specific type of token for the player.
     *
     * @param tokenName The name of the token type.
     * @param quantity  The quantity to set.
     */
    public void setQuantityTypeOfToken(String tokenName, int quantity) {
        quantities.put(tokenName, quantity);
    }

    /**
     * Increases the quantity of a specific type of token for the player.
     *
     * @param tokenName The name of the token type.
     * @param increase  The quantity to increase.
     */
    public void increaseQuantityToken(String tokenName, int increase) {
        quantities.put(tokenName, quantities.get(tokenName) + increase);
    }
	
	    /**
	     * Retrieves the name of the player.
	     *
	     * @return The name of the player.
	     */
	    public String getName() {
	        return name;
	    }

    /**
     * Sets the associated game for the player.
     *
     * @param game The game to associate with the player.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Retrieves the map of token quantities associated with the player.
     *
     * @return The map of token quantities.
     */
    public HashMap<String, Integer> getMap() {
        return quantities;
    }

    /**
     * Initiates the player's move with the specified type of token at the given row and column.
     *
     * @param typeToken The type of token to play.
     * @param row       The row to place the token.
     * @param column    The column to place the token.
     * @throws GomokuException 
     */
    public void play(int row, int column) throws GomokuException {
    	
    	String type = tokens.getFirst();
    	tokens.removeFirst();
    	try {
    		if(quantities.get(type) <= 0) throw new GomokuException(GomokuException.INVALID_MOVE_NO_TOKENS); 
    		Class<?> clazz = Class.forName("domain.Token."+type);
    		Constructor<?> constructor = clazz.getConstructor(Color.class, int.class, int.class);
    		Object tokenInstance = constructor.newInstance(color, row, column);
    		Token actualToken = (Token) tokenInstance;
    		actualToken.setPlayer(this);
    		game.playToken(actualToken, row, column);
    		quantities.put(type, quantities.get(type) - 1);
    		
    		game.setWinner(actualToken.getRow(), actualToken.getColumn());
    		
    		addToken();
    	}
    	catch(ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
    		tokens.addFirst(type);
    		e.printStackTrace();
    	}
    	catch(GomokuException e) {
    		tokens.addFirst(type);
    		throw new GomokuException(e.getMessage());
    	}
    }
    
    /**
     * Retrieves the type of the token that the player will play.
     *
     * @return The type of the token.
     */
    public String getToken() {
    	String token = tokens.getFirst();
    	return token;
    }
    
    /**
     * Adds a specific token type to the player's token queue.
     *
     * @param name The name of the token type to add.
     */
    public void addToken(String name ) {
    	String typeToken = name;
		tokens.addFirst(typeToken);
    }
    
    /**
     * Adds a random token type to the player's token queue based on the available token quantities.
     */
    public void addToken() {
    	if(tokens.isEmpty()) {
    		String typeToken = null;
    		Set<String> keys = quantities.keySet();
    		int sum = 0;
    		for(String className : keys) {
            	sum += quantities.get(className);
        	}
        	Random random = new Random();      	
        	int rn = random.nextInt(sum);
        	int i = 0;
        	for(String className : keys) {
            	if (i + quantities.get(className) > rn) {
            		typeToken = className;
            		break;
            	}
            	i += quantities.get(className);
        	}
        	tokens.addLast(typeToken);
    	}	
    }

    /**
     * Deletes the token at the specified row and column.
     *
     * @param row    The row coordinate of the token.
     * @param column The column coordinate of the token.
     * @throws GomokuException 
     */
    public void deleteToken(int row, int column) throws GomokuException {
        game.setToken(null, row, column);
    }

    /**
     * Retrieves the set of token subtypes associated with the player.
     *
     * @return The set of token subtypes.
     */
    public Set<Class<? extends Token>> getTokenSubtypes() {
        return Token.getTokenSubtypes();
    }

    /**
     * Abstract method to be implemented by player subclasses for playing the move.
     *
     * @return An ArrayList containing player move information.
     */
    public abstract int[] play();

    /**
     * Increases the turn count in the game.
     */
    public void increaseTurn() {
        game.increaseTurn();
    }

    /**
     * Decreases the turn count in the game.
     */
   
	public void decreaseTurn() {
		game.decreaseTurn();
	}
	
	/**
     * Retrieves the current score of the player.
     *
     * @return The player's score.
     */
	public int getScore() {
		return score;
	}
	
	/**
     * Increases the player's score by the specified amount and adjusts the maximum score threshold.
     *
     * @param amount The amount to increase the player's score.
     */
	public void increaseScore(int i) {
		score += i;
		if (score > 1000*maxScore) {
			 Set<Class<? extends Token>> tokens = Token.getTokenSubtypes();
	         Random r = new Random();
	         int rn = r.nextInt(tokens.size());
	         int j = 0;
	         for (Class<? extends Token> token : tokens) {
	        	 String name = token.getSimpleName();
	        	 if (j == rn) {
	        		 increaseQuantityToken(name, 1);
	        		 break;
	        	 }
	             j++;
	         }
	         maxScore ++;
		}
	}
	
	/**
     * Retrieves the color associated with the player.
     *
     * @return The player's color.
     */
	public Color getColor() {
		return color;
	}
	
	/**
     * Retrieves the game associated with the player.
     *
     * @return The associated game.
     */
	public Game getGame() {
		return game;
	}
	
	/**
     * Sets the player as the winner at the specified row and column.
     *
     * @param row    The row coordinate of the winning move.
     * @param column The column coordinate of the winning move.
     */
	public void setWinner(int r, int c) {
		game.setWinner(r, c);
	}
}
