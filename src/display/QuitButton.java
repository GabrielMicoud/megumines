package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import Ressources.ButtonImage;

public class QuitButton extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -842607530800078411L;
	private static final int BUTTON_WIDTH = 35;
	private static final int BUTTON_HEIGHT = 35;
	MainFrame mainFrame;
	
	public QuitButton(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		this.addMouseListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Toolkit toolkit = getToolkit();
		g.drawImage(toolkit.getImage(ButtonImage.QUIT), 0, 0, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Konnichiwa ! Soshite, sayonara.");
		mainFrame.getGame().writeHighScores();
		System.exit(0);
		return;
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
