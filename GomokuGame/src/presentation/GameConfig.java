package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import domain.Gomoku;
import domain.GomokuException;
import domain.Player.Player;


/**
 * The GameConfig class represents a dialog for configuring the settings of a Gomoku game,
 * including board size, game mode, players, and other options.
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class GameConfig extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private int WIDTH;
	private GomokuGUI gui;
	Gomoku gomoku;
	
	private JComboBox<String> gameMode;
	private JTextField limitTokens;
	private JTextField limitTime;

	private JComboBox<String> gamePlayers;
	private JTextField gamePlayerOne;
	private JTextField gamePlayerTwo;
	private JComboBox<String> machine1;
	private JComboBox<String> machine2;
	
	private JTextField gameSizeField;
	private JSlider gameSlider1;
	private JSlider gameSlider2;
	private JButton createGame;
	
	
	private JButton selectColorPlayerOne;
	private JButton selectColorPlayerTwo;
	private Color playerOneColor;
	private Color playerTwoColor;
	private int HIGH;
	
	
	/**
     * Constructs a GameConfig dialog with the specified screen size, width, height, and parent GUI.
     *
     * @param screenSize The screen size.
     * @param WIDTH      The width of the dialog.
     * @param HIGH       The height of the dialog.
     * @param gui        The parent GomokuGUI.
     */
	public GameConfig(Dimension screenSize,int WIDTH, int HIGH,GomokuGUI gui) {
		this.gui = gui;
		this.HIGH = HIGH;
		this.WIDTH = WIDTH;
		this.setSize(new Dimension(3 * this.WIDTH / 4 , 3 * this.HIGH / 4));
		int x = (screenSize.width - 3 * this.WIDTH / 4) / 2;
		int y = (screenSize.height - 3 * this.HIGH / 4) / 2;
		this.setLocation(x, y);
		this.add(new JScrollPane(prepareElementsStartConfig()));
		this.setVisible(true);
		prepareActionsCreateGame(this);
	}
	
	
	/**
	 * Prepares and constructs the configuration panel for starting a new game.
	 *
	 * @return The configuration panel.
	 */
	private JPanel prepareElementsStartConfig() {
		JPanel config = new JPanel();
		config.setLayout(new GridLayout(0, 1));
		config.add(prepareElementsStartConfigGameSize());
		config.add(prepareElementsStartConfigGameMode());
		config.add(prepareElementsStartConfigGamePlayers());
		config.add(prepareElementsStartConfigGamesSlider());
		config.add(prepareElementsStartCreateGame());
		return config;
	}
		
	
	/**
	 * Prepares and constructs the panel for configuring the game board size.
	 *
	 * @return The panel for configuring the game board size.
	 */
	private JPanel prepareElementsStartConfigGameSize() {
		JPanel configGameSize = new JPanel();
		configGameSize
				.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Size of the board")));
		configGameSize.setLayout(new GridLayout(0, 3, WIDTH / 16, 0));
		gameSizeField = new JTextField("15");
		configGameSize.add(gameSizeField);
		return configGameSize;
	}
	
	/**
	 * Prepares and constructs the panel for configuring the game mode.
	 *
	 * @return The panel for configuring the game mode.
	 */
	private JPanel prepareElementsStartConfigGameMode() {
		JPanel configGameMode = new JPanel();
		configGameMode.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Game mode config")));
		configGameMode.setLayout(new GridLayout(0, 3, WIDTH / 16, 0));
		gameMode = new JComboBox<>();
		gameMode.addItem("Normal");
		gameMode.addItem("QuickTime");
		gameMode.addItem("Limited");
		limitTokens = new JTextField("Limit tokens");
		limitTokens.setEditable(false);
		limitTime = new JTextField("Limit time (seconds)");
		limitTime.setEditable(false);
		configGameMode.add(gameMode);
		configGameMode.add(limitTokens);
		configGameMode.add(limitTime);
		prepareActionsStartConfigGameMode();
		return configGameMode;
	}
	
	
	/**
	 * Prepares and constructs the panel for configuring the game players.
	 *
	 * @return The panel for configuring the game players.
	 */
	private JPanel prepareElementsStartConfigGamePlayers() {
		JPanel configGamePlayers = new JPanel();
		configGamePlayers
				.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Players config")));
		configGamePlayers.setLayout(new GridLayout(0, 3, WIDTH / 16, 0));
		playerOneColor = Color.BLACK;
		selectColorPlayerOne = new JButton("Select Color for Player One");
		playerTwoColor = Color.WHITE;
	    selectColorPlayerTwo = new JButton("Select Color for Player Two");
	    configGamePlayers.add(selectColorPlayerOne);
	    configGamePlayers.add(selectColorPlayerTwo);
		gamePlayers = new JComboBox<>();
		gamePlayers.addItem("Player vs Player");
		gamePlayers.addItem("Player vs Machine");
		gamePlayers.addItem("Machine vs Machine");
		gamePlayerOne = new JTextField("Name player one");
		gamePlayerTwo = new JTextField("Name player two");
		configGamePlayers.add(gamePlayers);
		configGamePlayers.add(gamePlayerOne);
		configGamePlayers.add(gamePlayerTwo);
		
		Set<Class<? extends Player>> c = Player.getPlayerSubtypes();
		machine1 = new JComboBox<>();
		machine2 = new JComboBox<>();
		for(Class<? extends Player> x : c) {
			String name = x.getSimpleName();
			if (!name.equals("NormalPlayer")&&!name.equals("MachinePlayer")) {
				machine1.addItem(name);
				machine2.addItem(name);
			}
		} 
		configGamePlayers.add(new JLabel(""));
		configGamePlayers.add(machine1);
		configGamePlayers.add(machine2);
		machine1.setEnabled(false);
        machine2.setEnabled(false);
		prepareActionsStartConfigGamePlayers();
		return configGamePlayers;
	}
	
	
	/**
	 * Prepares and constructs the panel for configuring the game sliders.
	 *
	 * @return The panel for configuring the game sliders.
	 */
	private JPanel prepareElementsStartConfigGamesSlider() {
		JPanel configGameSlider = new JPanel();
		configGameSlider.setBorder(
				new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new TitledBorder("Especial percentage (%)")));
		configGameSlider.setLayout(new FlowLayout(FlowLayout.LEFT));
		configGameSlider.setLayout(new GridLayout(2,2,30,0));
		gameSlider1 = new JSlider(0, 100, 0);
		gameSlider1.setMajorTickSpacing(20);
		gameSlider1.setMinorTickSpacing(5);
		gameSlider1.setPaintTicks(true);
		gameSlider1.setPaintLabels(true);
		gameSlider2 = new JSlider(0, 100, 0);
		gameSlider2.setMajorTickSpacing(20);
		gameSlider2.setMinorTickSpacing(5);
		gameSlider2.setPaintTicks(true);
		gameSlider2.setPaintLabels(true);
		configGameSlider.add(new JLabel("Percentage of tokens"));
		configGameSlider.add(new JLabel("Percentage of squares"));
		configGameSlider.add(gameSlider1);
		configGameSlider.add(gameSlider2);
		return configGameSlider;
	}
	
	
	/**
	 * Prepares and constructs the panel for creating the game.
	 *
	 * @return The panel for creating the game.
	 */
	private JPanel prepareElementsStartCreateGame() {
		JPanel start = new JPanel();
		start.setLayout(new FlowLayout(FlowLayout.CENTER));
		createGame = new JButton("Create the game");
		start.add(createGame);
		return start;
	}

	
	/**
     * Sets up the action listeners for the game mode combo box to dynamically adjust the text fields
     * based on the selected game mode.
     */
	private void prepareActionsStartConfigGameMode() {
		gameMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> combo = (JComboBox<String>) e.getSource();
				String selecction = (String) combo.getSelectedItem();
				if (selecction.equals("Normal")) {
					limitTokens.setEditable(false);
					limitTime.setEditable(false);
					limitTokens.setText("No limit");
					limitTime.setText("No limit");
				} else if (selecction.equals("QuickTime")) {
					limitTokens.setEditable(false);
					limitTime.setEditable(true);
					limitTokens.setText("No limit");
					limitTime.setText("600");
				} else {
					limitTokens.setEditable(true);
					limitTime.setEditable(false);
					limitTokens.setText("50");
					limitTime.setText("No limit");
				}
			}
		});
	}
	
	
	/**
     * Sets up the action listeners for the game players combo box to dynamically adjust the text fields
     * based on the selected game players configuration.
     */
	private void prepareActionsStartConfigGamePlayers() {
	    gamePlayers.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            @SuppressWarnings("unchecked")
	            JComboBox<String> combo = (JComboBox<String>) e.getSource();
	            String selection = (String) combo.getSelectedItem();
	            if (selection.equals("Player vs Player")) {
	                gamePlayerOne.setEditable(true);
	                machine1.setEnabled(false);
	                machine2.setEnabled(false);
	                gamePlayerTwo.setEditable(true);
	                gamePlayerOne.setText("Name player one");
	                gamePlayerTwo.setText("Name player two");
	            } else if (selection.equals("Player vs Machine")) {
	                gamePlayerOne.setEditable(true);
	                gamePlayerTwo.setEditable(false);
	                machine1.setEnabled(false);
	                machine2.setEnabled(true);
	                gamePlayerOne.setText("Name player one");
	                gamePlayerTwo.setText("Machine");
	            } else {
	                gamePlayerOne.setEditable(false);
	                gamePlayerTwo.setEditable(false);
	                machine1.setEnabled(true);
	                machine2.setEnabled(true);
	                gamePlayerOne.setText("MachineOne");
	                gamePlayerTwo.setText("MachineTwo");
	            }
	        }
	    });
	    selectColorPlayerOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playerOneColor = JColorChooser.showDialog(null, "Select Color for Player One", Color.BLACK);
	            selectColorPlayerOne.setBackground(playerOneColor);
			}
            
        });

        selectColorPlayerTwo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playerTwoColor = JColorChooser.showDialog(null, "Select Color for Player Two", Color.WHITE);
	            selectColorPlayerTwo.setBackground(playerTwoColor);
			}
		});
	}

	
	/**
     * Sets up the action listener for the "Create the game" button to create a Gomoku game with the specified
     * configurations and start the game.
     *
     * @param parent The parent JDialog.
     */
	private void prepareActionsCreateGame(JDialog parent) {
		createGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int size = Integer.parseInt(gameSizeField.getText());
					String mode = (String) gameMode.getSelectedItem();
					gomoku = new Gomoku(mode, size);
					parent.dispose();
					prepareActionSetPlayersTypeAndName();
					gui.prepareElementsGame();
				} catch (NumberFormatException | GomokuException ex ) {
					Log.record(ex);
					Timer timer = new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							JOptionPane.getRootFrame().dispose();
						}
					});
					timer.setRepeats(false);
					timer.start();
					JOptionPane.showMessageDialog(null, "Invalid game size");
					timer.restart();
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					Log.record(ex);
					ex.printStackTrace();
				} 
			}
		});
	}
	
	
	 /**
     * Sets up the players' types, names, and other configurations based on the selected options.
	 * @param playerTwoColor2 
	 * @param playerOneColor2 
     */
	private void prepareActionSetPlayersTypeAndName() {
		int defaultNum = Integer.parseInt(gameSizeField.getText())*Integer.parseInt(gameSizeField.getText());
		try {
			gomoku.setLimits(Integer.parseInt(limitTokens.getText()),60);
		} catch (NumberFormatException e) {
			Log.record(e);
			try {
				gomoku.setLimits((defaultNum*defaultNum)/2,Integer.parseInt(limitTime.getText()));
			}catch (NumberFormatException e1) {Log.record(e1);
				gomoku.setLimits((defaultNum*defaultNum)/2,60);
			}
			
		}
		String players = (String) gamePlayers.getSelectedItem();
		String m1 = (String) machine1.getSelectedItem();
		String m2 = (String) machine2.getSelectedItem();
		if (players.equals("Player vs Player")) {
			try {
				gomoku.setPlayers("NormalPlayer", "NormalPlayer");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Log.record(e);
				e.printStackTrace();
			}
		} else if (players.equals("Player vs Machine")) {
			try {
				gomoku.setPlayers("NormalPlayer", m2);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Log.record(e);
				e.printStackTrace();
			}
		} else {
			try {
				gomoku.setPlayers(m1, m2);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				Log.record(e);
				e.printStackTrace();
			}
		}
		gomoku.setPlayersInfo(gamePlayerOne.getText(),playerOneColor, gamePlayerTwo.getText(),playerTwoColor);
		gomoku.setEspecialInfo(gameSlider1.getValue(), gameSlider2.getValue());
	}

}