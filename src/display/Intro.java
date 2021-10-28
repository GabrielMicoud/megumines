package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import Ressources.ButtonImage;

public class Intro extends JPanel implements MouseListener {
	
	private static final String IMAGE = "img/intro_megumin.png";
	private static final int BUTTON_WIDTH = 600;
	private static final int BUTTON_HEIGHT = 521;
	MainFrame mainFrame;
	
	public Intro(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		this.addMouseListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Toolkit toolkit = getToolkit();
		g.drawImage(toolkit.getImage(IMAGE), 0, 0, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		mainFrame.begin();
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
