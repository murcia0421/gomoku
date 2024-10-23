package domain.Game;

import java.util.concurrent.Flow.Subscription;

import domain.Time;

/**
 * The NormalGame class represents a specific implementation of the Game interface for a normal Gomoku game.
 * In a normal game, players have a set quantity of normal tokens, with a specified percentage of special tokens.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class NormalGame extends Game {


	/**
     * Constructs a NormalGame object with the specified size and percentage of special elements.
     *
     * @param size               The size of the game board, indicating the dimensions.
     * @param especialPercentage The percentage of special elements in the game.
     */
	public NormalGame(int size) {
		super(size);
	}

	
	/**
     * Initializes and starts a normal Gomoku game by distributing normal and special tokens to players.
     * The number of normal and special tokens is determined based on the size of the game board
     * and the specified percentage of special elements.
     */
	public void start() {
    	numTokens = size * size;
		super.start();
	}
	
	/**
     * Overrides the setTimeLimit method to set the game's time limit to an infinite value for normal games.
     * 
     * @param timeLimit The time limit for the game (ignored in normal games).
     */
	public void setTimeLimit(int timeLimit) {
		this.time = new Time(-1,this);
	}
	
	 /**
     * Overrides the onSubscribe method to set up the subscription and request the first item.
     * 
     * @param subscription The subscription for the game.
     */
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}
	
	/**
     * Overrides the onNext method to request more items after receiving one.
     * 
     * @param item The item received.
     */
	@Override
	public void onNext(Integer item) {
        subscription.request(1); // Solicitar más elementos después de recibir uno
	}
	
	/**
     * Overrides the onError method to handle errors during the game.
     * 
     * @param throwable The Throwable object representing the error.
     */
	@Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }
	
	/**
     * Overrides the onComplete method to indicate that the publication has finished.
     */
    @Override
    public void onComplete() {
        System.out.println("La publicación ha finalizado.");
    }
}
