package renderer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.PriorityQueue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import objects.Entity;
import utils.Map;
import utils.Renderable;

public class Renderer {

  private final GraphicsContext gc;
  private final int xResolution;
  private final int yResolution;
  private Point2D.Double mapRenderingCorner;

  private Image floor;
  private Image wall;

  private double tileSizeX = 39;
  private double tileSizeY = 19;


  public Renderer(GraphicsContext _gc, int _xResolution, int _yResolution) {
    this.gc = _gc;
    this.xResolution = _xResolution;
    this.yResolution = _yResolution;
    this.mapRenderingCorner = getMapRenderingCorner();

    //this.floor =
  }

  public void render(Map map, ArrayList<Entity> entities) {
    PriorityQueue<Renderable> gameObjects = new PriorityQueue<>(entities);

    int[][] rawMap = map.raw();

    for (int x = 0; x < rawMap.length; x++) {
      for (int y = 0; y < rawMap[x].length; y++) {
        Point2D.Double tileCoord = getIsoCoord(x, y, 4);
      }
    }


  }

  private Point2D.Double getIsoCoord(double x, double y, int spriteHeight) {
    double isoX = mapRenderingCorner.getX() - (y - x) * this.tileSizeX;
    double isoY = mapRenderingCorner.getY() - (y - x) * this.tileSizeY + (tileSizeY - spriteHeight);
    return new Point2D.Double(isoX, isoY);
  }

  private Point2D.Double getMapRenderingCorner() {
    return new Point2D.Double(this.xResolution / (double) 10, this.yResolution / (double) 10);
  }
}
