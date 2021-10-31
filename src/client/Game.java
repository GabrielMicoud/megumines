package client;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import display.MainFrame;
import util.Champ;
import util.Colors;
import util.Level;
import util.State;
import util.VictoryDefeat;

/**
 * Classe principale du client. Il permet de faire fonctionner un jeu en solo, ou un jeu en ligne si le serveur est connect�
 * 
 * @author Gabriel Micoud
 *
 */
public class Game implements Runnable{
	private static final int ONLINE_HIGH_SCORE_INDEX = 3;
	private static final int INTRO_DURATION = 2000;
	
	Champ c;
	MainFrame mainFrame;
	State[][] states;
	Colors[][] colors;
	Level level;
	boolean online = false;
	boolean firstclick = true;
	boolean victory = false;
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;
	Thread thread;
	String name;
	int seconds;
	int elapsedTime;
	Timer timer;
	Integer[] highScores = new Integer[4]; // high scores, easy medium hard and online
	String[] serverPresets = new String[3];
	int score = 0;
	ScheduledExecutorService executorService;
	
	/**
	 * Constructeur du jeu. Il configure la grille en fonction du niveau qu'on lui donne. Selon les choix EASY, MEDIUM et HARD.
	 * 
	 * @param level
	 */
	public Game(Level level) {
		this.level = level;
		this.c = new Champ(level);
		this.mainFrame = new MainFrame(this);
		states = new State[getDimX()][getDimY()];
		colors = new Colors[getDimX()][getDimY()];
		readHighScores();
		mainFrame.setHighScore(highScores[level.ordinal()]);
		
		//set initial states
		for (int x = 0; x < getDimX(); x++)
			for (int y = 0; y < getDimY(); y++) {
				states[x][y] = State.HIDDEN;
				colors[x][y] = Colors.DEFAULT;
			}
		
		//set timer
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				elapsedTime += 1000;
				seconds = (elapsedTime / 1000) % 100;
				mainFrame.getCounterLabel().setText("Time : " + String.format("%03d", seconds));
			}
		});
		
		//set intro duration
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				mainFrame.begin();
			}
		}, INTRO_DURATION, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Constructeur sans param�tre. Il configure automatiquement la difficult� sur EASY.
	 */
	
	public Game() {
		this(Level.EASY);
	}
	
	public void readHighScores() {
		//Path currentDir = Paths.get("scorefiles");
		//System.out.println(currentDir.toAbsolutePath());
		for(int i=0; i<highScores.length; i++) highScores[i] = 0;
		try(BufferedReader br = new BufferedReader(new FileReader("scorefile"))) {
		    String line = br.readLine();
		    int i=0;

		    while (line != null) {
		    	highScores[i] = Integer.parseInt(line);
		    	i++;
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				new File("scorefile").createNewFile();
				System.out.println("Scorefile was created.");
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Scorefile already exists");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeHighScores() {
		try (PrintWriter out = new PrintWriter("scorefile")) {
			for (int i=0; i<highScores.length; i++) {
				out.println(highScores[i]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				new File("scorefile").createNewFile();
				System.out.println("Scorefile was created.");
				writeHighScores();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void readServerPresets() {
		for(int i=0; i<serverPresets.length; i++) serverPresets[i] = "";
		try(BufferedReader br = new BufferedReader(new FileReader("settings"))) {
		    String line = br.readLine();
		    int i=0;

		    while (line != null) {
		    	serverPresets[i] = line;
		    	i++;
		        line = br.readLine();
		    }
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				new File("settings").createNewFile();
				System.out.println("Server settings file was created.");
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Server settings file already exists");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeServerPresets() {
		try (PrintWriter out = new PrintWriter("settings")) {
			for (int i=0; i<serverPresets.length; i++) {
				out.println(serverPresets[i]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				new File("settings").createNewFile();
				System.out.println("Server settings file was created.");
				writeServerPresets();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public String[] getServerPresets() {
		return serverPresets;
	}
	
	public void setServerPresets(String ip, String port, String name) {
		serverPresets[0] = ip;
		serverPresets[1] = port;
		serverPresets[2] = name;
	}
	
	/**
	 * Fonction servant � se connecter au serveur. Il prend en compte une adresse IP, un port, et un pseudonyme qu'on entre dans une fen�tre s�par�e.
	 * 
	 * @param ip
	 * @param port
	 * @param nickname
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	
	public void connect(String ip, int port, String nickname) throws UnknownHostException, IOException {
		System.out.println(ip + " " + port + " " + name);
		mainFrame.setHighScore(highScores[ONLINE_HIGH_SCORE_INDEX]);
		this.name = nickname;
		online = true;
		socket  = new Socket("localhost", 2000);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		Thread t = new Thread(this);
		t.start();
		//AUTRES TRUCS
	}
	
	/**
	 * Fonction servant � se d�connecter du serveur. Il envoie une notification au serveur, qui supprime le client de sa liste de joueurs.
	 * 
	 * @throws IOException
	 */
	
	public void disconnect() throws IOException {
		dos.writeUTF("/disconnect");
		online = false;
	}
	
	/**
	 * Configure le jeu en mode hors-ligne
	 */
	
	public void offline() {
		online = false;
	}
	
	/**
	 * Fonction r�initialisant la grille. Toutes les cases deviennent masqu�es, les mines sont �parpill�es, et la taille de la grille varie en fonction du niveau.
	 * Lorsqu'on est en ligne, cette fonction demande au serveur de r�initialiser la grille globale si le jeu est termin�.
	 * 
	 * @param level
	 */
	
	public void gameReset(Level level) {
		victory = false;
		writeHighScores();
		mainFrame.setHighScore(highScores[level.ordinal()]);
		if(online) {
			try {
				dos.writeUTF("/reset");
				mainFrame.setHighScore(highScores[ONLINE_HIGH_SCORE_INDEX]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			c = new Champ(level);
			c.placeMines();
			states = new State[getDimX()][getDimY()];
			colors = new Colors[getDimX()][getDimY()];
			for (int x = 0; x < getDimX(); x++)
				for (int y = 0; y < getDimY(); y++) {
					states[x][y] = State.HIDDEN;
					colors[x][y] = Colors.DEFAULT;
				}
			this.level = level;
			setResetButton(VictoryDefeat.IDLE);
			mainFrame.initCases();
			firstclick = true;
			elapsedTime = 0;
			score = 0;
			mainFrame.getScoreLabel().setText("Score : " + String.format("%04d", score));
			mainFrame.getCounterLabel().setText("Time : " + String.format("%03d", elapsedTime));
			timer.stop(); //si on appuie sur reset en pleine partie, pour �tre s�r.
		}
	}
	
	/**
	 * Cette fonction modifie l'image du bouton reset, en fonction de l'avancement de la partie, si elle est en cours, gagn�e ou perdue.
	 * @param v
	 */
	
	public void setResetButton(VictoryDefeat v) {
		mainFrame.getResetButton().setFace(v);
	}
	
	/**
	 * Cette fonction prend en compte la connexion au serveur. Elle peut �tre appel�e s'il y a eu une d�connexion brutale.
	 * @param online
	 */
	
	public void gameReset(boolean online) {
		this.online = online;
		gameReset();
	}
	
	/**
	 * Cette fonction combine les deux fonctions gameReset � un param�tre. Elle prend en compte le niveau et la connexion au serveur.
	 * @param online
	 * @param level
	 */
	
	public void gameReset(boolean online, Level level) {
		this.online = online;
		gameReset(level);
	}
	
	/**
	 * Cette fonction, sans param�tre, r�initialise le jeu dans les m�mes conditions que la partie pr�c�dente.
	 */
	
	public void gameReset() {
		gameReset(level);
	}
	
	/**
	 * Cette fonction calcule le score du joueur, quand il n'est pas connect� au serveur. Elle prend le score d�j� existant, puis lui ajoute le nombre de mines autour de la case cliqu�e.
	 * @param xPos
	 * @param yPos
	 */
	
	public void score(int xPos, int yPos) {
		int revealedCases;
		int hiddenCases = 0;
		for (int x=0; x<c.getDimX(); x++) {
			for (int y=0; y<c.getDimY(); y++) {
				if(states[x][y] == State.HIDDEN || states[x][y] == State.FLAGGED) hiddenCases ++;
			}
		}
		revealedCases = c.getDimX() * c.getDimY() - hiddenCases;
		if(revealedCases >= c.getDimX() * c.getDimY() - c.getNbMines()) {
			//victory
			victory();
		}
		score += c.detectMines(xPos, yPos);
		mainFrame.getScoreLabel().setText("Score : " + String.format("%04d", score));
		if (score > highScores[level.ordinal()]) {
			highScores[level.ordinal()] = score;
			mainFrame.setHighScore(score);
		}
	}
	
	public void victory() {
		System.out.println("Victory");
		victory = true;
		timer.stop();
		setResetButton(VictoryDefeat.VICTORY);
		writeHighScores();
		for (int x = 0; x < getDimX(); x++) {
			for (int y = 0; y < getDimY(); y++){
				if (c.isMine(x, y)) {
					states[x][y] = State.BOMB;
				}
				else {
					int proximityMines = c.detectMines(x, y);
					states[x][y] = State.values()[proximityMines]; // l'index est de z�ro � huit
				}
			}
		}
		mainFrame.draw();
	}

	/**
	 * Cette fonction retourne la largeur de la grille.
	 * @return 
	 */
	public int getDimX() {
		return c.getDimX();
	}
	
	/**
	 * Cette fonction retourne la hauteur de la grille.
	 * @return
	 */
	public int getDimY() {
		return c.getDimY();
	}
	
	/**
	 * Cette fonction retourne l'�tat actuel de toutes les cases (si elles sont cach�es, cliqu�es, flagg�es, et le nombre de mines autour).
	 * @return
	 */
	public State[][] getStates() {
		return states;
	}
	
	/**
	 * Cette fonction retourne la couleur des cases de la grille, dans le cas o� les joueurs en ligne sont diff�renci�s par des couleurs.
	 * @return
	 */
	public Colors[][] getColors(){
		return colors;
	}
	
	/**
	 * Cette fonction d�finit les �tats des cases de la grille. Elle est appel�e lorsque le joueur est connect� au serveur.
	 * @param states
	 */
	public void setStates(State[][] states) {
		this.states = states;
	}
	
	/**
	 * Cette fonction montre si le client est en mode online ou offline. Elle est appel�e par toutes les classes ayant besoin de v�rifier l'�tat de connexion du jeu.
	 * @return
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * Cette fonction est le c�ur du client. Elle prend plusieurs param�tres importants:
	 * - Si le joueur est en ligne ou non.
	 * - Si la partie est commenc�e gr�ce au bool�en firstclick (si c'est le cas, le timer d�marre et l'�ventuelle mine est d�plac�e)
	 * - Si la case est min�e.
	 * - Combien de mines se situent autour de cette case.
	 * Ensuite, elle change l'�tat de la case en fonction de ces param�tres. 
	 * S'il n'y a aucune mine autour de la case cliqu�e, on rejoue sur les 8 cases adjacentes.
	 * En ligne, elle envoie une commande au serveur, suivie des coordonn�es de la case, pour que le serveur effectue le m�me calcul.
	 * @param xPos
	 * @param yPos
	 */
	
	public void play(int xPos, int yPos) {
		if(victory) return;
		//hors ligne
		if(!online) {
			if(states[xPos][yPos] != State.HIDDEN && states[xPos][yPos] != State.FLAGGED) return; //lorsqu'on joue (clic gauche), la case doit �tre cach�e ou flagg�e

			if(firstclick) {
				timer.start();
			}
			
			//Une mine
			if (c.isMine(xPos, yPos)) {
				if(firstclick) {
					c.changeMine(xPos, yPos);
					System.out.println("Mine d�plac�e");
					play(xPos, yPos); //on rejoue au m�me endroit
				} 
				else {
					for (int x = 0; x < getDimX(); x++) {
						for (int y = 0; y < getDimY(); y++){
							if (c.isMine(x, y)) {
								states[x][y] = State.BOMB;
							}
							else {
								int proximityMines = c.detectMines(x, y);
								states[x][y] = State.values()[proximityMines]; // l'index est de z�ro � huit
							}
						}
					}
					setResetButton(VictoryDefeat.DEFEAT);
					states[xPos][yPos] = State.EXPLODED;
					timer.stop();
					System.out.println("explosion");
					writeHighScores();
				}
			}
			
			//Pas de mine
			else {
				int proximityMines = c.detectMines(xPos, yPos);
				states[xPos][yPos] = State.values()[proximityMines];
				score(xPos, yPos);
				//S'il n'y a rien autour, on rejoue autour.
				if(proximityMines == 0) {
					for (int i = Math.max(xPos-1, 0); i <= Math.min(xPos+1, getDimX()-1); i++) {
						for(int j = Math.max(yPos-1, 0); j<= Math.min(yPos+1, getDimY()-1); j++) {
							play(i,j);
						}
					}
				}
			}
			firstclick = false;
			mainFrame.draw();
		}
		
		//en ligne
		else {
			try {
				dos.writeUTF("/play " + xPos + " " + yPos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Si le clic est un clic droit, on marque la case avec un drapeau. Le nombre de mines affich� est chang� en cons�quence, m�me si la case marqu�e est vide.
	 * En ligne, cette fonction envoie une commande suivie des coordonn�es, pour que le serveur ex�cute la m�me fonction.
	 * @param xPos
	 * @param yPos
	 */
	
	public void flag(int xPos, int yPos) {
		if(victory) return;
		//en ligne
		if(!online) {
			if(states[xPos][yPos] == State.HIDDEN) {
				c.decrementMines();
				states[xPos][yPos] = State.FLAGGED;
			}
			else if (states[xPos][yPos] == State.FLAGGED) {
				c.incrementMines();
				states[xPos][yPos] = State.HIDDEN;
			}
			System.out.println("Mines restantes : " + c.getCurrentNbMines());
			mainFrame.draw();
		}
		//hors ligne
		else {
			try {
				dos.writeUTF("/flag "+ xPos + " " + yPos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void doubleClick(int xPos, int yPos) {
		if(victory) return;
		//si on est cach�, flagg�, ou en ligne, on joue pas
		if(states[xPos][yPos] == State.HIDDEN || states[xPos][yPos] == State.FLAGGED || online) return;
		int countFlags = 0;
		//comptage de drapeaux
		for (int i = Math.max(xPos-1, 0); i <= Math.min(xPos+1, getDimX()-1); i++) {
			for(int j = Math.max(yPos-1, 0); j<= Math.min(yPos+1, getDimY()-1); j++) {
				if(states[i][j] == State.FLAGGED) countFlags ++;
			}
		}
		//si le nb de mines �gale le nb de drapeaux, on joue autour
		if(c.detectMines(xPos, yPos) == countFlags) {
			System.out.println("Double clicked.");
			for (int i = Math.max(xPos-1, 0); i <= Math.min(xPos+1, getDimX()-1); i++) {
				for(int j = Math.max(yPos-1, 0); j<= Math.min(yPos+1, getDimY()-1); j++) {
					if(states[i][j] == State.HIDDEN) {
						play(i, j);
					}
				}
			}
		}
	}
	
	/**
	 * Cette fonction change l'�tat et la couleur de la case aux coordonn�es xPos et yPos, selon l'�tat et la couleur qu'on lui donne. 
	 * @param xPos
	 * @param yPos
	 * @param stateString
	 * @param colorString
	 */
	
	public void reveal(int xPos, int yPos, String stateString, String colorString) {
		switch(stateString) {
		case "ZERO":
			states[xPos][yPos] = State.ZERO;
			break;
		case "ONE":
			states[xPos][yPos] = State.ONE;
			break;
		case "TWO":
			states[xPos][yPos] = State.TWO;
			break;
		case "THREE":
			states[xPos][yPos] = State.THREE;
			break;
		case "FOUR":
			states[xPos][yPos] = State.FOUR;
			break;
		case "FIVE":
			states[xPos][yPos] = State.FIVE;
			break;
		case "SIX":
			states[xPos][yPos] = State.SIX;
			break;
		case "SEVEN":
			states[xPos][yPos] = State.SEVEN;
			break;
		case "EIGHT":
			states[xPos][yPos] = State.EIGHT;
			break;
		case "BOMB":
			states[xPos][yPos] = State.BOMB;
			break;
		case "EXPLODED":
			states[xPos][yPos] = State.EXPLODED;
			break;
		case "FLAGGED":
			states[xPos][yPos] = State.FLAGGED;
			break;
		case "HIDDEN":
			states[xPos][yPos] = State.HIDDEN;
			break;
		default:
			states[xPos][yPos] = State.EXPLODED;
		}
		
		switch(colorString) {
		case "CUSTOM_1": 
			colors[xPos][yPos] = Colors.CUSTOM_1;
			break;
		case "CUSTOM_2": 
			colors[xPos][yPos] = Colors.CUSTOM_2;
			break;
		case "CUSTOM_3": 
			colors[xPos][yPos] = Colors.CUSTOM_3;
			break;
		case "CUSTOM_4": 
			colors[xPos][yPos] = Colors.CUSTOM_4;
			break;
		case "CUSTOM_5": 
			colors[xPos][yPos] = Colors.CUSTOM_5;
			break;
		default:
			colors[xPos][yPos] = Colors.DEFAULT;
		}
		
		mainFrame.draw(states.length, states[0].length, states, colors);
	}
	
	/**
	 * Cette fonction run est lanc�e d�s que le joueur se connecte au serveur, dans le thread d'�coute du serveur.
	 * Son unique but est de lire les messages venant du serveur, de les d�couper, puis d'appeler les fonctions correspondant aux commandes re�ues.
	 * Il existe plusieurs commandes:
	 * - /whatsyourname : le serveur demande le nom du client, le client lui envoie son nom.
	 * - /maxlpayers : la partie compte d�j� le nombre maximum de joueurs. Le client se d�connecte.
	 * - /success : La connexion est r�ussie. Le client r�initialise son interface graphique et ses variables pour se conformer au jeu en ligne.
	 * - /timer : Le client met son interface graphique � jour, avec le temps que lui envoie le serveur.
	 * - /score : Le serveur envoie le score d'un joueur particulier. Le client se met � jour pour afficher le nouveau score sur l'interface.
	 * - /setNames et /connected : Le client re�oit les noms des autres joueurs connect�s. Pour /connected, le client signale au joueur l'arriv�e d'un autre joueur.
	 * - /removePlayer : enl�ve un joueur de la liste, souvent � cause d'une d�connexion.
	 * - /reveal : Le client re�oit l'�tat et la couleur de la case en ligne. Ensuite, la fonction reveal est appel�e.
	 * - /gameover : Le client a perdu. Le bouton reset change de t�te.
	 * - /victory : La grille a �t� nettoy�e. Le bouton reset change de t�te.
	 * - /setGrid : Le client re�oit les caract�ristiques de la grille du serveur.
	 */
	
	@Override
	public void run() {
		
		//Listening to server
		while (online) {
			try {
				String message = dis.readUTF();
				String[] splittedMessage = message.split(" ");
				String command = splittedMessage[0];
				//System.out.println(message);
				if (command.equals("/whatsyourname")) dos.writeUTF("/name " + name);
				else if (command.equals("/maxplayers")) {
					//d�j� trop de joueurs
				}
				else if (command.equals("/success")) {
					elapsedTime = 0;
					seconds = 0;
					mainFrame.getCounterLabel().setText("Time : " + String.format("%03d", seconds));
					mainFrame.getServerButton().setButton(true);
					setResetButton(VictoryDefeat.IDLE);
				}
				else if (command.equals("/timer")) {
					String time = splittedMessage[1];
					mainFrame.getCounterLabel().setText("Time : " + time);
				}
				else if (command.equals("/score")) {
					int score = Integer.parseInt(splittedMessage[1]);
					int index = Integer.parseInt(splittedMessage[2]);
					StringBuffer sb = new StringBuffer();
					for (int i=3; i<splittedMessage.length; i++) {
						sb.append(splittedMessage[i]);
						if (i <splittedMessage.length-1) sb.append(" ");
					}
					String player = sb.toString();
					mainFrame.setScore(score, index);
					if(player.equals(name) && score > highScores[ONLINE_HIGH_SCORE_INDEX]) {
						highScores[ONLINE_HIGH_SCORE_INDEX] = score;
						mainFrame.setHighScore(score);
					}
				}
				else if (command.equals("/flag")) {
					int xPos = Integer.parseInt(splittedMessage[1]);
					int yPos = Integer.parseInt(splittedMessage[2]);
					int nbMines = Integer.parseInt(splittedMessage[3]);
					reveal(xPos, yPos, State.FLAGGED.name(), Colors.DEFAULT.name());
				}
				else if (command.equals("/unflag")) {
					int xPos = Integer.parseInt(splittedMessage[1]);
					int yPos = Integer.parseInt(splittedMessage[2]);
					int nbMines = Integer.parseInt(splittedMessage[3]);
					reveal(xPos, yPos, State.HIDDEN.name(), Colors.DEFAULT.name());
				}
				else if (command.equals("/setNames")) {
					StringBuffer sb = new StringBuffer();
					for (int i=2; i<splittedMessage.length; i++) {
						sb.append(splittedMessage[i]);
						if (i <splittedMessage.length-1) sb.append(" ");
					}
					String player = sb.toString();
					int index = Integer.parseInt(splittedMessage[1]);
					mainFrame.addPlayer(player, index);
				}
				else if (command.equals("/connected")) {
					StringBuffer sb = new StringBuffer();
					for (int i=2; i<splittedMessage.length; i++) {
						sb.append(splittedMessage[i]);
						if (i <splittedMessage.length-1) sb.append(" ");
					}
					String player = sb.toString();
					int index = Integer.parseInt(splittedMessage[1]);
					mainFrame.addPlayer(player, index);
					System.out.println("Welcome to " + player);
				}
				else if(command.equals("/removePlayer")) {
					int index = Integer.parseInt(splittedMessage[1]);
					StringBuffer sb = new StringBuffer();
					for (int i=2; i<splittedMessage.length; i++) {
						sb.append(splittedMessage[i]);
						if (i <splittedMessage.length-1) sb.append(" ");
					}
					String player = sb.toString();
					mainFrame.removePlayer(index);
					System.out.println(player + " was disconnected.");
				}
				else if (command.equals("/reveal")) {
					int xPos = Integer.parseInt(splittedMessage[1]);
					int yPos = Integer.parseInt(splittedMessage[2]);
					String stateString = splittedMessage[3];
					String colorString = splittedMessage[4];
					reveal(xPos, yPos, stateString, colorString);
				}
				else if (command.equals("/gameover")) {
					//gameover
					setResetButton(VictoryDefeat.DEFEAT);
				}
				else if (command.equals("/victory")) {
					setResetButton(VictoryDefeat.VICTORY);
				}
				else if(command.equals("/setGrid")) {
					int sizeX = Integer.parseInt(splittedMessage[1]);
					int sizeY = Integer.parseInt(splittedMessage[2]);
					System.out.println("Taille de la grille: " + sizeX + " y: " + sizeY);
					states = new State[sizeX][sizeY];
					colors = new Colors[sizeX][sizeY];
					for (int x = 0; x < sizeX; x++)
						for (int y = 0; y < sizeY; y++) {
							states[x][y] = State.HIDDEN;
							colors[x][y] = Colors.DEFAULT;
						}
					mainFrame.initCases(sizeX, sizeY);
					mainFrame.refresh();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				offline();
				gameReset(Level.EASY);
				mainFrame.getServerButton().setButton(false);
				mainFrame.setOfflineDisplay();
				mainFrame.refresh();
			}
		}
		
	}
}
