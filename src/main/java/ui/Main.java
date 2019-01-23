package ui;
import audio.AudioController;
import audio.Sounds;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {
    
    private boolean viewSettings = false;
    private boolean soundFX = true;
    private boolean music = true;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
    
    
        AudioController audioController = new AudioController();
        audioController.playMusic(Sounds.intro);
        
        Label dummyLabel = new Label("GAME SCENE");
        Scene dummyScene = new Scene(dummyLabel, 1920, 1080);
        
        MenuController menuController = new MenuController(audioController, primaryStage, dummyScene);
        StackPane root = (StackPane) menuController.createMainMenu();
        
        primaryStage.setTitle("M.I.P.S");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
