package ui;
import audio.AudioController;
import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.layout.*;
import javafx.stage.Stage;


public class Main extends Application {
    
    private boolean viewSettings = false;
    private boolean soundFX = true;
    private boolean music = true;
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        
        AudioController audioController = new AudioController();
        MenuController menuController = new MenuController(audioController, primaryStage);
        StackPane root = (StackPane) menuController.createMainMenu();
        
        primaryStage.setTitle("M.I.P.S");
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
