package utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MapPreviewTester extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    MapPreview mp = new MapPreview(480, 320);

    Image image = mp.getMapPreview("default");
    ImageView imageView = new ImageView(image);

    HBox hbox = new HBox(imageView);

    Scene scene = new Scene(hbox, 480, 320);
    primaryStage.setScene(scene);
    primaryStage.show();

  }
}
