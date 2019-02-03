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

  private ArrayList<Image> mapTiles;

  private double tileSizeX = 39;
  private double tileSizeY = 19;

  /**
   * @param _xResolution Game x resolution
   * @param _yResolution Game y resolution
   * @param _mapTiles Map sprites (placeholder)
   */
  public Renderer(GraphicsContext _gc, int _xResolution, int _yResolution,
      ArrayList<Image> _mapTiles) {
    this.gc = _gc;
    this.xResolution = _xResolution;
    this.yResolution = _yResolution;
    this.mapRenderingCorner = getMapRenderingCorner();
    this.mapTiles = _mapTiles;

    //this.floor =
  }

  /**
   * @param map Game map
   * @param entities Playable entities
   */
  public void render(Map map, ArrayList<Entity> entities) {

    //sort entities to get rendering order
    entities.sort((o1, o2) -> {
      if (o1.getLocation().getY() == o2.getLocation().getY()) {
        return Double.compare(o1.getLocation().getX(), o2.getLocation().getX());
      } else if (o1.getLocation().getY() > o2.getLocation().getY()) {
        return 1;
      } else {
        return -1;
      }

    });

    int entityCounter = 0; //current position in entity list
    Image currentSprite;
    Point2D.Double rendCoord;

    int[][] rawMap = map.raw();

    for (int y = 0; y < rawMap.length; y++) {
      for (int x = 0; x < rawMap[y].length; x++) {
        //render current map tile
        currentSprite = mapTiles.get(rawMap[x][y]);
        rendCoord = getIsoCoord(x, y, currentSprite.getHeight());
        gc.drawImage(currentSprite, rendCoord.x, rendCoord.y);

        //check if entity should be on top of this tile
        Point2D.Double spriteCoord = entities.get(entityCounter).getLocation();
        while ((y - 1 <= spriteCoord.getY() && spriteCoord.getY() <= y
            || x - 1 <= spriteCoord.getX() && spriteCoord.getX() <= x) && entityCounter < entities
            .size()) {
          renderEntity(entities.get(entityCounter));
          entityCounter++;
        }
      }
    }

    //render elements off the map
    for (Entity e : entities.subList(entityCounter, entities.size())) {
      renderEntity(e);
    }

    renderHUD();

  }

  /**
   *
   * @param x cartesian x coordinate
   * @param y cartesian Y coordinate
   * @param spriteHeight vertical offset
   * @return
   */
  private Point2D.Double getIsoCoord(double x, double y, double spriteHeight) {
    double isoX = mapRenderingCorner.getX() - (y - x) * this.tileSizeX;
    double isoY = mapRenderingCorner.getY() - (y - x) * this.tileSizeY + (tileSizeY - spriteHeight);
    return new Point2D.Double(isoX, isoY);
  }

  /**
   *
   * @param e entity to render
   */
  private void renderEntity(Entity e) {
    Image currentSprite = e.getImage().get(0);
    Point2D.Double rendCoord = getIsoCoord(e.getLocation().getX(), e.getLocation().getY(),
        currentSprite.getHeight());
    gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());
  }

  /**
   * @return The top right corner coordinate to start rendering game map from
   */
  private Point2D.Double getMapRenderingCorner() {
    return new Point2D.Double(this.xResolution / 1.2, this.yResolution / (double) 10);
  }

  private void renderHUD() {

  }
}
