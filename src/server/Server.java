package server;

import utils.Input;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

	private BlockingQueue<Input> inputs;
	
	public Server() {
		inputs = new LinkedBlockingQueue<>();
		
	}
	
	public void addInput(Input in) {
		inputs.add(in);
	}
	
}
