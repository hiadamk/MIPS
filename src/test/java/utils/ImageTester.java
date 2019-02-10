package utils;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import objects.Entity;
import renderer.Renderer;
import utils.enums.Direction;

import java.awt.geom.Point2D.Double;

public class ImageTester extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    final Canvas canvas = new Canvas(1920, 1080);
    final GraphicsContext gc = canvas.getGraphicsContext2D();

    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    
      resourceLoader.loadMap("9x9");
    Map map = resourceLoader.getMap();
      Renderer r = new Renderer(gc, 1920, 1080, resourceLoader);



    Entity mip = new Entity(true, 1, new Double(1, 2));
    mip.setPacMan(true);

    Entity ghoul = new Entity(false, 4, new Double(1, 5));
    System.out.println(ghoul.getLocation().toString());
    ghoul.setPacMan(false);
    ghoul.setDirection(Direction.RIGHT);

    Entity[] entities = new Entity[]{ghoul};
    Methods.updateImages(entities, resourceLoader);

    Image background = resourceLoader.getBackground();
    gc.drawImage(background, 0, 0, 1920, 1080);

    r.render(map, entities);
    stage.setScene(new Scene(new Group(canvas)));
    stage.show();

  }
}
