package utils;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ImageTester extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    final Canvas canvas = new Canvas(480, 640);
    final GraphicsContext gc = canvas.getGraphicsContext2D();

    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(2);
    Image img = mipSprites.get(0).get(0);

    gc.drawImage(img,0,0);

    stage.setScene(new Scene(new Group(canvas)));
    stage.show();
  }
}
