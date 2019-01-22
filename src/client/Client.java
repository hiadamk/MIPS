package client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.enums.Controls;


public class Client extends Application{
	
	public int getId() {
		return id;
	}
	
	private int id;
	private KeyController keyController;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		int id = 0; //This will be changed if client joins a lobby, server will give it new id
		keyController = new KeyController();
		Group root = new Group();
		Scene scene = new Scene(root, 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		scene.setOnKeyPressed(keyController);
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInput();
			}
		}.start();
	}
	
	public void processInput(){
		if(keyController.getActiveKey() == null) return;
		switch (keyController.getActiveKey()) {
			case UP: System.out.println("Direction up");break;
			case DOWN: System.out.println("Direction down");break;
			case LEFT: System.out.println("Direction left");break;
			case RIGHT: System.out.println("Direction right");break;
		}
		
	}
	
}
