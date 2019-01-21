import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
        StackPane root = new StackPane();
        root.setPrefSize(1920,1080);
        
        
        ImageView bg = new ImageView("images/EditedBackground.png");
        bg.fitWidthProperty().bind(primaryStage.widthProperty());
        root.getChildren().add(bg);
        StackPane.setAlignment(bg, Pos.CENTER);
        
        
        Button playBtn = new Button();
        StackPane.setAlignment(playBtn, Pos.CENTER);
        StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
        Image playImg = new Image("images/play.png");
        playBtn.setGraphic(new ImageView(playImg));
        playBtn.setStyle("-fx-background-color: transparent;");
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("Clicked");
            }
        });
        root.getChildren().add(playBtn);
        
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
        root.getChildren().add(settingsBtn);
        
        
        Button quitBtn = new Button();
        StackPane.setAlignment(quitBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(quitBtn, new Insets(50, 50, 0, 0));
        quitBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(quitBtn);
        Image quitImg = new Image("images/quit.png");
        quitBtn.setGraphic(new ImageView(quitImg));
        
        Button musicBtn = new Button();
        StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(quitBtn, new Insets(0,0, 25, 0));
        musicBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(musicBtn);
        Image musicOn = new Image("images/Music-On.png");
        musicBtn.setGraphic(new ImageView(musicOn));
    
        Button soundFxBtn = new Button();
        StackPane.setAlignment(soundFxBtn, Pos.CENTER_LEFT);
        StackPane.setMargin(soundFxBtn, new Insets(150,0, 0, 0));
        soundFxBtn.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(soundFxBtn);
        Image soundFXOn = new Image("images/SoundFX-On.png");
        soundFxBtn.setGraphic(new ImageView(soundFXOn));
        
        primaryStage.setTitle("M.I.P.S");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
