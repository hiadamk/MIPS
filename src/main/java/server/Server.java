package server;

import javafx.animation.AnimationTimer;
import utils.Input;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

	private BlockingQueue<Input> inputs;
	
	public Server() {
		inputs = new LinkedBlockingQueue<>();
		makeConnections();
		startGame();
	}
	
	private void makeConnections() {
		//TODO implement
	}
	
	public void addInput(Input in) {
		inputs.add(in);
	}
	
	public void startGame() {
		//TODO implement
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInputs();
				informClients();
				processPhysics();
				updateClients();
			}
		}.start();
	}
	
	private void processInputs() {
		//TODO implement
	}
	
	private void informClients() {
		//TODO implement
	}
	
	private void processPhysics() {
		//TODO implement
	}
	
	private void updateClients() {
		//TODO implement
	}
}
