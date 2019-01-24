package client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.Server;
import utils.Input;
import utils.enums.Direction;

public class Client extends Application {

  private int id;
  private KeyController keyController;
  private Server server;
  public int getId() {
    return id;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    int id = 0; // This will be changed if client joins a lobby, server will give it new id
    keyController = new KeyController();
    Group root = new Group();
    Scene scene = new Scene(root, 500, 500);
    primaryStage.setScene(scene);
    primaryStage.show();
    scene.setOnKeyPressed(keyController);
    // Main menu code will be here

    // If hosting if not server will be set by connection method along with new client id
    server = new Server();


    // AnimationTimer started once game has started
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        processInput();
        render();
      }
    }.start();
  }

  private void processInput() {
    Direction input = keyController.getActiveKey();
    Direction current = server.getEntity(id).getDirection();
    if (input == null | input == current) {
      return;
    }
    switch (input) {
      case UP: // Add code here
        // Validate the input
        System.out.println("Direction up");
        informServer(new Input(0, Direction.UP));
        break;
      case DOWN: // Add code here
        System.out.println("Direction down");
        informServer(new Input(0, Direction.DOWN));
        break;
      case LEFT: // Add code here
        System.out.println("Direction left");
        informServer(new Input(0, Direction.LEFT));
        break;
      case RIGHT: // Add code here
        System.out.println("Direction right");
        informServer(new Input(0, Direction.RIGHT));
        break;
    }
  }

  private void informServer(Input input) {
    if(id == 0){
      server.addInput(input);
    } else {
      //TODO integrate with netwroking to send to server
    }
  }

  private void moveEntities() {

  }

  private void render() {
    // TODO put render code here pass in either scene or graphics content
  }
}
