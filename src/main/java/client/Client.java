package client;

import audio.AudioController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import server.Telemetry;
import ui.MenuController;
import utils.Input;
import utils.enums.Direction;

public class Client extends Application {

  private int id;
  private KeyController keyController;
  private Telemetry telemetry;
  private AudioController audioController;

  public int getId() {
    return id;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    int id = 0; // This will be changed if client joins a lobby, telemetry will give it new id
    audioController = new AudioController();
    keyController = new KeyController();
    Scene dummyScene = new Scene(new Label("place holder"), 1920, 1080);
    MenuController menuController = new MenuController(audioController, primaryStage, dummyScene);
    StackPane root = (StackPane) menuController.createMainMenu();
    Scene scene = new Scene(root, 1920, 1080);
    scene.setOnKeyPressed(keyController);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Main menu code will be here

    // If hosting if not telemetry will be set by connection method along with new client id
    telemetry = new Telemetry();


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
    Direction current = telemetry.getEntity(id).getDirection();
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
      telemetry.addInput(input);
    } else {
      //TODO integrate with netwroking to send to telemetry
    }
  }

  private void render() {
    // TODO put render code here pass in either scene or graphics content
  }
}
