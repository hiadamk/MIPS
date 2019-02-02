package ui;

import audio.AudioController;
import audio.Sounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * @author Adam Kona
 * Class which handles the creation and functionality of components in the main menu.
 */
public class MenuController {
    
    private AudioController audioController;
    private Client client;
    
    private boolean viewSettings = false;
    private boolean soundFX = true;
    private boolean music = true;
    private Stage primaryStage;
    private Stack backTree = new Stack();
    private ArrayList<Node> itemsOnScreen = new ArrayList<>();
    
    private Button singlePlayerBtn;
    private Button multiplayerBtn;
    private Button startGameBtn;
    private Button backBtn;
    private Button playBtn;
    private Button musicBtn;
    private Button soundFxBtn;
    private Button creditsBtn;
    private Button settingsBtn;
    private Button quitBtn;
    private Button createGameBtn;
    private Button joinGameBtn;
    private Button createLobbyBtn;
    private ImageView volumeImg;
    private Button incrVolumeBtn;
    private Button decrVolumeBtn;
    private boolean isHome = true;
    
    /**
     * Constructor takes in the audio controller stage and game scene
     *
     * @param audio     Global audio controller which is passed around the system
     * @param stage     The game window
     */
    public MenuController(AudioController audio, Stage stage, Client client) {
        this.audioController = audio;
        this.primaryStage = stage;
        this.client = client;
        
    }
    
    /**
     * Hides the components on the screen
     */
    private void hideItemsOnScreen() {
        for (Node item : itemsOnScreen) {
            item.setVisible(false);
        }
    }
    
    /**
     * Hides the items currently on the screen and moves them onto the stack which will store which components were previously showing
     */
    private void moveItemsToBackTree() {
        hideItemsOnScreen();
        List<Node> components = new ArrayList<>(itemsOnScreen);
        backTree.push(components);
        itemsOnScreen.clear();
    }
    
