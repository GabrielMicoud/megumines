package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import Ressources.CaseImage;
import client.Game;
import util.Colors;
import util.State;

/**
 * Classe représentant les cases de la grille. Elle contrôle l'image affichée par la case. Elle est sensible aux actions de la souris quand il y a un clic ou un passage.
 * @author Gabriel Micoud
 *
 */
public class Case extends JPanel implements MouseListener{
	State state;
	private static final int CASE_WIDTH = 24;
	private static final int CASE_HEIGHT = 24;
	public int x, y;
	private String imgString;
	Game game;
	
	/**
	 * Constructeur prenant en compte les coordonnées de la case, ainsi que le jeu auquel elle est attachée.
	 * @param x
	 * @param y
	 * @param game
	 */
	public Case(int x, int y, Game game) {
		setPreferredSize(new Dimension(CASE_WIDTH, CASE_HEIGHT));
		state = State.HIDDEN;
		this.x = x;
		this.y = y;
		this.game = game;
		this.addMouseListener(this);
	}
	
	/**
	 * Fonction changeant l'état et la couleur de la case, avant de rafraîchir.
	 * @param s
	 * @param c
	 */
	public void setStateColor(State s, Colors c) {
		state = s;
		setBackground(c.color);
		refresh();
	}
	
	/**
	 * Fonction de rafraîchissement de la case.
	 */
	public void refresh() {
		repaint();
	}
	
	/**
	 * Définition de l'image par défaut de la case. Le paint component est appelé à chaque rafraîchissement via la fonction repaint().
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//our design
		Toolkit toolkit = getToolkit();
		imgString = CaseImage.getRessourceForState(state);
		g.drawImage(toolkit.getImage(imgString),0, 0, this);
		//g.drawString(txt, getWidth()/2, getHeight()/2);
	}
	
	/**
	 * Fonction appelant les fonctions play() ou flag() du jeu s'il y a eu un clic droit ou gauche.
 	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			game.play(x, y);
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			game.flag(x, y);
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		if(state == State.HIDDEN) {
			imgString = CaseImage.MOUSE_UNKNOWN;
			refresh();
		}
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		if(state == State.HIDDEN) {
			imgString = CaseImage.UNKNOWN;
			refresh();
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
