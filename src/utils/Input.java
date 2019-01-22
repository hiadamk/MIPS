package utils;

import utils.enums.Controls;

public class Input {
	
	private int clientID;
	private Controls move;
	
	public int getClientID() {
		return clientID;
	}
	
	public Controls getMove() {
		return move;
	}
	
	public  Input(int id, Controls move) {
		this.clientID = id;
		this.move = move;
	}
}
