

package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JPanel;

import Ressources.ButtonImage;
import util.Level;

public class OnlineButton extends JPanel implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1698475018649912903L;
	boolean online;
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 35;
	MainFrame mainFrame;
	
	public OnlineButton(MainFrame mainFrame) {
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		online = false;
		this.mainFrame = mainFrame;
		this.addMouseListener(this);
	}
	
	public void setButton(boolean online) {
		this.online = online;
		refresh();
	}

	public void refresh() {
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Toolkit toolkit = getToolkit();
		g.drawImage(toolkit.getImage(ButtonImage.getRessourceOnline(online)), 0, 0, this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(!mainFrame.getGame().isOnline()) {
			//game.connect("localhost", 2000, "Megumin"); //MAGIC TEXT AND NUMBER TO REMOVE
			new ServerTextField(mainFrame);
		} else {
			try {
				mainFrame.getGame().disconnect();
				mainFrame.getGame().gameReset(false, Level.EASY);
				mainFrame.setOfflineDisplay();
				setButton(false);
				mainFrame.refresh();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
