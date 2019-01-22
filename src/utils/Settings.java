package utils;

import javafx.scene.input.KeyCode;
import utils.enums.Controls;

public class Settings {
	static private KeyCode up;
	static private KeyCode down;
	static private KeyCode left;
	static private KeyCode right;
	
	public static KeyCode getKey(Controls c) {
		switch (c) {
			case UP: return up;
			case DOWN: return down;
			case LEFT: return left;
			case RIGHT: return right;
			default: System.err.println("None bound key requested"); return null;
		}
		
		
	}
	
	public static void setKey(Controls c, KeyCode key) {
		switch (c) {
			case UP: up = key; break;
			case DOWN: down = key; break;
			case LEFT: left = key; break;
			case RIGHT: right = key; break;
		}
		System.err.println("Invalid key to be bound");
	}
}
