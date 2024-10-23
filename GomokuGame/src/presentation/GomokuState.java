package presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonListener;

import domain.Gomoku;
import domain.GomokuException;

/**
 * The GomokuState class represents the graphical user interface for displaying and interacting with the Gomoku game state.
 * It implements the Subscriber interface to receive updates from the Gomoku game logic.
 * @author Juan Daniel Murcia - Mateo Forero Fuentes
 * @version 2.0
 */
public class GomokuState extends JPanel implements Subscriber<Gomoku>{
	/**
	 * 
	 */
	
	/**
     * Serial version UID for serialization.
     */
	/**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 3L;

    /**
     * The parent GomokuGUI.
     */
    private GomokuGUI gui;

    /**
     * The size of the Gomoku game state.
     */
    private int size;

    /**
     * The side length of each square in the Gomoku game state.
     */
    private int SIDE;

    /**
     * The Gomoku game instance.
     */
    private Gomoku gomoku;

    /**
     * 2D array of JButtons representing the game state.
     */
    private BotonConCirculo[][] buttons;

    /**
     * The subscription for receiving updates from the Gomoku game logic.
     */
    private Subscription subscription;

    /**
     * The thread responsible for updating the view of the Gomoku game state.
     */
    private Thread view;
	
	
	/**
     * Constructs a GomokuState with the specified parent GomokuGUI.
     *
     * @param gui The parent GomokuGUI.
     */
	public GomokuState(GomokuGUI gui) {
		this.gui = gui;
		gomoku = Gomoku.getGomoku();
		gomoku.subscribe(this);
		size = Math.min((3 * GomokuGUI.WIDTH) / 4, (3 * GomokuGUI.HIGH) / 4);
		SIDE = size / gomoku.getSize();
		buttons = new BotonConCirculo[gomoku.getSize()][gomoku.getSize()];
		setLayout(new GridLayout(gomoku.getSize(), gomoku.getSize(), 1, 1));
		for (int i = 0; i < gomoku.getSize(); i++) {
			for (int j = 0; j < gomoku.getSize(); j++) {
				buttons[i][j] = new BotonConCirculo();
				add(buttons[i][j]);
			}
		}
		prepareActionsSquareClicked();
		setPreferredSize(new Dimension(1 + SIDE * gomoku.getSize(), 1 + SIDE * gomoku.getSize()));
		this.setBackground(new Color(190, 120, 50));
		view = new Thread(gomoku);
		view.start();
	}
	
	
	/**
     * Paints the Gomoku game state on the panel.
     *
     * @param g The Graphics object to paint on.
     */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int f = 0; f <= gomoku.getSize(); f++) {
			g.drawLine(f * SIDE, 0, f * SIDE, gomoku.getSize() * SIDE);
		}
		for (int c = 0; c <= gomoku.getSize(); c++) {
			g.drawLine(0, c * SIDE, gomoku.getSize() * SIDE, c * SIDE);
		}
		for (int i = 0; i < gomoku.getSize(); i++) {
			for (int j = 0; j < gomoku.getSize(); j++) {
				Color color = gomoku.getTokenColor(i, j);
				if (color!=null) {
					
					String s = gomoku.getToken(i,j);
					buttons[i][j].changeColor(color,s.substring(0, 1));
				}
				else {
					buttons[i][j].changeColor(new Color(190, 120, 50),"");
				}
				
			}
		}
	}
	
	
	/**
     * Prepares the action listeners for handling square clicks.
     */
	private void prepareActionsSquareClicked() {
		for (int i = 0; i < gomoku.getSize(); i++) {
			for (int j = 0; j < gomoku.getSize(); j++) {
				int x = i;
				int y = j;
				for (MouseListener m : buttons[i][j].getMouseListeners()) {
					buttons[i][j].removeMouseListener(m);
				}
				buttons[i][j].addMouseListener(new BasicButtonListener(null) {
					public void mouseEntered(MouseEvent e) {
					}

					public void mouseExited(MouseEvent e) {
					}
				});
				buttons[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int init = gomoku.getTurn();
						try {
							gomoku.play(x, y);
							String winner = gomoku.getWinner();
							if (init != gomoku.getTurn()) {
								gui.prepareElementsTokensInfo();
							}
							if (winner != null) {
								repaint();
								JOptionPane.showMessageDialog(null, "The winner is: " + winner);
								gui.getContentPane().removeAll();
								gui.add(gui.start);

							}
						} catch (GomokuException exe) {
							Log.record(exe);
							Timer timer = new Timer(2000, new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									JOptionPane.getRootFrame().dispose();
								}
							});
							timer.setRepeats(false);
							timer.start();
							JOptionPane.showMessageDialog(null, exe.getMessage());
							timer.restart();
						}
						repaint();
					}
				});

			}
		}
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
     * @param item The Gomoku instance received as an update.
     */
	@Override
	public void onNext(Gomoku item) {
		System.out.println(item);
        subscription.request(1); 
        String winner = gomoku.getWinner();
		if (winner != null) {
			repaint();
			JOptionPane.showMessageDialog(null, "The winner is: " + winner);
			gui.getContentPane().removeAll();
			gui.add(gui.start);

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
        System.err.println("Error: " + throwable.getMessage());
    }
	
	
	/**
     * Implementation of the onComplete method of the Subscriber interface.
     */
    @Override
    public void onComplete() {
        System.out.println("La publicación ha finalizado.");
    }
}

class BotonConCirculo extends JButton {

    private Color colorCirculo;
    private String s;

    public BotonConCirculo() {
        super();
        this.setBackground(new Color(190, 120, 50));
        colorCirculo = new Color(190, 120, 50);
        s = "";
    }
    public void changeColor(Color colorCirculo,String s) {
        this.colorCirculo = colorCirculo;
        this.s = s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujarCirculo(g);
        dibujarLetra(g);
    }
    
    private void dibujarCirculo(Graphics g) {
        int diametro = Math.min(getWidth(), getHeight()) - 5;
        int x = (getWidth() - diametro) / 2;
        int y = (getHeight() - diametro) / 2;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(colorCirculo);
        g2d.fill(new Ellipse2D.Double(x, y, diametro, diametro));
        g2d.dispose();
    }

    private void dibujarLetra(Graphics g) {
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(s)) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getForeground());
        g2d.drawString(s, x, y);
        g2d.dispose();
    }
}
