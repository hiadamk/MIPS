package utils;

import java.util.HashMap;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import objects.Entity;
import renderer.Renderer;
import utils.enums.RenderingMode;

/**
 * @author Tim Cheung
 */
public class MapPreview {

  private int xRes;
  private int yRes;

  /**
   * @param x x resoltion of previews
   * @param y y resolution of previews
   */
  public MapPreview(int x, int y) {
    this.xRes = x;
    this.yRes = y;
  }

  /**
   * @param mapName name of map file (no file extension)
   * @return JavaFx image of a preview of the map
   */
  public Image getMapPreview(String mapName) {

    Canvas canvas = new Canvas(xRes, yRes);
    Group screenshotGroup = new Group();
    screenshotGroup.getChildren().add(canvas);

    Scene previewScene = new Scene(screenshotGroup);

    Stage hiddenWindow = new Stage();
    hiddenWindow.setScene(previewScene);

    GraphicsContext gc = canvas.getGraphicsContext2D();

    //create a separate instance of resource loader from the game to not overwrite its loaded themes
    ResourceLoader resourceLoader = new ResourceLoader("src/main/resources/");
    //load map name into resource loader
    resourceLoader.loadMap(mapName);
    Map map = resourceLoader.getMap();

    Renderer renderer = new Renderer(gc, xRes, yRes, resourceLoader);

    renderer.setResolution(xRes, yRes, RenderingMode.SMOOTH_SCALING);
    gc.setFill(Color.TRANSPARENT);
    gc.fillRect(0, 0, 480, 320);
    renderer.renderGameOnly(map, new Entity[]{}, 0, new HashMap<>());

    WritableImage screenshot = new WritableImage(xRes, yRes);

    canvas.snapshot(null, screenshot);

    hiddenWindow.hide();

    return screenshot;
  }


}
