package domain.Game;

import java.util.concurrent.Flow.Subscription;

import domain.Time;

/**
 * The LimitedGame class represents a specialized type of Game with limited token quantities.
 * It extends the Game class and overrides the start() method to initialize the game with a specific
 * distribution of normal and special tokens between two players.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class LimitedGame extends Game {
	/*
     * Constructs a LimitedGame with the specified size.
     *
     * @param size The size of the game board.
     */
	public LimitedGame(int size) {
		super(size);
	}
	
	/**
     * Sets the time limit for the game to be infinite, ignoring any time constraints.
     *
     * @param timeLimit The time limit for the game (ignored in LimitedGame).
     */
	public void setTimeLimit(int timeLimit) {
		this.time = new Time(-1,this);
	}
	
	/**
     * Implementation of the onSubscribe method from the Subscriber interface.
     * Sets the subscription for the LimitedGame and requests the delivery of the initial item.
     *
     * @param subscription The Subscription object representing the communication channel with the publisher.
     */
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}
	
	/**
     * Implementation of the onNext method from the Subscriber interface.
     * Requests more elements after receiving one, allowing the asynchronous processing of data streams.
     *
     * @param item The Integer item received from the data stream.
     */
	@Override
	public void onNext(Integer item) {
        subscription.request(1); // Solicitar más elementos después de recibir uno
	}
	
	/**
     * Implementation of the onError method from the Subscriber interface.
     * Prints an error message when an error occurs in the data stream.
     *
     * @param throwable The Throwable object representing the encountered error.
     */
	@Override
    public void onError(Throwable throwable) {
        System.err.println("Error: " + throwable.getMessage());
    }
	
	/**
     * Implementation of the onComplete method from the Subscriber interface.
     * Prints a message indicating that the data stream has completed.
     */
    @Override
    public void onComplete() {
        System.out.println("La publicación ha finalizado.");
    }
}
