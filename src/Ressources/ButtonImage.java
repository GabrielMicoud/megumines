package Ressources;

import util.Level;
import util.VictoryDefeat;

public class ButtonImage {
	public static final String ONLINE = "img/online_button.png";
	public static final String OFFLINE = "img/offline_button.png";
	public static final String IN_GAME = "img/good_reset.png";
	public static final String VICTORY = "img/happy_reset.png";
	public static final String DEFEAT = "img/bad_reset.png";
	public static final String EZ = "img/easy_button.png";
	public static final String MID = "img/medium_button.png";
	public static final String HARD = "img/hard_button.png";
	public static final String QUIT = "img/quit_button.png";
	
	public static String getRessourceOnline(boolean online) {
		if(online == true) return OFFLINE;
		else return ONLINE;
	}
	
	public static String getRessourceLevel(Level level) {
		switch(level) {
		case EASY:
			return EZ;
		case MEDIUM:
			return MID;
		case HARD:
			return HARD;
		default:
			return EZ;
		}
	}
	
	public static String getRessourceButton(VictoryDefeat v ) {
		switch(v) {
		case VICTORY:
			return VICTORY;
		case DEFEAT:
			return DEFEAT;
		case IDLE: 
			return IN_GAME;
		default:
			return IN_GAME;
		}
	}
}
