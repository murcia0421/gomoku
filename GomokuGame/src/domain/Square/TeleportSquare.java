package domain.Square;

import java.util.Random;

import domain.AlertPlay;
import domain.Board;
import domain.GomokuException;
import domain.Token.Token;

/**
 * The TeleportSquare class represents a square on the game board that, when activated,
 * teleport the token on it to a randomly selected empty square on the same board.
 * It extends the Square class.
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class TeleportSquare extends Square {

    /**
     * Constructs a TeleportSquare with the specified board, row, and column.
     *
     * @param board  The game board to which the TeleportSquare belongs.
     * @param row    The row index of the TeleportSquare on the board.
     * @param column The column index of the TeleportSquare on the board.
     */
    public TeleportSquare(Board board, int row, int column) {
        super(board, row, column);
    }

    /**
     * Performs the teleportation action when the TeleportSquare is activated.
     * Detaches the square from the alert play, then randomly selects an empty square
     * on the board and moves the token from the current square to the selected square.
     * @throws GomokuException 
     */
    public void act() throws GomokuException {
        AlertPlay.dettach(this);
        Random random = new Random();
        int i = random.nextInt(0, board.getSize());
        int j = random.nextInt(0, board.getSize());

        // Find a random empty square on the board
        while (board.getTokenColor(i, j) != null) {
            i = random.nextInt(0, board.getSize());
            j = random.nextInt(0, board.getSize());
        }

        // Perform the teleportation by moving the token to the selected empty square
        Token actualToken = token;
        token = null;
        board.playToken(actualToken, i, j);
    }
}
