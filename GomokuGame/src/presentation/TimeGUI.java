package presentation;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import javax.swing.JLabel;
import javax.swing.JPanel;

import domain.Gomoku;


/**
 * The GomokuState class represents the graphical user interface for displaying and interacting with the Gomoku game state.
 * It implements the Subscriber interface to receive updates from the Gomoku game logic.
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class TimeGUI extends JPanel implements Subscriber<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	private double timePlayerOne;
	private double timePlayerTwo;
	private Subscription subscription;
	private JLabel c1;
	private JLabel c2;
	
	
	/**
     * Constructs a TimeGUI to display the remaining time for each player.
     */
	public TimeGUI() {
		Gomoku.getGomoku().getTime().subscribe(this);
		setLayout(new GridLayout(0,2,50,0));
		c1 = new JLabel("Time player one: "+ timePlayerOne);
		c2 = new JLabel("Time player two: "+ timePlayerTwo);
		add(c1);
		add(c2);
	}
	
	
	/**
     * Paints the component and updates the displayed remaining time.
     *
     * @param g The Graphics object to paint on.
     */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		c1.setText("Time player one: "+ (timePlayerOne/1000));
		c2.setText("Time player two: "+ (timePlayerTwo/1000));
	}
	
	/**
     * Implementation of the onSubscribe method of the Subscriber interface.
     *
     * @param subscription The subscription for receiving updates.
     */
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
		repaint();
	}
	
	
	/**
     * Implementation of the onNext method of the Subscriber interface.
     *
     * @param item The remaining time received as an update.
     */
	@Override
	public void onNext(Integer item) {
		subscription.request(1); 
		if (Gomoku.getGomoku().getTurn()%2 == 0) {
			timePlayerOne = item;
		}
		else {
			timePlayerTwo = item;
		}
		repaint();
	}
	
	
	/**
     * Implementation of the onError method of the Subscriber interface.
     *
     * @param throwable The error received.
     */
	@Override
	public void onError(Throwable throwable) {
		
	}
	
	
	/**
     * Implementation of the onComplete method of the Subscriber interface.
     */
	@Override
	public void onComplete() {
	}

}
