package presentation;

import domain.*;
import domain.Player.Player;
import domain.Token.Token;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;


/**
 * The GomokuGUI class represents the graphical user interface for the Gomoku game.
 * It extends JFrame and implements the Subscriber interface to receive updates from the Gomoku game.
 * The GUI provides menus for starting, saving, and opening games, as well as displaying the game board, player information, and game controls.
 * 
 * @see JFrame
 * @see Subscriber
 * @see Gomoku
 * @see GomokuState
 * @see TimeGUI
 * @see Player
 * @see Token
 * @see GameConfig
 * 
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class GomokuGUI extends JFrame implements Subscriber<Gomoku> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = (3 * screenSize.width) / 4;
	public static final int HIGH = (3 * screenSize.height) / 4;
	private static final Dimension PREFERRED_DIMENSION = new Dimension(WIDTH, HIGH);
	private Gomoku gomoku;
	private Subscription subscription;

	// Menu
	private JMenuItem menuNew;
	private JMenuItem menuOpen;
	private JMenuItem menuSave;
	private JMenuItem menuClose;

	// Start
	public JPanel start;
	private JButton startButton;

	// Game

	private GomokuState state;
	private String move;

	// PlayerOne
	private JPanel player1;
	private JButton[] tokensPlayer1;
	JPanel tokens1;

	// PlayerTwo
	private JPanel player2;
	private JButton[] tokensPlayer2;
	JPanel tokens2;
	private JPanel time;

	private Clip backgroundMusic;
	private JMenuItem menuFinish;
	
	private JComboBox<String> gameToken;
	private JButton gameButtonToken;
	/**
     * Constructs a GomokuGUI object, initializing its components and preparing the actions.
     */
	private GomokuGUI() {
		prepareElements();
		prepareActions();
		initializeBackgroundMusic();
		playBackgroundMusic();
	}
	
	/**
     * Initializes the background music for the GUI.
     */
	private void initializeBackgroundMusic() {
		try {
			File audioFile = new File("Resources/Music1.wav");
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			backgroundMusic = AudioSystem.getClip();
			backgroundMusic.open(audioStream);
		} catch (Exception e) {
			Log.record(e);
			e.printStackTrace();
		}
	}
	
	/**
     * Main method to create and display the GomokuGUI.
     *
     * @param args Command-line arguments.
     */
	public static void main(String[] args) {
		GomokuGUI gui = new GomokuGUI();
		gui.setVisible(true);
	}
	
	
	/**
     * Prepares the main elements of the GUI, including its size, location, menu bar, and start panel.
     */
	private void prepareElements() {
		setTitle("GomokuPOO");
		setSize(PREFERRED_DIMENSION);
		int x = (screenSize.width - WIDTH) / 2;
		int y = (screenSize.height - HIGH) / 2;
		setLocation(x, y);
		setJMenuBar(prepareElementsMenu());
		prepareElementsStart();
		getContentPane().add(start);
		setResizable(false);
		prepareMenuActions();
	}

	/**
     * Prepares the menu bar of the GUI, including menu items for starting, saving, opening, and closing games.
     *
     * @return The JMenuBar object.
     */
	private JMenuBar prepareElementsMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("File");

		menuFinish = new JMenuItem("Finish");
		menuNew = new JMenuItem("New");
		menuOpen = new JMenuItem("Open");
		menuSave = new JMenuItem("Save");
		
		menuClose = new JMenuItem("Close");

		gameMenu.add(menuFinish);
		gameMenu.add(menuNew);
		gameMenu.add(menuOpen);
		gameMenu.add(menuSave);
		gameMenu.addSeparator();
		gameMenu.add(menuClose);

		menuBar.add(gameMenu);
		menuBar.setBorderPainted(true);
		return menuBar;

	}
	
	/**
     * Prepares the start panel of the GUI, which includes a "Start new game" button.
     */
	public void prepareElementsStart() {
		start = new JPanel();
		start.setLayout(new BorderLayout());
		startButton = new JButton("Start new game");
		start.add(startButton, BorderLayout.CENTER);
	}
	
	/**
     * Prepares the game elements of the GUI, including the game board, player information, and controls.
     */
	public void prepareElementsGame() {
		this.gomoku = Gomoku.getGomoku();
		gomoku.subscribe(this);
		getContentPane().removeAll();
		move = "NormalToken";
		state = new GomokuState(this);
		time = new TimeGUI();
		player1 = new JPanel();
		player2 = new JPanel();
		tokens1 = new JPanel();
		Set<Class<? extends Token>> tokens = gomoku.getTokenSubtypes();
		tokens1.setLayout(new GridLayout(0, 2));
		tokens1.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Info player one")));
		tokens2 = new JPanel();
		tokens2.setLayout(new GridLayout(0, 2));
		tokens2.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Info player two")));

		tokensPlayer1 = new JButton[tokens.size()];
		tokensPlayer2 = new JButton[tokens.size()];
		int i = 0;
		for (Class<? extends Token> token : tokens) {
			tokensPlayer1[i] = new JButton(token.getSimpleName());
			tokensPlayer2[i] = new JButton(token.getSimpleName());
			tokensPlayer1[i].setEnabled(false);
			i++;
		}
		prepareElementsTokensInfo();
		prepareActionsTokensInfo();
		gameButtonToken = new JButton("Elige tu ficha");
		prapreChange();
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		getContentPane().add(player1, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		getContentPane().add(time, gbc);
		gbc.gridy = 1;
		getContentPane().add(state, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		getContentPane().add(gameButtonToken,gbc);
		gbc.gridx = 2;
		gbc.gridy = 1;
		getContentPane().add(player2, gbc);

		getContentPane().revalidate();
		getContentPane().repaint();

	}
	
	/**
     * Prepares the elements for displaying player one's information.
     */
	private void prepareElementsPlayer1() {
		tokens1.removeAll();
		Player playerOne = gomoku.getPlayerOne();
		HashMap<String, Integer> map = playerOne.getMap();
		tokens1.add(new JLabel("Name :"));
		tokens1.add(new JLabel(playerOne.getName()));
		tokens1.add(new JLabel("Score "));
		tokens1.add(new JLabel(playerOne.getScore() + ""));
		for (int i = 0; i < map.size(); i++) {
			if (gomoku.getTurn() % 2 != 0)
				tokensPlayer1[i].setEnabled(false);
			else if (tokensPlayer1[i].getText().equals(gomoku.getToken()))
				tokensPlayer1[i].setEnabled(true);
			else
				tokensPlayer1[i].setEnabled(false);
			tokens1.add(tokensPlayer1[i]);
			tokens1.add(new JLabel(": " + map.get(tokensPlayer1[i].getText())));
		}
		player1.add(tokens1);
		player1.revalidate();
		player1.repaint();
	}
	
	/**
     * Prepares the elements for displaying player one's information.
     */
	private void prepareElementsPlayer2() {
		tokens2.removeAll();
		Player playerTwo = gomoku.getPlayerTwo();
		HashMap<String, Integer> map = playerTwo.getMap();
		tokens2.add(new JLabel("Name :"));
		tokens2.add(new JLabel(playerTwo.getName()));
		tokens2.add(new JLabel("Score :"));
		tokens2.add(new JLabel(playerTwo.getScore() + ""));
		for (int i = 0; i < map.size(); i++) {
			if (gomoku.getTurn() % 2 == 0)
				tokensPlayer2[i].setEnabled(false);
			else if (tokensPlayer2[i].getText().equals(gomoku.getToken()))
				tokensPlayer2[i].setEnabled(true);
			else
				tokensPlayer2[i].setEnabled(false);
			tokens2.add(tokensPlayer2[i]);
			tokens2.add(new JLabel(": " + map.get(tokensPlayer2[i].getText())));
		}
		player2.add(tokens2);
		player2.revalidate();
		player2.repaint();
	}
	
	 /**
     * Prepares the elements for displaying player information and tokens.
     */
	public void prepareElementsTokensInfo() {
		prepareElementsPlayer1();
		prepareElementsPlayer2();
	}
	
	/**
     * Prepares the action listeners for components in the GUI.
     */
	private void prepareActions() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				closeOption();
			}
		});
		prepareActionsStart();
	}
	
	 /**
     * Prepares the action listeners for components in the start panel.
     */
	private void prepareActionsStart() {
		GomokuGUI parent = this;
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				JDialog gameConfig = new GameConfig(screenSize, WIDTH, HIGH, parent);

			}
		});
	}
	
	/**
     * Starts playing the background music.
     */
	private void playBackgroundMusic() {
		if (backgroundMusic != null && !backgroundMusic.isRunning()) {
			backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
     * Stops playing the background music.
     */
	private void stopBackgroundMusic() {
		if (backgroundMusic != null && backgroundMusic.isRunning()) {
			backgroundMusic.stop();
		}
	}
	
	
	private void prapreChange() {
		gameButtonToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog d = new JDialog();
				d.setSize(new Dimension(3 * WIDTH / 4 , 3 * HIGH / 4));
				int x = (screenSize.width - 3 * WIDTH / 4) / 2;
				int y = (screenSize.height - 3 * HIGH / 4) / 2;
				d.setLocation(x, y);
				d.setLayout(new FlowLayout());
				gameToken = new JComboBox<>();
				gameToken.addItem("NormalToken");
				gameToken.addItem("TemporaryToken");
				gameToken.addItem("HeavyToken");
				gameToken.addItem("OverlapToken");
				d.add(gameToken);
				JButton c = new JButton("Escoger");
				d.add(c);
				d.setVisible(true);
				c.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ec) {
						d.dispose();
						gomoku.setPlayerToken((String)gameToken.getSelectedItem());
					}
					
				});
			}
		});
	}
	/**
     * Prepares the action listeners for tokens, menu items, and the closing option.
     */
	private void prepareActionsTokensInfo() {
		
		for (JButton j : tokensPlayer1) {
			j.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					move = j.getText();
				}
			});
		}

		for (JButton j : tokensPlayer2) {
			j.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					move = j.getText();
				}
			});
		}
	}

	/**
	 * Prepares the action listeners for menu items, such as creating a new game,
	 * saving, opening, and closing the application.
	 */
	private void prepareMenuActions() {
		GomokuGUI parent = this;
		menuFinish.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				gomoku.finish();

			}
			
		});
		
		menuNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new GameConfig(screenSize, WIDTH, HIGH, parent);
			}
		});
		
		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Gomoku data", "gom");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "You are trying to save the game with the next name: "
							+ chooser.getSelectedFile().getName());
					gomoku.guardarPartida(chooser.getSelectedFile().getAbsolutePath() + ".gom");
				}
			}
		});
		menuOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Gomoku data", "gom");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					JOptionPane.showMessageDialog(null, "You are trying to open the file with the next name: "
							+ chooser.getSelectedFile().getName());
					gomoku = Gomoku.cargarPartida(chooser.getSelectedFile().getAbsolutePath());
					prepareElementsGame();
				}
			}
		});

		menuClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeOption();
			}
		});
	}
	
	/**
	 * Displays a confirmation dialog for closing the application. If the user chooses to exit,
	 * stops the background music, hides the GUI, and exits the application.
	 */
	private void closeOption() {
		int yesNo = JOptionPane.showOptionDialog(null, "Are you sure you want exit?", "Warning",
				JOptionPane.YES_NO_OPTION, JOptionPane.CANCEL_OPTION, null, null, "No");
		if (yesNo == JOptionPane.YES_OPTION) {
			stopBackgroundMusic();
			setVisible(false);
			System.exit(0);
		}
	}
	
	/**
	 * Retrieves the current move selected by the player.
	 *
	 * @return The selected move.
	 */
	public String getMove() {
		return move;
	}
	
	/**
	 * Receives the subscription to updates from the Gomoku game and requests the initial item.
	 *
	 * @param subscription The subscription to the Gomoku game.
	 */
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(1);
	}

	/**
	 * Receives updates from the Gomoku game when a new state is available.
	 * Requests another item and updates the GUI's tokens information.
	 *
	 * @param item The Gomoku game instance.
	 */
	@Override
	public void onNext(Gomoku item) {
		subscription.request(1);
		prepareElementsTokensInfo();
	}

	/**
	 * Handles errors that may occur during the game's state updates.
	 *
	 * @param throwable The error that occurred.
	 */
	@Override
	public void onError(Throwable throwable) {
		System.err.println("Error: " + throwable.getMessage());
	}

	/**
	 * Notifies when the Gomoku game state updates are complete.
	 * Prints a message indicating the completion of the publication.
	 */
	@Override
	public void onComplete() {
		System.out.println("La publicación ha finalizado.");
	}

}