package client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


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
		//Main menu code will be here
		//AnimationTimer started once game has started
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				processInput();
				render();
			}
		}.start();
	}
	
	public void processInput(){
		if(keyController.getActiveKey() == null) return;
		switch (keyController.getActiveKey()) {
			case UP: //Add code here
				System.out.println("Direction up");break;
			case DOWN: //Add code here
				System.out.println("Direction down");break;
			case LEFT: //Add code here
				System.out.println("Direction left");break;
			case RIGHT: //Add code here
				System.out.println("Direction right");break;
		}
		
	}
	
	public void render() {
		//TODO put render code here pass in either scene or graphics content
	}
}
