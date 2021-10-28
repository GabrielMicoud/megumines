package client;

import util.Level;

/**
 * Fonction principale du client. Elle démarre un nouveau jeu.
 * @author Gabriel Micoud
 *
 */
public class Main {
	Game game;
	/**
	 * Fonction main.
	 * @param args arguments du main
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Game(Level.EASY);
	}
}
