package display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import Ressources.ButtonImage;
import util.Colors;
import util.VictoryDefeat;

public class ResetButton extends JPanel implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6317216891145459518L;
	VictoryDefeat v;
	private static final int BUTTON_WIDTH = 35;
	private static final int BUTTON_HEIGHT = 35;
	MainFrame mainFrame;
	
	public ResetButton(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.v = VictoryDefeat.IDLE;
		setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
		setBackground(Colors.DEFAULT.color);
		this.addMouseListener(this);
		
	}
	
	public void setFace(VictoryDefeat v) {
		this.v = v;
		refresh();
	}
	
	public void refresh() {
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Toolkit toolkit = getToolkit();
		g.drawImage(toolkit.getImage(ButtonImage.getRessourceButton(v)), 0, 0, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		mainFrame.getGame().gameReset();
		mainFrame.refresh();
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
