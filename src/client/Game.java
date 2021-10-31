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
 * Classe principale du client. Il permet de faire fonctionner un jeu en solo, ou un jeu en ligne si le serveur est connecté
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
	 * Constructeur sans paramètre. Il configure automatiquement la difficulté sur EASY.
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
	 * Fonction servant à se connecter au serveur. Il prend en compte une adresse IP, un port, et un pseudonyme qu'on entre dans une fenêtre séparée.
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
	 * Fonction servant à se déconnecter du serveur. Il envoie une notification au serveur, qui supprime le client de sa liste de joueurs.
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
	 * Fonction réinitialisant la grille. Toutes les cases deviennent masquées, les mines sont éparpillées, et la taille de la grille varie en fonction du niveau.
	 * Lorsqu'on est en ligne, cette fonction demande au serveur de réinitialiser la grille globale si le jeu est terminé.
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
			timer.stop(); //si on appuie sur reset en pleine partie, pour être sûr.
		}
	}
	
	/**
	 * Cette fonction modifie l'image du bouton reset, en fonction de l'avancement de la partie, si elle est en cours, gagnée ou perdue.
	 * @param v
	 */
	
	public void setResetButton(VictoryDefeat v) {
		mainFrame.getResetButton().setFace(v);
	}
	
	/**
	 * Cette fonction prend en compte la connexion au serveur. Elle peut être appelée s'il y a eu une déconnexion brutale.
	 * @param online
	 */
	
	public void gameReset(boolean online) {
		this.online = online;
		gameReset();
	}
	
	/**
	 * Cette fonction combine les deux fonctions gameReset à un paramètre. Elle prend en compte le niveau et la connexion au serveur.
	 * @param online
	 * @param level
	 */
	
	public void gameReset(boolean online, Level level) {
		this.online = online;
		gameReset(level);
	}
	
	/**
	 * Cette fonction, sans paramètre, réinitialise le jeu dans les mêmes conditions que la partie précédente.
	 */
	
	public void gameReset() {
		gameReset(level);
	}
	
	/**
	 * Cette fonction calcule le score du joueur, quand il n'est pas connecté au serveur. Elle prend le score déjà existant, puis lui ajoute le nombre de mines autour de la case cliquée.
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
					states[x][y] = State.values()[proximityMines]; // l'index est de zéro à huit
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
	 * Cette fonction retourne l'état actuel de toutes les cases (si elles sont cachées, cliquées, flaggées, et le nombre de mines autour).
	 * @return
	 */
	public State[][] getStates() {
		return states;
	}
	
	/**
	 * Cette fonction retourne la couleur des cases de la grille, dans le cas où les joueurs en ligne sont différenciés par des couleurs.
	 * @return
	 */
	public Colors[][] getColors(){
		return colors;
	}
	
	/**
	 * Cette fonction définit les états des cases de la grille. Elle est appelée lorsque le joueur est connecté au serveur.
	 * @param states
	 */
	public void setStates(State[][] states) {
		this.states = states;
	}
	
	/**
	 * Cette fonction montre si le client est en mode online ou offline. Elle est appelée par toutes les classes ayant besoin de vérifier l'état de connexion du jeu.
	 * @return
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * Cette fonction est le cœur du client. Elle prend plusieurs paramètres importants:
	 * - Si le joueur est en ligne ou non.
	 * - Si la partie est commencée grâce au booléen firstclick (si c'est le cas, le timer démarre et l'éventuelle mine est déplacée)
	 * - Si la case est minée.
	 * - Combien de mines se situent autour de cette case.
	 * Ensuite, elle change l'état de la case en fonction de ces paramètres. 
	 * S'il n'y a aucune mine autour de la case cliquée, on rejoue sur les 8 cases adjacentes.
	 * En ligne, elle envoie une commande au serveur, suivie des coordonnées de la case, pour que le serveur effectue le même calcul.
	 * @param xPos
	 * @param yPos
	 */
	
	public void play(int xPos, int yPos) {
		if(victory) return;
		//hors ligne
		if(!online) {
			if(states[xPos][yPos] != State.HIDDEN && states[xPos][yPos] != State.FLAGGED) return; //lorsqu'on joue (clic gauche), la case doit être cachée ou flaggée

			if(firstclick) {
				timer.start();
			}
			
			//Une mine
			if (c.isMine(xPos, yPos)) {
				if(firstclick) {
					c.changeMine(xPos, yPos);
					System.out.println("Mine déplacée");
					play(xPos, yPos); //on rejoue au même endroit
				} 
				else {
					for (int x = 0; x < getDimX(); x++) {
						for (int y = 0; y < getDimY(); y++){
							if (c.isMine(x, y)) {
								states[x][y] = State.BOMB;
							}
							else {
								int proximityMines = c.detectMines(x, y);
								states[x][y] = State.values()[proximityMines]; // l'index est de zéro à huit
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
	 * Si le clic est un clic droit, on marque la case avec un drapeau. Le nombre de mines affiché est changé en conséquence, même si la case marquée est vide.
	 * En ligne, cette fonction envoie une commande suivie des coordonnées, pour que le serveur exécute la même fonction.
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
		//si on est caché, flaggé, ou en ligne, on joue pas
		if(states[xPos][yPos] == State.HIDDEN || states[xPos][yPos] == State.FLAGGED || online) return;
		int countFlags = 0;
		//comptage de drapeaux
		for (int i = Math.max(xPos-1, 0); i <= Math.min(xPos+1, getDimX()-1); i++) {
			for(int j = Math.max(yPos-1, 0); j<= Math.min(yPos+1, getDimY()-1); j++) {
				if(states[i][j] == State.FLAGGED) countFlags ++;
			}
		}
		//si le nb de mines égale le nb de drapeaux, on joue autour
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
	 * Cette fonction change l'état et la couleur de la case aux coordonnées xPos et yPos, selon l'état et la couleur qu'on lui donne. 
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
	 * Cette fonction run est lancée dès que le joueur se connecte au serveur, dans le thread d'écoute du serveur.
	 * Son unique but est de lire les messages venant du serveur, de les découper, puis d'appeler les fonctions correspondant aux commandes reçues.
	 * Il existe plusieurs commandes:
	 * - /whatsyourname : le serveur demande le nom du client, le client lui envoie son nom.
	 * - /maxlpayers : la partie compte déjà le nombre maximum de joueurs. Le client se déconnecte.
	 * - /success : La connexion est réussie. Le client réinitialise son interface graphique et ses variables pour se conformer au jeu en ligne.
	 * - /timer : Le client met son interface graphique à jour, avec le temps que lui envoie le serveur.
	 * - /score : Le serveur envoie le score d'un joueur particulier. Le client se met à jour pour afficher le nouveau score sur l'interface.
	 * - /setNames et /connected : Le client reçoit les noms des autres joueurs connectés. Pour /connected, le client signale au joueur l'arrivée d'un autre joueur.
	 * - /removePlayer : enlève un joueur de la liste, souvent à cause d'une déconnexion.
	 * - /reveal : Le client reçoit l'état et la couleur de la case en ligne. Ensuite, la fonction reveal est appelée.
	 * - /gameover : Le client a perdu. Le bouton reset change de tête.
	 * - /victory : La grille a été nettoyée. Le bouton reset change de tête.
	 * - /setGrid : Le client reçoit les caractéristiques de la grille du serveur.
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
					//déjà trop de joueurs
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
