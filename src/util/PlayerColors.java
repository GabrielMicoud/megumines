package util;

import java.awt.Color;

//couleurs désaturées
public enum PlayerColors {
	PLAYER_1("#7FB3D5"), PLAYER_2("#F8C471"), PLAYER_3("#F1948A"), PLAYER_4("#84D0EB"), PLAYER_5("#DFE1E1"), DEFAULT();
	
	String defaultColor = "#616A6B";
	public Color color;
	
	PlayerColors(String hex) {
		this.color = Color.decode(hex);
	}
	
	PlayerColors(){
		this.color = Color.decode(defaultColor);
	}
}
