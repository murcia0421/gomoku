package domain;

/*
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class GomokuException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String INVALID_GAME_SIZE = "The game should have a size upper than 10.";
	public static final String INVALID_MOVE_OVERLAP = "You cant put this token because there is already a token.";
	public static final String INVALID_OVERLAP_SAME = "You cant put a overlap token in some of your tokens.";
	public static final String INVALID_TOKEN_TO_PLAY = "By the golden square you have to play 2 normal tokens this turn";
	public static final String INVALID_OVERLAP = "You cant put a overlap token in a overlap token.";
	public static final String INVALID_MOVE_POSITION = "Invalid move position.";
	public static final String INVALID_MOVE_NO_TOKENS = "You cant make this move beacose you dont have the token";
	
	
	public GomokuException(String message) {
		super(message);
	}
}
