package com.lordsofmidnight.main;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.renderer.Renderer;
import com.lordsofmidnight.server.ClientLobbySession;
import com.lordsofmidnight.server.ServerGameplayHandler;
import com.lordsofmidnight.server.ServerLobby;
import com.lordsofmidnight.server.telemeters.DumbTelemetry;
import com.lordsofmidnight.server.telemeters.HostTelemetry;
import com.lordsofmidnight.server.telemeters.Telemetry;
import com.lordsofmidnight.ui.GameSceneController;
import com.lordsofmidnight.ui.MenuController;
import com.lordsofmidnight.utils.Input;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.Settings;
import com.lordsofmidnight.utils.enums.Direction;
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
    mainMenu = new Scene(menuController, Settings.getxResolution(), Settings.getyResolution());
    canvas = new Canvas(Settings.getxResolution(), Settings.getyResolution());
    this.gameSceneController = new GameSceneController(canvas, this);
    this.gameScene = new Scene(gameSceneController.getGameRoot());
    this.gameScene.getStylesheets()
        .add(getClass().getResource("/ui/stylesheet.css").toExternalForm());
    GraphicsContext gc = canvas.getGraphicsContext2D();
    renderer = new Renderer(gc, Settings.getxResolution(), Settings.getyResolution(),
        resourceLoader);
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
    this.menuController.scaleImages(1100, 1920);
    updateResolution();
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
  public void leaveLobby() {
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
    } else {
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
    Point.setMap(m);
  }

  /**
   * Updates the current screen resolution
   */
  public void updateResolution() {
    primaryStage.setWidth(Settings.getxResolution());
    primaryStage.setHeight(Settings.getyResolution());

    canvas.setWidth(Settings.getxResolution());
    canvas.setHeight(Settings.getyResolution());

    //old method to change resolution
    //com.lordsofmidnight.renderer.setResolution(xRes, yRes, this.renderingMode);

    renderer.refreshSettings();
  }

  public void updateTheme(String themeName) {
    Settings.setTheme(themeName);
    renderer.refreshSettings();
    resourceLoader.refreshSettings();
  }

  /**
   * Sets the name for the current client and checks that it contains letters
   *
   * @param n the name of the client
   */
  public void setName(String n) {
    //not sure why this updateResolution is here
    //updateResolution(this.screenRes);
    if (n.matches(".*[a-zA-Z]+.*")) {
      this.name = n;
    } else {
      this.name = "Joe Bloggs";
    }
  }

  /**
   * Sets the initial MIP ID
   *
   * @param id the initial ID
   */
  public void setMIP(int id) {
    this.MIPID = id;
  }

  /**
   * Handles starting the game for all clients
   */
  private void startGame() {
    updateResolution();
    if (telemetry != null) {
      agents = telemetry.getAgents();
      map = telemetry.getMap();
      pellets = telemetry.getPellets();
    }

    for (int i = 0; i < agents.length; i++) {
      if (!(playerNames[i] == null) && !playerNames[i].equals("null")) {
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
   * Sends the user key press to telemetry (via com.lordsofmidnight.server in multiplayer)
   *
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
   *
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
   *
   * @return the current resource loader instance
   */
  public ResourceLoader getResourceLoader() {
    return this.resourceLoader;
  }

  /**
   * Gets all current agents
   *
   * @return the array of agents in the game
   */
  public Entity[] getAgents() {
    return this.agents;
  }

  /**
   * Gets the map currently being used
   *
   * @return the map in use
   */
  public Map getMap() {
    return this.map;
  }
}
