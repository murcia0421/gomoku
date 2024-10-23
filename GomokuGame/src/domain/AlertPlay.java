package domain;

import java.io.Serializable;
import java.util.ArrayList;

import domain.Token.PlayToken;

/**
 * The AlertPlay class represents a mechanism for managing and notifying observers (PlayToken instances)
 * when a specific alert condition is met.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class AlertPlay  implements Serializable{

    // Flag to indicate if the observer list is currently being iterated
	private static boolean iterating = false;
	
    // List to store the registered observers
	private static ArrayList<PlayToken> observers =  new ArrayList<>();
	
	/**
     * Attaches a PlayToken observer to the list.
     *
     * @param observer The PlayToken observer to be attached.
     */
	public static void  attach(PlayToken observer) {
		observers.add(observer);
	}
	
	
	/**
     * Detaches a PlayToken observer from the list. If the list is currently being iterated,
     * the observer is marked for removal and will be removed after the iteration is complete.
     *
     * @param observer The PlayToken observer to be detached.
     */
	public static void  dettach(PlayToken observer) {
		int index = observers.indexOf(observer);
		observers.remove(index);
		if(iterating) {
			observers.add(index,null);
		}
		
	}
	
	
	/**
     * Notifies all registered observers by calling their 'act' method. During the iteration,
     * observers marked for removal are skipped. After the iteration, the marked observers are removed.
	 * @throws GomokuException 
     */
	public static void notifyObservers() throws GomokuException {
		iterating = true;
		for (int i = 0; i < observers.size(); i++) {
			if (observers.get(i) != null) {
				observers.get(i).act();
			}
		}
		iterating = false;
		toRemove();
	}
	
	
	/**
     * Removes observers marked for removal from the list.
     */
	public static void toRemove() {
		while(observers.contains(null)) {
			observers.remove(null);			
		}

	}

	
	 /**
     * Detaches all observers from the list.
     */
	public static void dettachAll() {
		observers = new ArrayList<>();
	}
}