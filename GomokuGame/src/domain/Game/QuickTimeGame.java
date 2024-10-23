package domain.Game;

import java.util.concurrent.Flow.Subscription;

/**
 * The QuickTimeGame class represents a specific implementation of the Game interface for a Gomoku game
 * with a quick time limit for each player's turn.
 *
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class QuickTimeGame extends Game {
    /**
     * Constructs a QuickTimeGame object with the specified size and initializes the game with a quick time limit.
     *
     * @param size The size of the game board, indicating the dimensions.
     */
    public QuickTimeGame(int size) {
        super(size);
    }

    /**
     * Initializes and starts a QuickTime Gomoku game by distributing normal and special tokens to players
     * and setting up the quick time limit for each player's turn.
     */
    public void start() {
        numTokens = size * size;
        super.start();
    }
    
    /**
     * Handles the subscription, processing, and completion of a LimitedGame instance using the reactive programming model.
     * Overrides methods from the Flow.Subscriber interface.
     * 
     * @param subscription The subscription object representing the communication link between the publisher and subscriber.
     * @param item         The next item received during the game.
     */
    @Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}
    
    /**
     * Processes the next item received during the game. If the item is negative, determines the winner based on the current turn.
     * 
     * @param item The next item received during the game.
     */
	@Override
	public void onNext(Integer item) {
		if (item < 0) {
			if (turn % 2 == 0) {
				winner = playerTwo.getName();
			}
			else {
				winner = playerOne.getName();
			}
		}
        subscription.request(1); // Request more items after receiving one
	}
	
	/**
	 * Handles errors that may occur during the game.
	 * 
	 * @param throwable The throwable representing the error that occurred.
	 */
	@Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }
	
	/**
	 * Signals that the subscription has completed, indicating the end of the game.
	 */
    @Override
    public void onComplete() {
        System.out.println("La publicación ha finalizado."); // The publication has finished
    }
}
