package utils;

import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import objects.Entity;
import renderer.Renderer;

public class ImageTester extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    final Canvas canvas = new Canvas(1920, 1080);
    final GraphicsContext gc = canvas.getGraphicsContext2D();

    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");

    Map map = resourceLoader.getMap();
    Renderer r = new Renderer(gc, 1920, 1080, resourceLoader.getMapTiles());

    ArrayList<Entity> entities = new ArrayList<>();
    Entity mip = new Entity(true, 0, resourceLoader);
    mip.setLocation(new Double(1, 1));
    mip.setPacMan(true);
    entities.add(mip);

    r.render(map, entities);

    stage.setScene(new Scene(new Group(canvas)));
    stage.show();
  }
}
