package domain;

import java.io.Serializable;
import java.util.concurrent.SubmissionPublisher;

import domain.Game.Game;


/**
 * Represents a timer for controlling the time limit in a Gomoku game.
 * Extends SubmissionPublisher to publish time remaining during the game.
 * Implements the Runnable interface to run the timer in a separate thread.
 * 
 * The timer runs independently for each player and publishes the remaining time at one-second intervals.
 * When the time limit is reached, it publishes the remaining time as a negative value to signal the end of the game.
 * 
 * @see SubmissionPublisher
 * @see Runnable
 * @see Game
 * @see GomokuException
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class Time extends SubmissionPublisher<Integer> implements Runnable,Serializable{

	private int limitMili;          // Time limit in milliseconds
	private Game game;              // Reference to the associated game
	private int deltaMili;          // Time interval for updating the timer
	private int timePlayerOneMili;  // Remaining time for player one in milliseconds
	private int timePlayerTwoMili;  // Remaining time for player two in milliseconds
	private boolean finish;
	 
	 /**
	* Constructs a Time object with the specified time limit and associated game.
	* The timer is initialized based on the provided time limit, and the associated game is subscribed to the timer updates.
	*
	* @param limit The time limit in seconds. If set to -1, the timer runs indefinitely.
	* @param game  The associated game to which the timer will publish updates.
	*/
	public Time(int limit,Game game) {
		this.limitMili = limit*1000;
		this.game = game;
		this.subscribe(game);
		
        // Set up the timer based on the time limit
		if(this.limitMili == -1000) {
			this.limitMili = 0;
			deltaMili = 100;
		}
		else{
			deltaMili = -100;
		}
		
        // Initialize remaining time for both playersç
		timePlayerOneMili = this.limitMili;
		timePlayerTwoMili = this.limitMili;
		finish = false;
	}
	
	/**
     * Runs the timer in a separate thread, updating the remaining time for each player until the game ends.
     * Publishes updates at one-second intervals.
     */
	@Override
	public void run() {
		while(game.getWinner() == null || !finish) {
            // Player one's turn
			if(game.getTurn()%2 == 0) {
				while (game.getWinner() == null && game.getTurn()%2 == 0 && timePlayerOneMili >= 0) {
					try {
						Thread.sleep(Math.abs(deltaMili));
						timePlayerOneMili += deltaMili;
						
                        // Publish time update at one-second intervals
						if (timePlayerOneMili%1000 == 0 && !finish) {
							this.submit(timePlayerOneMili);
						}
					} catch (InterruptedException e) {
					}
				}
			}
			
            // Player two's turn
			else {
				while(game.getWinner() == null && game.getTurn()%2 != 0 && timePlayerTwoMili >= 0) {
					try {
						Thread.sleep(Math.abs(deltaMili));
						timePlayerTwoMili += deltaMili;
						
                        // Publish time update at one-second intervals
						if (timePlayerTwoMili%1000 == 0) {
							this.submit(timePlayerTwoMili);
						}
					} catch (InterruptedException e) {
					}
				}
			}
			
            // Publish remaining time if it goes below zero
			if(timePlayerOneMili < 0) {
				this.submit(timePlayerOneMili);
			}
			if(timePlayerTwoMili < 0) {
				this.submit(timePlayerTwoMili);
			}
		}
	}
	
	public void finish() {
		this.close();
		finish = true;
	}

}
