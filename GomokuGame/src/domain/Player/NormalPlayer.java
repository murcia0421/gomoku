package domain.Player;

/**
 * The NormalPlayer class represents a player in a normal Gomoku game.
 * In a normal game, players have a set quantity of normal tokens and no predefined strategy for playing.
 * This class extends the Player class and provides a basic implementation of the play() method,
 * which returns null, indicating that the player does not have a precalculated move.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class NormalPlayer extends Player {

	/**
     * Overrides the play() method to return null, indicating that the player does not have a precalculated move.
     * 
     * @return Null, as NormalPlayer does not have a predefined move.
     */
	public int[] play() {
		return null;
	}

	
}
