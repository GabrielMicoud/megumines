/**
 * @author Gabriel Micoud
 */

package display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import client.Game;
import util.Colors;
import util.Level;
import util.State;

public class MainFrame extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4876718790730079080L;
	private static final int FONT_SIZE = 20;
	Game game;
	Case[][] cases;
	State[][] states;
	private static final String ICON = "img/megumin.png";
	private QuitButton butQuit;
	private JLabel counterLabel;
	private JLabel scoreLabel;
	private JLabel highScoreLabel;
	private ArrayList<JLabel> players = new ArrayList<JLabel>();
	private ArrayList<String> playerNames = new ArrayList<String>();
	private OnlineButton butServ;
	private ResetButton butReset;
	private LevelButton butEasy;
	private LevelButton butMedium;
	private LevelButton butHard;
	private Intro intro;
	private JPanel gauche;
	private JPanel haut;
	private JPanel mines;
	private JPanel bas;
	private boolean isIntro = true;
	public MainFrame(Game game) {
		super("Megumines");
		this.game = game;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setBackground(Colors.BACKGROUND.color);
		setIconImage(Toolkit.getDefaultToolkit().getImage(ICON));
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = screenSize.height;
		int width = screenSize.width;
		setSize(width/2, height/2);
		setLocationRelativeTo(null);
		
		//intro
		intro = new Intro(this);
		
		//haut de page
		haut = new JPanel();
		haut.setLayout(new FlowLayout());
		haut.setBackground(Colors.BACKGROUND.color);
		//bas de page
		bas = new JPanel();
		bas.setLayout(new FlowLayout());
		bas.setBackground(Colors.BACKGROUND.color);
		//mines
		mines = new JPanel();
		mines.setBackground(Colors.BACKGROUND.color);
		mines.setBorder(new CompoundBorder(mines.getBorder(), new EmptyBorder(20,20,20,20)));
		//gauche
		gauche = new JPanel();
		gauche.setLayout(new GridLayout(7,1));
		gauche.setBorder(new CompoundBorder(gauche.getBorder(), new EmptyBorder(20,20,20,20)));
		gauche.setBackground(Colors.BACKGROUND.color);
		
		/**
		 * boutons et labels de haut de page
		 */
		
		//counter
		counterLabel = new JLabel("Time : " + String.format("%03d", 0));
		counterLabel.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
		counterLabel.setHorizontalAlignment(JLabel.CENTER);
		counterLabel.setForeground(Color.WHITE);
		gauche.add(counterLabel);
		
		//score
		scoreLabel = new JLabel("Score : " + String.format("%04d", 0));
		scoreLabel.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);
		scoreLabel.setForeground(Color.WHITE);
		gauche.add(scoreLabel);
		
		//high score
		highScoreLabel = new JLabel("High score : " + String.format("%04d", 0));
		highScoreLabel.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
		highScoreLabel.setHorizontalAlignment(JLabel.CENTER);
		highScoreLabel.setForeground(Color.WHITE);
		gauche.add(highScoreLabel);
				
		//Quit button
		butQuit = new QuitButton(this);
		haut.add(butQuit);
		
		//reset button
		butReset = new ResetButton(this);
		haut.add(butReset);
		
		//server button
		butServ = new OnlineButton(this);
		haut.add(butServ);
		
		/**
		 * boutons de bas de page
		 */
		
		//Easy button
		butEasy = new LevelButton(this, Level.EASY);
		bas.add(butEasy);
		
		//Medium button
		butMedium = new LevelButton(this, Level.MEDIUM);
		bas.add(butMedium);
		
		//Hard button
		butHard = new LevelButton(this, Level.HARD);
		bas.add(butHard);
	
		initCases();
		
		/**
		 * ajout pages haut, bas et gauche
		 */
		
		add(intro);
		/*add(haut, BorderLayout.NORTH);
		add(mines, BorderLayout.CENTER);
		add(bas, BorderLayout.SOUTH);
		add(gauche, BorderLayout.WEST);*/
		refresh();
		
	}
	
	public void begin() {
		if(!isIntro) return;
		isIntro = false;
		remove(intro);
		add(haut, BorderLayout.NORTH);
		add(mines, BorderLayout.CENTER);
		add(bas, BorderLayout.SOUTH);
		add(gauche, BorderLayout.WEST);
		refresh();
	}
	
	public void refresh() {
		if(game.isOnline()) {
			for(JLabel player: players) {
				player.setForeground(Colors.values()[players.indexOf(player)].color);
			}
		}
		pack();
		setVisible(true);
	}
	
	public Game getGame() {
		return game;
	}
	
	public void addPlayer(String name, int index) {
		if(players.size() > index) if (players.get(index) != null) return;
		JLabel player = new JLabel();
		//player.setBackground(Colors.values()[index].color);
		player.setForeground(Colors.values()[index].color);
		player.setHorizontalAlignment(JLabel.CENTER);
		player.setFont(new Font("Verdana", Font.PLAIN, FONT_SIZE));
		player.setText(name + " : 0000");
		players.add(player);
		gauche.add(players.get(index));
		playerNames.add(name);
		refresh();
	}
	
	public void removePlayer(int index) {
		gauche.remove(players.get(index));
		playerNames.remove(playerNames.get(index));
		players.remove(index);
		refresh();
	}
	
	public void setScore(int score, int index) {
		if(players.size() > index) {
			players.get(index).setText(playerNames.get(index) + " : " + String.format("%04d", score));
			refresh();	
		}
	}
	
	public void setHighScore(int hScore) {
		highScoreLabel.setText("High score : " + String.format("%04d", hScore));
		refresh();
	}
	
	public OnlineButton getServerButton() {
		return butServ;
	}
	
	public ResetButton getResetButton() {
		return butReset;
	}
	
	public JLabel getCounterLabel() {
		return counterLabel;
	}
	
	public JLabel getScoreLabel() {
		return scoreLabel;
	}
	
	public ArrayList<JLabel> getPlayerLabels() {
		return players;
	}
	
	public void setOnlineDisplay() {
		bas.remove(butEasy);
		bas.remove(butMedium);
		bas.remove(butHard);
		
		gauche.remove(scoreLabel);
		refresh();
	}
	
	public void setOfflineDisplay() {
		bas.add(butEasy);
		bas.add(butMedium);
		bas.add(butHard);
		
		for(JLabel player : players) gauche.remove(player);
		gauche.add(scoreLabel);
		refresh();
	}
	
	public void initCases(int dimX, int dimY) {
		mines.removeAll();
		mines.setLayout(new GridLayout(dimX, dimY));
		cases = new Case[dimX][dimY];
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				cases[x][y] = new Case(x, y, game);
				mines.add(cases[x][y], x, y);
			}
		}
		mines.repaint();
	}
	
	public void initCases() {
		initCases(game.getDimX(), game.getDimY());
	}
	
	public void draw(int dimX, int dimY, State[][] states, Colors[][] colors) {
		for (int x = 0; x < dimX; x++) {
			for (int y = 0; y < dimY; y++) {
				cases[x][y].setStateColor(states[x][y], colors[x][y]);
			}
		}
		repaint();

	}
	public void draw() {
		draw(game.getDimX(), game.getDimY(), game.getStates(), game.getColors());
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == butQuit) {
			System.out.println("Konnichiwa ! Soshite, sayonara.");
			System.exit(0);
			return;
		}
		if(e.getSource() == butReset) {
			game.gameReset();
			refresh();
		}
		if(e.getSource() == butEasy) {
			game.gameReset(Level.EASY);
			refresh();
		}
		if(e.getSource() == butMedium) {
			game.gameReset(Level.MEDIUM);
			refresh();
		}
		if(e.getSource() == butHard) {
			game.gameReset(Level.HARD);
			refresh();
		}
	}
}

