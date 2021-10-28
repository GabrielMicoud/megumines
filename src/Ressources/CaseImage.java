package Ressources;

import util.State;

public class CaseImage {
	public static final String[] IMAGES = {"img/online_0.png", "img/online_1.png", "img/online_2.png", "img/online_3.png", "img/online_4.png", "img/online_5.png", "img/online_6.png", "img/online_7.png", "img/online_8.png"};
	public static final String UNKNOWN = "img/online_hidden.png";
	public static final String MOUSE_UNKNOWN = "img/online_hidden_mouse.png";
	public static final String MINE = "img/online_mine.png";
	public static final String GAME_OVER = "img/online_explosion.png";
	public static final String FLAG = "img/online_flag.png";
	public static String getRessourceForState(State s) {
		switch(s) {
		case ZERO:
			return IMAGES[0];
		case ONE:
			return IMAGES[1];
		case TWO:
			return IMAGES[2];
		case THREE:
			return IMAGES[3];
		case FOUR:
			return IMAGES[4];
		case FIVE:
			return IMAGES[5];
		case SIX:
			return IMAGES[6];
		case SEVEN:
			return IMAGES[7];
		case EIGHT:
			return IMAGES[8];
		case HIDDEN:
			return UNKNOWN;
		case BOMB:
			return MINE;
		case EXPLODED:
			return GAME_OVER;
		case FLAGGED:
			return FLAG;
		default:
			return UNKNOWN;
		}
	}
}
