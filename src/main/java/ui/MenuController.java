package ui;

import audio.AudioController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MenuController {
    
    private AudioController audioController;
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
    
    public MenuController(AudioController audio, Stage stage) {
        this.audioController = audio;
        this.primaryStage = stage;
        
    }
    
    private void moveItemsToBackTree() {
        for(Node item : itemsOnScreen){
            item.setVisible(false);
        }
        List<Node> components = new ArrayList<>(itemsOnScreen);
        backTree.push(components);
        itemsOnScreen.clear();
    }
    
    public Node createMainMenu() {
        StackPane root = new StackPane();
        root.setPrefSize(1920, 1080);
        
        
        ImageView bg = new ImageView("menuImages/EditedBackground.png");
        bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
        root.getChildren().add(bg);
        StackPane.setAlignment(bg, Pos.CENTER);
        
        
        startGameBtn = new Button();
        StackPane.setAlignment(startGameBtn, Pos.CENTER);
        StackPane.setMargin(startGameBtn, new Insets(160, 0, 0, 0));
        Image startImg = new Image("menuImages/start.png");
        startGameBtn.setGraphic(new ImageView(startImg));
        startGameBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(startGameBtn);
        startGameBtn.setVisible(false);
        
        
        this.singlePlayerBtn = new Button();
        StackPane.setAlignment(this.singlePlayerBtn, Pos.CENTER);
        StackPane.setMargin(this.singlePlayerBtn, new Insets(160, 0, 0, 0));
        Image singleplayerImg = new Image("menuImages/Single-Player.png");
        this.singlePlayerBtn.setGraphic(new ImageView(singleplayerImg));
        this.singlePlayerBtn.setStyle("-fx-background-color: transparent;");
        this.singlePlayerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                moveItemsToBackTree();
                itemsOnScreen.add(startGameBtn);
                startGameBtn.setVisible(true);
            }
        });
        
        root.getChildren().add(this.singlePlayerBtn);
        this.singlePlayerBtn.setVisible(false);
        
        this.multiplayerBtn = new Button();
        StackPane.setAlignment(this.multiplayerBtn, Pos.CENTER);
        StackPane.setMargin(this.multiplayerBtn, new Insets(320, 0, 0, 0));
        Image multiplayerImg = new Image("menuImages/Multiplayer.png");
        this.multiplayerBtn.setGraphic(new ImageView(multiplayerImg));
        this.multiplayerBtn.setStyle("-fx-background-color: transparent;");
        this.multiplayerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                moveItemsToBackTree();
//                itemsOnScreen.add()
            }
        });
        root.getChildren().add(this.multiplayerBtn);
        this.multiplayerBtn.setVisible(false);
        
        backBtn = new Button();
        StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(backBtn, new Insets(0, 0, 100, 0));
        Image backImg = new Image("menuImages/back.png");
        backBtn.setGraphic(new ImageView(backImg));
        backBtn.setStyle("-fx-background-color: transparent;");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (backTree.isEmpty()) {
//                    backBtn.setVisible(false);
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
//                        itemsOnScreen.add()
                        backBtn.setVisible(false);
                    }
                    
                }
                
            }
        });
        backBtn.setVisible(false);
        root.getChildren().add(backBtn);
//        final Font f = Font.loadFont(new FileInputStream(new File("./src/Utils/ARCADEPI.TTF")), 12);
        
        
        playBtn = new Button();
        StackPane.setAlignment(playBtn, Pos.CENTER);
        StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
        Image playImg = new Image("menuImages/play.png");
        playBtn.setGraphic(new ImageView(playImg));
        playBtn.setStyle("-fx-background-color: transparent;");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                
                backBtn.setVisible(true);
                playBtn.setVisible(false);
                ArrayList<Node> components = new ArrayList<>();
                components.add(playBtn);
                backTree.push(components);
                singlePlayerBtn.setVisible(true);
                multiplayerBtn.setVisible(true);
                itemsOnScreen.clear();
                itemsOnScreen.add(singlePlayerBtn);
                itemsOnScreen.add(multiplayerBtn);
            }
        });
        root.getChildren().add(playBtn);
        
        
        musicBtn = new Button();
        StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(musicBtn, new Insets(0, 0, 25, 0));
        musicBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(musicBtn);
        Image musicOn = new Image("menuImages/Music-On.png");
        Image musicOff = new Image("menuImages/Music-Off.png");
        musicBtn.setGraphic(new ImageView(musicOn));
        musicBtn.setVisible(false);
        musicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (music) {
                    musicBtn.setGraphic(new ImageView(musicOff));
                    music = false;
                } else {
                    musicBtn.setGraphic(new ImageView(musicOn));
                    music = true;
                }
                
            }
        });
        
        soundFxBtn = new Button();
        StackPane.setAlignment(soundFxBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(soundFxBtn, new Insets(150, 0, 0, 0));
        soundFxBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(soundFxBtn);
        Image soundFXOn = new Image("menuImages/SoundFX-On.png");
        Image soundFXOff = new Image("menuImages/SoundFX-Off.png");
        ImageView fxView = new ImageView(soundFXOn);
        fxView.setFitWidth(500);
        fxView.setPreserveRatio(true);
        soundFxBtn.setGraphic(fxView);
        soundFxBtn.setVisible(false);
        soundFxBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (soundFX) {
                    fxView.setImage(soundFXOff);
                    soundFX = false;
                } else {
                    fxView.setImage(soundFXOn);
                    soundFX = true;
                    
                }
                
            }
        });
        
        
        creditsBtn = new Button();
        StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));
        creditsBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(creditsBtn);
        Image creditsImg = new Image("menuImages/Credits.png");
        creditsBtn.setGraphic(new ImageView(creditsImg));
        creditsBtn.setVisible(false);
        
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(11);
        
        settingsBtn = new Button();
        settingsBtn.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
        StackPane.setMargin(settingsBtn, new Insets(50, 0, 0, 50));
        Image settingsImg = new Image("menuImages/settings.png");
        ImageView settingsView = new ImageView(settingsImg);
        settingsView.setFitHeight(50);
        settingsView.setFitWidth(50);
        settingsView.setPreserveRatio(true);
        settingsBtn.setGraphic(settingsView);
        settingsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!viewSettings) {
                    musicBtn.setVisible(true);
                    soundFxBtn.setVisible(true);
                    creditsBtn.setVisible(true);
                    viewSettings = true;
                    bg.setEffect(gaussianBlur);
                } else {
                    musicBtn.setVisible(false);
                    soundFxBtn.setVisible(false);
                    viewSettings = false;
                    creditsBtn.setVisible(false);
                    bg.setEffect(null);
                }
            }
        });
        
        root.getChildren().add(settingsBtn);
        
        quitBtn = new Button();
        StackPane.setAlignment(quitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(quitBtn, new Insets(50, 50, 0, 0));
        quitBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(quitBtn);
        Image quitImg = new Image("menuImages/quit.png");
        quitBtn.setGraphic(new ImageView(quitImg));
        
        backTree.empty();
        itemsOnScreen.add(playBtn);
        
        return root;
    }
}
