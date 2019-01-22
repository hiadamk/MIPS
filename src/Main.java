import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {
    
    private boolean viewSettings = false;
    private boolean soundFX = true;
    private boolean music = true;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
        StackPane root = new StackPane();
        root.setPrefSize(1920,1080);
        
        
        ImageView bg = new ImageView("images/EditedBackground.png");
        bg.fitWidthProperty().bind(primaryStage.widthProperty());
        root.getChildren().add(bg);
        StackPane.setAlignment(bg, Pos.CENTER);
//        final Font f = Font.loadFont(new FileInputStream(new File("./src/Utils/ARCADEPI.TTF")), 12);
        Button playBtn = new Button();
        StackPane.setAlignment(playBtn, Pos.CENTER);
        StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
//        playBtn.setFont(f);
//        playBtn.setText("Play");
        Image playImg = new Image("images/play.png");
        playBtn.setGraphic(new ImageView(playImg));
        playBtn.setStyle("-fx-background-color: transparent;");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("Clicked");
            }
        });
        root.getChildren().add(playBtn);
    
    
        Button musicBtn = new Button();
        StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(musicBtn, new Insets(0,0, 25, 0));
        musicBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(musicBtn);
        Image musicOn = new Image("images/Music-On.png");
        Image musicOff = new Image("images/Music-Off.png");
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
        Image soundFXOn = new Image("images/SoundFX-On.png");
        Image soundFXOff = new Image("images/SoundFX-Off.png");
        soundFxBtn.setGraphic(new ImageView(soundFXOn));
        soundFxBtn.setVisible(false);
        soundFxBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(soundFX){
                    soundFxBtn.setGraphic(new ImageView(soundFXOff));
                    soundFX = false;
                }else{
                    soundFxBtn.setGraphic(new ImageView(soundFXOn));
                    soundFX = true;
                }
            
            }
        });
        
        
        Button creditsBtn = new Button();
        StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(creditsBtn, new Insets(0,0,50, 0));
        creditsBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(creditsBtn);
        Image creditsImg = new Image("images/Credits.png");
        creditsBtn.setGraphic(new ImageView(creditsImg));
        creditsBtn.setVisible(false);
        
        GaussianBlur gaussianBlur = new GaussianBlur();
        gaussianBlur.setRadius(11);
        
        Button settingsBtn = new Button();
        settingsBtn.setStyle("-fx-background-color: transparent;");
        StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
        StackPane.setMargin(settingsBtn, new Insets(50,0,0,50));
        Image settingsImg = new Image("images/settings.png");
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
        Image quitImg = new Image("images/quit.png");
        quitBtn.setGraphic(new ImageView(quitImg));
        
        primaryStage.setTitle("M.I.P.S");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
