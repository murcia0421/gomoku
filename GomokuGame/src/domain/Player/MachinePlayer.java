package domain.Player;

/*
 * The MachinePlayer class represents an abstract machine player in the Gomoku game.
 * It extends the Player class and provides a framework for AI-based player implementations.
 * Subclasses must extend this class and implement the play() and miniMax() methods
 * to define the behavior of the machine player.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public abstract class MachinePlayer extends Player {
    /**
     * The time delay (in milliseconds) used to introduce a delay between moves of the machine player.
     * Default value is set to 1000 milliseconds (1 second).
     */
    protected int timeRetard = 0;

    /**
     * Abstract method representing the main play strategy for the machine player.
     * Subclasses must implement this method to define how the machine player makes a move in the game.
     *
     * @return An array of integers representing the coordinates (row, column) of the selected move.
     */
    public abstract int[] play();

    /**
     * Abstract method representing the mini-max algorithm used for decision-making in the game.
     * Subclasses must implement this method to define the mini-max algorithm's logic for the specific game.
     *
     * @return An array of integers representing the coordinates (row, column) of the best move calculated by the mini-max algorithm.
     */
    public abstract int[] miniMax();
}