    /**
     * Shows items on the screen which have been previously set to hidden.
     */
    private void showItemsOnScreen() {
        for (Node item : itemsOnScreen) {
            item.setVisible(true);
        }
    }
    
    
    /**
     * Creates all the menu items and defines their functionality.
     *
     * @return The node containing the menu which will be returned to the game main window.
     */
    public Node createMainMenu() {
        StackPane root = new StackPane();
        root.setPrefSize(1920, 1080);
        
        
        ImageView bg = new ImageView("ui/EditedBackground.png");
        bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
        root.getChildren().add(bg);
        StackPane.setAlignment(bg, Pos.CENTER);
        
        
        startGameBtn = new Button();
        StackPane.setAlignment(startGameBtn, Pos.CENTER);
        StackPane.setMargin(startGameBtn, new Insets(160, 0, 0, 0));
        Image startImg = new Image("ui/start.png");
        startGameBtn.setGraphic(new ImageView(startImg));
        startGameBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(startGameBtn);
        startGameBtn.setVisible(false);
        startGameBtn.setOnAction(e -> {
            audioController.playSound(Sounds.click);
//            primaryStage.setScene(gameScene);
            client.startSinglePlayerGame();
        });
        
        this.singlePlayerBtn = new Button();
        StackPane.setAlignment(this.singlePlayerBtn, Pos.CENTER);
        StackPane.setMargin(this.singlePlayerBtn, new Insets(160, 0, 0, 0));
        Image singleplayerImg = new Image("ui/Single-Player.png");
        this.singlePlayerBtn.setGraphic(new ImageView(singleplayerImg));
        this.singlePlayerBtn.setStyle("-fx-background-color: transparent;");
        this.singlePlayerBtn.setOnAction(e -> {
            audioController.playSound(Sounds.click);
            moveItemsToBackTree();
            itemsOnScreen.add(startGameBtn);
            startGameBtn.setVisible(true);
        });
        
        root.getChildren().add(this.singlePlayerBtn);
        this.singlePlayerBtn.setVisible(false);
        
        this.multiplayerBtn = new Button();
        StackPane.setAlignment(this.multiplayerBtn, Pos.CENTER);
        StackPane.setMargin(this.multiplayerBtn, new Insets(320, 0, 0, 0));
        Image multiplayerImg = new Image("ui/Multiplayer.png");
        this.multiplayerBtn.setGraphic(new ImageView(multiplayerImg));
        this.multiplayerBtn.setStyle("-fx-background-color: transparent;");
        this.multiplayerBtn.setOnAction(e -> {
            audioController.playSound(Sounds.click);
            moveItemsToBackTree();
            itemsOnScreen.add(createGameBtn);
            itemsOnScreen.add(joinGameBtn);
            showItemsOnScreen();
            
        });
        root.getChildren().add(this.multiplayerBtn);
        this.multiplayerBtn.setVisible(false);
        
        backBtn = new Button();
        StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(backBtn, new Insets(0, 0, 100, 0));
        Image backImg = new Image("ui/back.png");
        backBtn.setGraphic(new ImageView(backImg));
        backBtn.setStyle("-fx-background-color: transparent;");
        backBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            if (backTree.isEmpty()) {
            } else {
                for (Node item : itemsOnScreen) {
                    item.setVisible(false);
                }
                itemsOnScreen.clear();
                ArrayList<Node> toShow = (ArrayList<Node>) backTree.pop();
                for (Node item : toShow) {
                    item.setVisible(true);
                    itemsOnScreen.add(item);
                }
                if (backTree.isEmpty()) {
                    backBtn.setVisible(false);
                    isHome = true;
                }
                
            }
            
        });
        backBtn.setVisible(false);
        root.getChildren().add(backBtn);
        
        playBtn = new Button();
        StackPane.setAlignment(playBtn, Pos.CENTER);
        StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
        Image playImg = new Image("ui/play.png");
        playBtn.setGraphic(new ImageView(playImg));
        playBtn.setStyle("-fx-background-color: transparent;");
        playBtn.setOnAction(e -> {
            audioController.playSound(Sounds.click);
            isHome = false;
            backBtn.setVisible(true);
            moveItemsToBackTree();
            itemsOnScreen.add(singlePlayerBtn);
            itemsOnScreen.add(multiplayerBtn);
            showItemsOnScreen();
        });
        root.getChildren().add(playBtn);
        
        musicBtn = new Button();
        StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(musicBtn, new Insets(0, 0, 25, 0));
        musicBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(musicBtn);
        Image musicOn = new Image("ui/Music-On.png");
        Image musicOff = new Image("ui/Music-Off.png");
        musicBtn.setGraphic(new ImageView(musicOn));
        musicBtn.setVisible(false);
        musicBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            if (music) {
                musicBtn.setGraphic(new ImageView(musicOff));
                music = false;
                audioController.setMusicVolume(0);
            } else {
                musicBtn.setGraphic(new ImageView(musicOn));
                music = true;
                audioController.setMusicVolume(0.5);
            }
            
        });
        
        soundFxBtn = new Button();
        StackPane.setAlignment(soundFxBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(soundFxBtn, new Insets(150, 0, 0, 0));
        soundFxBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(soundFxBtn);
        Image soundFXOn = new Image("ui/SoundFX-On.png");
        Image soundFXOff = new Image("ui/SoundFX-Off.png");
        ImageView fxView = new ImageView(soundFXOn);
        fxView.setFitWidth(500);
        fxView.setPreserveRatio(true);
        soundFxBtn.setGraphic(fxView);
        soundFxBtn.setVisible(false);
        soundFxBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            if (soundFX) {
                fxView.setImage(soundFXOff);
                soundFX = false;
                audioController.setSoundVolume(0);
            } else {
                fxView.setImage(soundFXOn);
                soundFX = true;
                audioController.setSoundVolume(0.5);
            }
        });
    
        volumeImg = new ImageView(new Image("ui/Volume.png"));
        StackPane.setAlignment(volumeImg, Pos.CENTER_LEFT);
        StackPane.setMargin(volumeImg, new Insets(400, 0, 0, 100));
        volumeImg.setVisible(false);
        volumeImg.setPreserveRatio(true);
        root.getChildren().add(volumeImg);
        volumeImg.setFitWidth(250);
    
    
        incrVolumeBtn = new Button();
        StackPane.setAlignment(incrVolumeBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(incrVolumeBtn, new Insets(530, 0, 0, 250));
        incrVolumeBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(incrVolumeBtn);
        ImageView incrView = new ImageView(new Image("ui/increaseVolume.png"));
        incrView.setPreserveRatio(true);
        incrView.setFitWidth(50);
        incrVolumeBtn.setGraphic(incrView);
        incrVolumeBtn.setVisible(false);
        incrVolumeBtn.setOnAction(event -> {
        
            audioController.playSound(Sounds.click);
            audioController.increaseVolume();
        });
    
        decrVolumeBtn = new Button();
        StackPane.setAlignment(decrVolumeBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(decrVolumeBtn, new Insets(530, 0, 0, 150));
        decrVolumeBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(decrVolumeBtn);
        ImageView decrView = new ImageView(new Image("ui/decreaseVolume.png"));
        decrView.setPreserveRatio(true);
        decrView.setFitWidth(50);
        decrVolumeBtn.setGraphic(decrView);
        decrVolumeBtn.setVisible(false);
        decrVolumeBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            audioController.decreaseVolume();
        });
        
        
        creditsBtn = new Button();
        StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));
        creditsBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(creditsBtn);
        Image creditsImg = new Image("ui/Credits.png");
        creditsBtn.setGraphic(new ImageView(creditsImg));
        creditsBtn.setVisible(false);
        
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(11);
        
        settingsBtn = new Button();
        settingsBtn.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
        StackPane.setMargin(settingsBtn, new Insets(50, 0, 0, 50));
        Image settingsImg = new Image("ui/settings.png");
        ImageView settingsView = new ImageView(settingsImg);
        settingsView.setFitHeight(50);
        settingsView.setFitWidth(50);
        settingsView.setPreserveRatio(true);
        settingsBtn.setGraphic(settingsView);
        settingsBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            if (!viewSettings) {
                musicBtn.setVisible(true);
                soundFxBtn.setVisible(true);
                creditsBtn.setVisible(true);
                volumeImg.setVisible(true);
                viewSettings = true;
                incrVolumeBtn.setVisible(true);
                decrVolumeBtn.setVisible(true);
                bg.setEffect(gaussianBlur);
                hideItemsOnScreen();
                backBtn.setVisible(false);
                
            } else {
                musicBtn.setVisible(false);
                soundFxBtn.setVisible(false);
                viewSettings = false;
                creditsBtn.setVisible(false);
                volumeImg.setVisible(false);
                incrVolumeBtn.setVisible(false);
                decrVolumeBtn.setVisible(false);
                bg.setEffect(null);
                showItemsOnScreen();
                if (!isHome) {
                    backBtn.setVisible(true);
                }
            }
    
        });
    
    
        root.getChildren().add(settingsBtn);
        
        quitBtn = new Button();
        StackPane.setAlignment(quitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(quitBtn, new Insets(50, 50, 0, 0));
        quitBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(quitBtn);
        Image quitImg = new Image("ui/quit.png");
        quitBtn.setGraphic(new ImageView(quitImg));
        quitBtn.setOnAction(event -> {
            audioController.playSound(Sounds.click);
            System.exit(0);
        
        });
        
        createGameBtn = new Button();
        StackPane.setAlignment(createGameBtn, Pos.CENTER);
        StackPane.setMargin(createGameBtn, new Insets(50, 0, 0, 0));
        Image createGameImg = new Image("ui/create-game.png");
        createGameBtn.setStyle("-fx-background-color: transparent;");
        ImageView createGameView = new ImageView(createGameImg);
        createGameBtn.setGraphic(createGameView);
        createGameBtn.setVisible(false);
        root.getChildren().add(createGameBtn);
        
        joinGameBtn = new Button();
        StackPane.setAlignment(joinGameBtn, Pos.CENTER);
        StackPane.setMargin(joinGameBtn, new Insets(100, 0, 0, 0));
        joinGameBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(joinGameBtn);
        Image joinGameImg = new Image("ui/join-game.png");
        ImageView joinGameView = new ImageView(joinGameImg);
        joinGameBtn.setGraphic(joinGameView);
        joinGameBtn.setVisible(false);
        
        createLobbyBtn = new Button();
        StackPane.setAlignment(createGameBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(createGameBtn, new Insets(0, 0, 300, 0));
        createLobbyBtn.setStyle("-fx-background-color: transparent;");
        Image createLobbyImg = new Image("ui/Create-Lobby.png");
        ImageView createLobbyView = new ImageView(createLobbyImg);
        createLobbyBtn.setGraphic(createLobbyView);
        createLobbyBtn.setVisible(false);
        root.getChildren().add(createLobbyBtn);
        
        backTree.empty();
        itemsOnScreen.add(playBtn);
        
        return root;
    }
}
