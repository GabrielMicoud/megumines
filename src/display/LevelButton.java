package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import Ressources.ButtonImage;
import util.Level;

/**
 * Classe représentant les boutons de sélection du niveau, en mode hors ligne.
 * @author Utilisateur
 *
 */

public class LevelButton extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1367262671377666827L;
	Level level;
	private static final int BUTTON_WIDTH = 35;
	private static final int BUTTON_HEIGHT = 35;
	MainFrame mainFrame;
	
	public LevelButton(MainFrame mainFrame, Level level) {
		this.level = level;
		this.mainFrame = mainFrame;
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		this.addMouseListener(this);
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Toolkit toolkit = getToolkit();
		g.drawImage(toolkit.getImage(ButtonImage.getRessourceLevel(level)), 0, 0, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		mainFrame.getGame().gameReset(level);
		mainFrame.refresh();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
