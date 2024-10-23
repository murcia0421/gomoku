package domain.Square;

import java.util.Random;
import java.util.Set;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;
import domain.Token.NormalToken;
import domain.Token.Token;

/**
 * The GoldenSquare class represents a square on the game board that, when activated,
 * has a special effect on the game. It implements the PlayToken interface.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class GoldenSquare extends Square{

    private Integer creationTurn;
    private boolean entered;

    /**
     * Constructs a GoldenSquare with the specified board, row, and column.
     *
     * @param board  The game board to which the GoldenSquare belongs.
     * @param row    The row index of the GoldenSquare on the board.
     * @param column The column index of the GoldenSquare on the board.
     */
    public GoldenSquare(Board board, int row, int column) {
        super(board, row, column);
        entered = false;
    }

    /**
     * Performs the special action associated with the GoldenSquare when activated.
     * If it's the first activation, it sets the token and increases the quantity of a randomly selected
     * special token for the current player. If it's the second activation (two turns later), it detaches
     * the square from the alert play and decreases the turn count.
     * @throws GomokuException 
     */
    public void act() throws GomokuException {
        if (creationTurn == null) {
            creationTurn = board.getTurn();
            Set<Class<? extends Token>> tokens = Token.getTokenSubtypes();
            Random r = new Random();
            int rn = r.nextInt(tokens.size());
            int i = 0;
            for (Class<? extends Token> token : tokens) {
                String name = token.getSimpleName();
                if (i == rn) {
                    if (!name.equals("NormalToken")) {
                        AlertPlay.dettach(this);
                        creationTurn = null;
                    }
                    else {
                    	board.increasePlayerQuantity(name, 1);
                    	board.addToken(name);
                    	board.addToken(name);
                    }
                    board.increasePlayerQuantity(name, 1);
                    break;
                }
                i++;
            }
            setToken(token);
            board.increaseTurn();
        } else {
            if (creationTurn + 2 == board.getTurn()) {
            	Token last = board.getLastToken();
            	if (!( last instanceof NormalToken)) {
            		board.setToken(null,last.getRow(),last.getColumn());
            		throw new GomokuException(GomokuException.INVALID_TOKEN_TO_PLAY);
            	}
            	else if (( last instanceof NormalToken) && !entered) {
            		board.decreaseTurn();
                	entered = true;
                }
            	else if ((last instanceof NormalToken) && entered) {
                	AlertPlay.dettach(this);
                	creationTurn = null;
                }              
            }
        }
        
    }
}
