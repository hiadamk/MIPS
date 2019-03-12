package main;

import audio.AudioController;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Entity;
import objects.Pellet;
import renderer.Renderer;
import server.ClientLobbySession;
import server.ServerGameplayHandler;
import server.ServerLobby;
import server.telemeters.DumbTelemetry;
import server.telemeters.HostTelemetry;
import server.telemeters.Telemetry;
import ui.GameSceneController;
import ui.MenuController;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.ResourceLoader;
import utils.enums.Direction;
import utils.enums.RenderingMode;
import utils.enums.ScreenResolution;

public class Client extends Application {

  Map map;
  private int id;
  private String name;
  private String[] playerNames = new String[5];
  private KeyController keyController;
  //  private HostTelemetry telemetry;
  private Telemetry telemetry;
  private AudioController audioController;
  private Scene gameScene;
  private Stage primaryStage;
  private Renderer renderer;
  private int xRes = 1920;
  private int yRes = 1080;
  private ScreenResolution screenRes = ScreenResolution.LOW;
  private RenderingMode renderingMode = RenderingMode.STANDARD_SCALING;
  private ResourceLoader resourceLoader;
  private Entity[] agents;
  private Queue<Input> inputs;
  private ServerLobby server;
  private ServerGameplayHandler serverGameplayHandler;
  private MenuController menuController;
  private ClientLobbySession clientLobbySession;
  private Queue<String> clientIn;
  private Queue<Input> keypressQueue;
  public boolean isHost;
  private boolean singlePlayer = false;
  private BlockingQueue<Input> incomingQueue; // only used in singleplayer
  private HashMap<String, Pellet> pellets;
  private int MIPID;
  private Canvas canvas = new Canvas();
  private AnimationTimer inputRenderLoop;
  private GameSceneController gameSceneController;
  private Scene mainMenu;
  private boolean gameStarted = false;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
    this.renderer.setClientID(id);
  }

  public void setPlayerNames(String[] names) {
    this.playerNames = names;
  }

  public void setRenderingMode(RenderingMode rm) {
    this.renderingMode = rm;
  }

  @Override
  public void start(Stage primaryStage) {
    audioController = new AudioController();
    keyController = new KeyController();
    resourceLoader = new ResourceLoader("src/main/resources/");
    this.primaryStage = primaryStage;
    //    audioController.playMusic(Sounds.intro);
    menuController =
        new MenuController(audioController, primaryStage, this, resourceLoader);
    StackPane menuController = (StackPane) this.menuController.createMainMenu();
    menuController.getStylesheets()
        .add(getClass().getResource("/ui/stylesheet.css").toExternalForm());
    mainMenu = new Scene(menuController, xRes, yRes);
    canvas = new Canvas(xRes, yRes);
    this.gameSceneController = new GameSceneController(canvas, this);
    this.gameScene = new Scene(gameSceneController.getGameRoot());
    this.gameScene.getStylesheets()
        .add(getClass().getResource("/ui/stylesheet.css").toExternalForm());
    GraphicsContext gc = canvas.getGraphicsContext2D();
    renderer = new Renderer(gc, xRes, yRes, resourceLoader);
    primaryStage.setScene(mainMenu);
    primaryStage.setMinWidth(1366);
    primaryStage.setMinHeight(768);
    primaryStage
        .widthProperty()
        .addListener(
            (obs, oldVal, newVal) -> this.menuController
                .scaleImages((double) newVal, (double) oldVal));

    primaryStage.show();
    primaryStage.setOnCloseRequest(e -> System.exit(0));

    updateResolution(this.screenRes);
  }

  /**
   * Starts a single player game for the client
   */
  public void startSinglePlayerGame() {

    singlePlayer = true;
    map = resourceLoader.getMap();

    incomingQueue = new LinkedBlockingQueue<>();
    this.telemetry = new HostTelemetry(incomingQueue, this);
    this.primaryStage.setScene(gameScene);
    this.id = 0;

    gameScene.setOnKeyPressed(keyController);
    startGame();
  }

  /**
   * Creates a multiplayer lobby
   */
  public void createMultiplayerLobby() {
    System.out.println("Created multiplayer lobby");
    isHost = true;

    clientIn = new LinkedList<>();
    // outgoing keypressQueue for local client
    keypressQueue = new LinkedBlockingQueue<>();
    try {
      this.server = new ServerLobby();
      clientLobbySession = new ClientLobbySession(clientIn, keypressQueue, this, name);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allows a client to join a lobby
   */
  public void joinMultiplayerLobby() {
    map = resourceLoader.getMap();
    isHost = false;
    BlockingQueue<String> clientIn = new LinkedBlockingQueue<>();
    keypressQueue = new LinkedBlockingQueue<>();
    try {
      clientLobbySession = new ClientLobbySession(clientIn, keypressQueue, this, name);
      this.telemetry = new DumbTelemetry(clientIn, this);
      this.telemetry.setMipID(MIPID);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Allows all clients to safely leave a lobby
   */
  public void leaveLobby(){
    if (!gameStarted) {
      if (isHost) {
        clientLobbySession.leaveLobby();
        server.shutDown();
        setId(0);
        this.telemetry = null;
        this.keypressQueue = null;
        isHost = false;
      } else {
        clientLobbySession.leaveLobby();
        setId(0);
        this.telemetry = null;
        this.keypressQueue = null;
        isHost = false;
      }
    }

  }

  /**
   * Handles starting a game.
   */
  public void startMultiplayerGame() {
    gameStarted = true;
    menuController.endPlayerDiscovery();
    if (isHost) {
      System.out.println("Starting multiplayer for host");
      BlockingQueue<Input> inputQueue = new LinkedBlockingQueue<>();
      BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>();
      serverGameplayHandler = server.gameStart(inputQueue, outputQueue);
      map = resourceLoader.getMap();
      int playerCount = server.getPlayerCount();
      System.out.println("PLAYER COUNT IS: " + playerCount);
      this.telemetry = new HostTelemetry(playerCount, inputQueue, outputQueue, this);
      this.telemetry.setMipID(MIPID);
      gameScene.setOnKeyPressed(keyController);
      startGame();
    }else{
      System.out.println("Starting multiplayer for non-host");
      this.primaryStage.setScene(gameScene);
      gameScene.setOnKeyPressed(keyController);
      startGame();
    }
  }

  /**
   * Sets the current map being used
   *
   * @param m the map to use
   */
  public void setMap(Map m) {
    this.map = m;
  }

  /**
   * Updates the current screen resolution
   * @param s the desired resolution
   */
  public void updateResolution(ScreenResolution s) {
    this.screenRes = s;
    switch (s) {
      case LOW:
        primaryStage.setWidth(1366);
        primaryStage.setHeight(768);
        xRes = 1366;
        yRes = 768;
        break;
      case MEDIUM:
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        xRes = 1920;
        yRes = 1080;
        break;
      case HIGH:
        primaryStage.setWidth(2650);
        primaryStage.setHeight(1440);
        xRes = 2650;
        yRes = 1440;
        break;
    }

    canvas.setWidth(xRes);
    canvas.setHeight(yRes);
    renderer.setResolution(xRes, yRes, this.renderingMode);
  }

  /**
   * Sets the name for the current client and checks that it contains letters
   * @param n the name of the client
   */
  public void setName(String n) {
    updateResolution(this.screenRes);
    if (n.matches(".*[a-zA-Z]+.*")) {
      this.name = n;
    } else {
      this.name = "Joe Bloggs";
    }
  }

  /**
   * Sets the initial MIP ID
   * @param id the initial ID
   */
  public void setMIP(int id) {
    this.MIPID = id;
  }

  /**
   * Handles starting the game for all clients
   */
  private void startGame() {
    updateResolution(this.screenRes);
    if (telemetry != null) {
      agents = telemetry.getAgents();
      map = telemetry.getMap();
      pellets = telemetry.getPellets();
    }

    for (int i = 0; i < agents.length; i++) {
      if(!(playerNames[i] == null) && !playerNames[i].equals("null")){
        agents[i].setName(playerNames[i]);
      }

    }
    this.inputRenderLoop = new AnimationTimer() {
      @Override
      public void handle(long now) {
        processInput();
        renderer.render(map, agents, now, pellets, telemetry.getActivePowerUps(),
            Telemetry.getGameTimer() / 100);

      }
    };
    inputRenderLoop.start();
    this.telemetry.startGame();
    Methods.updateImages(agents, resourceLoader);

    renderer.initMapTraversal(map);
    map = resourceLoader.getMap();
    this.primaryStage.setScene(gameScene);
  }

  /**
   * Handles the closing down of the game session in single player and multiplayer
   */
  public void closeGame() {
    gameScene.setOnKeyPressed(null);
    this.telemetry.stopGame();
    menuController.reset();
    primaryStage.setScene(mainMenu);
    if (singlePlayer) {
      singlePlayer = false;
      incomingQueue = null;
    } else {
      if (isHost) {
        isHost = false;
        server.shutDown();
        serverGameplayHandler.close();
      }
      clientLobbySession.leaveLobby();
    }
  }

  /**
   * Process the players input given in via the keyboard @Author Matthew Jones
   */
  private void processInput() {
    if (keyController.UseItem()) {
      informServer(new Input(this.id, Direction.USE));
      return;
    }
    Direction input = keyController.getActiveKey();
    Direction current = agents[id].getDirection();
    if (input == null || input == current) {
      return;
    }
    //    System.out.println(input.toString() + "     " + current + " ID: " + id);
    if (!Methods.validateDirection(input, agents[id].getLocation(), map)) {
      return;
    }
    switch (input) {
      case UP:
        informServer(new Input(this.id, Direction.UP));
        break;
      case DOWN:
        informServer(new Input(this.id, Direction.DOWN));
        break;
      case LEFT:
        informServer(new Input(this.id, Direction.LEFT));
        break;
      case RIGHT:
        informServer(new Input(this.id, Direction.RIGHT));
        break;
    }
  }

  /**
   * Sends the user key press to telemetry (via server in multiplayer)
   * @param input the current keypress
   */
  private void informServer(Input input) {
    if (singlePlayer) {
      incomingQueue.add(input);
    } else {
      if (getId() == 0) {
        this.telemetry.addInput(input);
      } else {
        keypressQueue.add(input);
      }
    }
  }

  /**
   * Handles pausing the game loops for the MIPs man animation when there is a collision
   * @param newMipsman the new MIPs man
   */
  public void collisionDetected(Entity newMipsman) {
    inputRenderLoop.stop();
    telemetry.getInputProcessor().pause();
    renderer.renderCollisionAnimation(newMipsman, agents, map, inputRenderLoop,
        telemetry.getInputProcessor());
  }

  /**
   * Gets the resource loader being used by the client
   * @return the current resource loader instance
   */
  public ResourceLoader getResourceLoader() {
    return this.resourceLoader;
  }

  /**
   * Gets all current agents
   * @return the array of agents in the game
   */
  public Entity[] getAgents() {
    return this.agents;
  }

  /**
   * Gets the map currently being used
   * @return the map in use
   */
  public Map getMap() {
    return this.map;
  }
}
