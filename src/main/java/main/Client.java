package main;

import audio.AudioController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Entity;
import objects.Pellet;
import renderer.Renderer;
import server.*;
import ui.MenuController;
import utils.Input;
import utils.Map;
import utils.Methods;
import utils.ResourceLoader;
import utils.enums.Direction;
import utils.enums.RenderingMode;
import utils.enums.ScreenResolution;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Application {
    
    Map map;
    private int id;
    private String name;
    private String[] playerNames;
    private KeyController keyController;
    //  private Telemetry telemetry;
    private Telemeters telemetry;
    private AudioController audioController;
    private Scene gameScene;
    private Stage primaryStage;
    private Renderer renderer;
    private int xRes = 1920;
    private int yRes = 1080;
    private ResourceLoader resourceLoader;
    private Entity[] agents;
    private Queue<Input> inputs;
    private ServerLobby server;
    private ServerGameplayHandler serverGameplayHandler;
    private ClientLobbySession clientLobbySession;
    private Queue<String> clientIn;
    private Queue<Input> keypressQueue;
    private boolean isHost;
    private boolean singlePlayer = false;
    private BlockingQueue<Input> incomingQueue; //only used in singleplayer
    private HashMap<String, Pellet> pellets;
    
    public int getId() {
        return id;
    }
    
    public String[] getPlayerNames() {
        return this.playerNames;
    }
    
    public void setId(int id) {
        this.id = id;
        this.renderer.setClientID(id);
    }
    
    public void setPlayerNames(String[] names) {
        this.playerNames = names;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
        xRes = screenRes.width;
        yRes = screenRes.height;
        
        int id = 0; // This will be changed if main joins a lobby, telemetry will give it new id
        audioController = new AudioController();
        keyController = new KeyController();
        resourceLoader = new ResourceLoader("src/main/resources/");
        this.primaryStage = primaryStage;
        //        audioController.playMusic(Sounds.intro);
        MenuController menuController = new MenuController(audioController, primaryStage, this);
        StackPane root = (StackPane) menuController.createMainMenu();
        Scene scene = new Scene(root, xRes, yRes);
        final Canvas canvas = new Canvas(xRes, yRes);
        Group gameRoot = new Group();
        gameRoot.getChildren().add(canvas);
        this.gameScene = new Scene(gameRoot);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        renderer = new Renderer(gc, xRes, yRes, resourceLoader);
        primaryStage.setScene(scene);
        primaryStage
                .widthProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            menuController.scaleImages((double) newVal, (double) oldVal);
                        });
        
        primaryStage.show();
        updateResolution(ScreenResolution.LOW);
    }
    
    public void startSinglePlayerGame() {
        
        singlePlayer = true;
        System.out.println("Starting single player game...");
        map = resourceLoader.getMap();
        
        incomingQueue = new LinkedBlockingQueue<>();
        this.telemetry = new Telemetry(map, incomingQueue, resourceLoader);
        this.primaryStage.setScene(gameScene);
        this.id = 0;
        
        gameScene.setOnKeyPressed(keyController);
        
        startGame();
    }
    
    public void createMultiplayerLobby() {
        System.out.println("Created multiplayer lobby");
        isHost = true;
        
        clientIn = new LinkedList<>();
        //outgoing keypressQueue for local client
        keypressQueue = new LinkedBlockingQueue<>();
        try {
            this.server = new ServerLobby();
            clientLobbySession = new ClientLobbySession(clientIn, keypressQueue, this, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void joinMultiplayerLobby() {
        map = resourceLoader.getMap();
        isHost = false;
        BlockingQueue<String> clientIn = new LinkedBlockingQueue<String>();
        BlockingQueue<Input> keypressQueue = new LinkedBlockingQueue<Input>();
        try {
            
            clientLobbySession = new ClientLobbySession(clientIn, keypressQueue, this, name);
            this.telemetry = new DumbTelemetry(map, clientIn, resourceLoader);
            //waits for game to start
            while (!clientLobbySession.isGameStarted()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.primaryStage.setScene(gameScene);
            gameScene.setOnKeyPressed(keyController);
            startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void startMultiplayerGame() {
        
        if (isHost) {
            System.out.println("Starting multiplayer for host");
            BlockingQueue<Input> inputQueue = new LinkedBlockingQueue<Input>();
            BlockingQueue<String> outputQueue = new LinkedBlockingQueue<String>();
            ServerGameplayHandler s = server.gameStart(inputQueue, outputQueue);
            
            int playerCount = server.getPlayerCount();
            System.out.println("PLAYER COUNT IS: " + playerCount);
            map = resourceLoader.getMap();
            this.telemetry = new Telemetry(this.map, playerCount, inputQueue, outputQueue, this.resourceLoader);
            map = resourceLoader.getMap();
            gameScene.setOnKeyPressed(keyController);
            startGame();
        }
    }
    
    public void setMap(Map m) {
        this.map = m;
    }
    
    public void updateResolution(ScreenResolution s) {
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
        renderer.setResolution(xRes, yRes, RenderingMode.INTEGER_SCALING);
        //        for (Entity e : agents) {
        //            e.updateImages(resourceLoader);
        //        }
    }
    
    public void setName(String n) {
        if (n.matches(".*[a-zA-Z]+.*")) {
            this.name = n;
        } else {
            this.name = "Player " + this.getId();
        }
    }
    
    private void startGame() {
        // inputs = new Queue<Input>();
        
        // agents = new Entity[1];
        // agents[0] = new Entity(true, 0, new Double(0.5, 0.5));
        // agents[1] = new Entity(false, 1, new Double(0.5, 1.5));
        // agents[2] = new Entity(false, 2, new Double(0.5, 1.5));
        // agents[3] = new Entity(false, 3, new Double(0.5, 1.5));
        // agents[4] = new Entity(false, 4, new Double(0.5, 1.5));
        if (telemetry != null) {
            agents = telemetry.getAgents();
            map = telemetry.getMap();
            pellets = telemetry.getPellets();
        }
        Methods.updateImages(agents, resourceLoader);
        this.primaryStage.setScene(gameScene);
        // AnimationTimer started once game has started
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                processInput();
                renderer.render(map, agents, now, pellets);
            }
        }.start();
        telemetry.startAI();
    }
    
    private void processInput() {
        Direction input = keyController.getActiveKey();
        Direction current = agents[id].getDirection();
        if (input == null || input == current) {
            return;
        }
        //    System.out.println(input.toString() + "     " + current + " ID: " + id);
        if (!Methods.validiateDirection(input, agents[0], map)) {
            return;
        }
        switch (input) {
            case UP: // Add code here
                informServer(new Input(this.id, Direction.UP));
                agents[id].setDirection(input);
                break;
            case DOWN: // Add code here
                informServer(new Input(this.id, Direction.DOWN));
                agents[id].setDirection(input);
                break;
            case LEFT: // Add code here
                informServer(new Input(this.id, Direction.LEFT));
                agents[id].setDirection(input);
                break;
            case RIGHT: // Add code here
                informServer(new Input(this.id, Direction.RIGHT));
                agents[id].setDirection(input);
                break;
        }
        if (clientIn == null) {
            return;
        }
        while (!clientIn.isEmpty()) {
            String in = clientIn.poll();
            String code = in.substring(0, 4);
            String rest = in.substring(4);
            String position = rest.substring(rest.length() - 14);
            double x = Double.valueOf(position.substring(0, 7));
            double y = Double.valueOf(position.substring(7));
            switch (code) {
                case "POS0":
                    break;
                case "POS1":
                    break;
                case "COL0":
                    break;
                case "COL1":
                    break;
            }
        }
    }
    
    private void informServer(Input input) {
        if (singlePlayer) {
            incomingQueue.add(input);
        } else {
            System.out.println("TRIED TO SEND MY KEY MOVEMENT");
            keypressQueue.add(input);
            
        }
    }
    
    //communicates to slients
    private void stopMultiplayerGame() {
        if (isHost) {
            server.gameStop();
            //add some stuff that stops rendering the game?
        }
        //TODO add options in future for client to quit a game
    }

//    private void render() {
//        renderer.render(map, agents);
//    }
}
