package main;

import audio.AudioController;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Entity;
import renderer.Renderer;
import server.Telemetry;
import ui.MenuController;
import utils.Input;
import utils.Map;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Client extends Application {

  private int id;
  private KeyController keyController;
  private Telemetry telemetry;
  private AudioController audioController;
  private Scene gameScene;
  private Stage primaryStage;
  private Renderer renderer;
  private final int xRes = 1920;
  private final int yRes = 1080;
  private ResourceLoader resourceLoader;
  private Entity[] agents;
  Map map;


  public int getId() {
    return id;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    int id = 0; // This will be changed if main joins a lobby, telemetry will give it new id
    audioController = new AudioController();
    keyController = new KeyController();
    resourceLoader = new ResourceLoader("src/test/resources/");
    this.primaryStage = primaryStage;
    //this.gameScene = new Scene(new Label("place holder"), xRes, yRes);
    MenuController menuController = new MenuController(audioController, primaryStage, this);
    StackPane root = (StackPane) menuController.createMainMenu();
    Scene scene = new Scene(root, xRes, yRes);
    final Canvas canvas = new Canvas(xRes, yRes);
    Group gameRoot = new Group();
    root.getChildren().add(canvas);
    this.gameScene = new Scene(gameRoot);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    renderer = new Renderer(gc, xRes, yRes, resourceLoader.getMapTiles() );
    primaryStage.setScene(scene);
    primaryStage.show();

  }
  
  
  public void startSinglePlayerGame() {
    //TODO Implement fully
  
    System.out.println("Starting single player game...");
    // If hosting if not telemetry will be set by connection method along with new main id
    this.telemetry = new Telemetry();
    this.primaryStage.setScene(gameScene);

    gameScene.setOnKeyPressed(keyController);

    startGame();
  }
  
  public void startMultiplayerGame() {
    //TODO Implement
  }
  
  private void startGame() {
    map = resourceLoader.getMap();
    this.primaryStage.setScene(gameScene);
    // AnimationTimer started once game has started
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        processInput();
        Telemetry.processPhysics(agents);
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
      if (id == 0) {
      telemetry.addInput(input);
    } else {
          // TODO integrate with networking to send to telemetry
    }
  }

  private void render() {
    ArrayList<Entity> x = new ArrayList<>();
    for(Entity e : agents){
      x.add(e);
    }
    renderer.render(map, x);
  }
}
