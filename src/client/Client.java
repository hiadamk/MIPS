package client;

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
	}
	
	public void processInput(Controls direction){
		switch (direction) {
			case UP: System.out.println("Direction up");break;
			case DOWN: System.out.println("Direction down");break;
			case LEFT: System.out.println("Direction left");break;
			case RIGHT: System.out.println("Direction right");break;
		}
		
	}
	
}
