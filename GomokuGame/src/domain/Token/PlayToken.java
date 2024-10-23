package domain.Token;

import domain.GomokuException;

/**
 * Interface representing the action to be performed when playing a token in the game.
 *
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public interface PlayToken{

    /**
     * Performs the action associated with playing a token in the game.
     * @throws GomokuException 
     */
    public void act() throws GomokuException;
}
