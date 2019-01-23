package ui;
import audio.AudioController;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuController {
    
    private AudioController audioController;
    private boolean viewSettings = false;
    private boolean soundFX = true;
    private boolean music = true;
    private Stage primaryStage;
    
    public MenuController(AudioController audio, Stage stage){
        this.audioController = audio;
        this.primaryStage = stage;
    
    }
    
    public Node createMainMenu(){
        StackPane root = new StackPane();
        root.setPrefSize(1920,1080);
    
    
        ImageView bg = new ImageView("menuImages/EditedBackground.png");
        bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
        root.getChildren().add(bg);
        StackPane.setAlignment(bg, Pos.CENTER);
    
        Button singleplayerBtn =  new Button();
        StackPane.setAlignment(singleplayerBtn, Pos.CENTER);
        StackPane.setMargin(singleplayerBtn, new Insets(160, 0,0,0));
        Image singleplayerImg = new Image("menuImages/Single-Player.png");
        singleplayerBtn.setGraphic(new ImageView(singleplayerImg));
        singleplayerBtn.setStyle("-fx-background-color: transparent;");
        singleplayerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            }
        });
    
        root.getChildren().add(singleplayerBtn);
        singleplayerBtn.setVisible(false);
    
        Button multiplayerBtn = new Button();
        StackPane.setAlignment(multiplayerBtn, Pos.CENTER);
        StackPane.setMargin(multiplayerBtn, new Insets(320, 0,0,0));
        Image multiplayerImg = new Image("menuImages/Multiplayer.png");
        multiplayerBtn.setGraphic(new ImageView(multiplayerImg));
        multiplayerBtn.setStyle("-fx-background-color: transparent;");
        multiplayerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            }
        });
        root.getChildren().add(multiplayerBtn);
        multiplayerBtn.setVisible(false);
    
        Button backBtn = new Button();
        StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(backBtn, new Insets(0,0,100,0));
        Image backImg = new Image("menuImages/back.png");
        backBtn.setGraphic(new ImageView(backImg));
        backBtn.setStyle("-fx-background-color: transparent;");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            //TODO Take all hide items in currently viewable array list and clear then show items in previously shown and cle
            @Override
            public void handle(ActionEvent event) {
            
            
            }
        });
//        final Font f = Font.loadFont(new FileInputStream(new File("./src/Utils/ARCADEPI.TTF")), 12);
        Button playBtn = new Button();
        StackPane.setAlignment(playBtn, Pos.CENTER);
        StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
        Image playImg = new Image("menuImages/play.png");
        playBtn.setGraphic(new ImageView(playImg));
        playBtn.setStyle("-fx-background-color: transparent;");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                playBtn.setVisible(false);
                singleplayerBtn.setVisible(true);
                multiplayerBtn.setVisible(true);
            }
        });
        root.getChildren().add(playBtn);
    
    
        Button musicBtn = new Button();
        StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(musicBtn, new Insets(0,0, 25, 0));
        musicBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(musicBtn);
        Image musicOn = new Image("menuImages/Music-On.png");
        Image musicOff = new Image("menuImages/Music-Off.png");
        musicBtn.setGraphic(new ImageView(musicOn));
        musicBtn.setVisible(false);
        musicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(music){
                    musicBtn.setGraphic(new ImageView(musicOff));
                    music = false;
                }else{
                    musicBtn.setGraphic(new ImageView(musicOn));
                    music = true;
                }
            
            }
        });
    
        Button soundFxBtn = new Button();
        StackPane.setAlignment(soundFxBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(soundFxBtn, new Insets(150,0, 0, 0));
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
                if(soundFX){
                    fxView.setImage(soundFXOff);
                    soundFX = false;
                }else{
                    fxView.setImage(soundFXOn);
                    soundFX = true;
                
                }
            
            }
        });
    
    
        Button creditsBtn = new Button();
        StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(creditsBtn, new Insets(0,0,50, 0));
        creditsBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(creditsBtn);
        Image creditsImg = new Image("menuImages/Credits.png");
        creditsBtn.setGraphic(new ImageView(creditsImg));
        creditsBtn.setVisible(false);
    
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(11);
    
        Button settingsBtn = new Button();
        settingsBtn.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
        StackPane.setMargin(settingsBtn, new Insets(50,0,0,50));
        Image settingsImg = new Image("menuImages/settings.png");
        ImageView settingsView = new ImageView(settingsImg);
        settingsView.setFitHeight(50);
        settingsView.setFitWidth(50);
        settingsView.setPreserveRatio(true);
        settingsBtn.setGraphic(settingsView);
        settingsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!viewSettings){
                    musicBtn.setVisible(true);
                    soundFxBtn.setVisible(true);
                    creditsBtn.setVisible(true);
                    viewSettings = true;
                    bg.setEffect(gaussianBlur);
                }else{
                    musicBtn.setVisible(false);
                    soundFxBtn.setVisible(false);
                    viewSettings = false;
                    creditsBtn.setVisible(false);
                    bg.setEffect(null);
                }
            }
        });
        root.getChildren().add(settingsBtn);
    
        Button quitBtn = new Button();
        StackPane.setAlignment(quitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(quitBtn, new Insets(50, 50, 0, 0));
        quitBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(quitBtn);
        Image quitImg = new Image("menuImages/quit.png");
        quitBtn.setGraphic(new ImageView(quitImg));
        
        return root;
    }
}
