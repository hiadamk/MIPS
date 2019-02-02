package renderer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import objects.Entity;
import utils.Map;

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

    //sort entities to get rendering order
    entities.sort((o1, o2) -> {
      if (o1.getLocation().getX() == o2.getLocation().getX()) {
        if (o1.getLocation().getY() == o2.getLocation().getY()) {
          return 0;
        } else if (o1.getLocation().getY() > o2.getLocation().getY()) {
          return 1;
        } else {
          return -1;
        }
      } else if (o1.getLocation().getX() > o2.getLocation().getX()) {
        return 1;
      } else {
        return -1;
      }

    });

    int[][] rawMap = map.raw();

    for (int y = 0; y < rawMap.length; y++) {
      for (int x = 0; x < rawMap[y].length; x++) {
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
