package domain;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.SubmissionPublisher;

import domain.Game.Game;
import domain.Player.Player;
import domain.Square.Square;
import domain.Token.Token;

/**
 * The Gomoku class represents a game of Gomoku, a two-player strategy board game.
 * It manages the game state, player information, and provides access to various
 * aspects of the ongoing Gomoku game.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class Gomoku extends SubmissionPublisher<Gomoku> implements Runnable, Serializable{

	private Game game;
	boolean ok = true;
	public boolean finish;
	
	private static Gomoku gomokuSingleton = null;
	
	/**
	 * Retrieves the singleton instance of the Gomoku class.
	 *
	 * @return The singleton instance of the Gomoku class.
	 */
	public static Gomoku getGomoku() {
		return gomokuSingleton;
	}
	/**
	 * Constructs a Gomoku object based on the specified game type, and size.
	 *
	 * @param gameType           The type of the game, represented as a String.
	 * @param size               The size of the game, indicating the dimensions.
	 * @throws ClassNotFoundException    If the specified game type class is not found.
	 * @throws NoSuchMethodException     If a matching constructor is not found in the specified class.
	 * @throws SecurityException         If a security violation occurs during reflection.
	 * @throws InstantiationException    If an instance of the class cannot be created (abstract class or interface).
	 * @throws IllegalAccessException    If the constructor is not accessible due to access modifiers.
	 * @throws IllegalArgumentException  If the provided arguments are not valid for the constructor.
	 * @throws InvocationTargetException If an exception occurs while invoking the constructor.
	 * @throws GomokuException           If the given size is less than 10
	 */
	public Gomoku(String gameType, int size)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, GomokuException {
		if (size < 10) throw new GomokuException(GomokuException.INVALID_GAME_SIZE);
		String type = "domain.Game." + gameType + "Game";
		Class<?> clazz = Class.forName(type);
		Constructor<?> constructor = clazz.getConstructor(int.class);
		Object gameInstance = constructor.newInstance(size);
		game = (Game) gameInstance;
		AlertPlay.dettachAll();	
		finish = false;
		gomokuSingleton = this;
	}

	/**
	 * Sets the players for the Gomoku game based on the specified player types.
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
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		game.setPlayers(typePlayer1, typePlayer2);
	}

	/**
	 * Sets the information for players in the Gomoku game, including names and colors.
	 *
	 * @param player1 The name of player 1.
	 * @param color1  The color associated with player 1.
	 * @param player2 The name of player 2.
	 * @param color2  The color associated with player 2.
	 */
	public void setPlayersInfo(String player1, Color color1, String player2, Color color2) {
		game.setPlayersInfo(player1, color1, player2, color2);
	}
	
	/**
	 * Sets the especial info in the Gomoku game, including the especial percentage for tokens or squares.
	 *
	 * @param especialPercentageTokens the percentage of especial tokens for the players
	 * @param especialPercentageSquares the percentage of especial squares for the board
	 */
	public void setEspecialInfo(int especialPercentageTokens, int especialPercentageSquares) {
		game.setEspecialInfo(especialPercentageTokens, especialPercentageSquares);
	}
	
	/**
	 * Sets the limits of time an tokens that will have the players
	 * 
	 * @param numTokens The number of tokens for the game
	 */
	public void setLimits(int numTokens,int timeLimit) {
		game.setNumTokens(numTokens);
		game.setTimeLimit(timeLimit);
	}
	@Override
	public void run(){
		while (null == game.getWinner()&& !finish) {
			if(ok) {
				int[] info = game.play();
				if (info != null) {
					try {
						game.play(info[0], info[1]);
						ok=true;
						if(!finish)
							this.submit(this);
					} catch (GomokuException e) {
						e.printStackTrace();
					}
				}
				else {
					ok = false;
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Plays a move in the Gomoku game by placing a token at the specified row and column.
	 *
	 * @param token  The player's token to be placed on the game board.
	 * @param row    The row where the player wants to place the token.
	 * @param column The column where the player wants to place the token.
	 * @throws GomokuException 
	 */
	public void play(int row, int column) throws GomokuException {
		game.play(row, column);
		ok = true;
	}
	
	public void setPlayerToken(String token) {
		game.setPlayerToken(token);
	}
	
	public String getToken() {
		return game.getToken();
	}
	/**
	 * Retrieves the size of the Gomoku game board.
	 *
	 * @return The size of the game board.
	 */
	public int getSize() {
		return game.getSize();
	}

	/**
	 * Retrieves the underlying game instance.
	 *
	 * @return The instance of the underlying game.
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Retrieves the color of the token at the specified row and column on the game board.
	 *
	 * @param row    The row coordinate of the token.
	 * @param column The column coordinate of the token.
	 * @return The color of the token.
	 */
	public Color getTokenColor(int row, int column) {
		return game.getTokenColor(row, column);
	}

	/**
	 * Retrieves the winner of the Gomoku game.
	 *
	 * @return The name of the winner, or null if there is no winner yet.
	 */
	public String getWinner() {
		return game.getWinner();
	}

	/**
	 * Retrieves a map containing player names and their corresponding token counts.
	 *
	 * @return A map of player names to token counts.
	 */
	public HashMap<String, Integer> getPlayerTokens() {
		return game.getPlayerTokens();
	}

	/**
	 * Retrieves the information of player one.
	 *
	 * @return The Player object representing player one.
	 */
	public Player getPlayerOne() {
		return game.getPlayerOne();
	}

	/**
	 * Retrieves the information of player two.
	 *
	 * @return The Player object representing player two.
	 */
	public Player getPlayerTwo() {
		return game.getPlayerTwo();
	}
	/**
	 * Retrieves the current turn number in the Gomoku game.
	 *
	 * @return The current turn number.
	 */
	public int getTurn() {
		return game.getTurn();
	}
	

	/**
	 * Retrieves the set of token subtypes used in the Gomoku game.
	 *
	 * @return A set containing the classes of token subtypes.
	 */
	public Set<Class<? extends Token>> getTokenSubtypes() {
		return game.getTokenSubtypes();
	}

	public Square getSquare(int i, int j) {
		return game.getSquare(i,j);
	}
	
	public Time getTime() {
		return game.getTime();
	}
	
	public void finish() {
		if(this!=null) {
			this.close();
			finish = true;
			game.finish();
		}
		
	}
	/**
	* Saves the current game state to a specified file.
	*
	* @param nombreArchivo The name of the file to save the game state.
	*/
	public void guardarPartida(String nombreArchivo) {
	    ObjectOutputStream salida = null;
	    try {
	        salida = new ObjectOutputStream(new FileOutputStream(nombreArchivo));
	        salida.writeObject(this);
	        salida.flush(); 
	        System.out.println("Partida guardada con éxito. Ruta: " + new File(nombreArchivo).getAbsolutePath());
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (salida != null) {
	            try {
	                salida.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	/**
 	* Loads a game state from a specified file and returns a Vintage object.
 	*
 	* @param nombreArchivo The name of the file to load the game state.
 	* @return A Vintage object representing the loaded game state.
	*/
	public static Gomoku cargarPartida(String nombreArchivo) {
		
		Gomoku partidaCargada = null;
	    try {
	    	try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
				partidaCargada = (Gomoku) entrada.readObject();
			}

	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    } 
	    if(getGomoku()!=partidaCargada)getGomoku().finish();
	    gomokuSingleton = partidaCargada;
	    return partidaCargada;
	}
	public String getToken(int i, int j) {
		return game.getToken(i, j);
	}

}