package renderer;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import objects.Entity;
import utils.Map;
import utils.Renderable;
import utils.ResourceLoader;

public class Renderer {

  private static final double MAP_BORDER = 10;
  private final GraphicsContext gc;
  private final int xResolution;
  private final int yResolution;
  private Point2D.Double mapRenderingCorner;

  private ArrayList<Image> mapTiles;
  private Image background;
  private BufferedImage palette;

  private double tileSizeX = 39;
  private double tileSizeY = 19;

  /**
   * @param _xResolution Game x resolution
   * @param _yResolution Game y resolution
   */
  public Renderer(GraphicsContext _gc, int _xResolution, int _yResolution, ResourceLoader r) {
    this.gc = _gc;
    this.xResolution = _xResolution;
    this.yResolution = _yResolution;
    this.mapRenderingCorner = getMapRenderingCorner();
    this.mapTiles = r.getMapTiles();
    this.background = r.getBackground();
    this.palette = r.getBackgroundPalette();

  }

  /**
   * @param map Game map
   * @param entityArr Playable entities
   */
  public void render(Map map, Entity[] entityArr) {

    renderBackground(map);

    ArrayList<Entity> entities = new ArrayList<>(Arrays.asList(entityArr));
    //sort entities to get rendering order
    entities.sort((o1, o2) -> {
      if (o1.getLocation().getY() == o2.getLocation().getY()) {
        return java.lang.Double.compare(o1.getLocation().getX(), o2.getLocation().getX());
      } else if (o1.getLocation().getY() > o2.getLocation().getY()) {
        return 1;
      } else {
        return -1;
      }

    });

    int entityCounter = 0; //current position in entity list
    Image currentSprite;
    Point2D.Double rendCoord;
    Point2D.Double spriteCoord = new Point2D.Double(java.lang.Double.MAX_VALUE,
        java.lang.Double.MAX_VALUE);

    int[][] rawMap = map.raw();

    for (int x = 0; x < rawMap.length; x++) {
      for (int y = 0; y < rawMap[x].length; y++) {
        //render current map tile
        currentSprite = mapTiles.get(rawMap[x][y]);
        rendCoord = getIsoCoord(x, y, currentSprite.getHeight());
        gc.drawImage(currentSprite, rendCoord.x, rendCoord.y);

        //check if entity should be on top of this tile
        if (entityCounter < entities.size()) {
          spriteCoord = entities.get(entityCounter).getLocation();
        }

        while (entityCounter < entities
            .size() && x == (int) (spriteCoord.getX() + 1) && y == (int) (spriteCoord.getY() + 1)) {
          renderEntity(entities.get(entityCounter));
          entityCounter++;

          if (entityCounter < entities.size()) {
            spriteCoord = entities.get(entityCounter).getLocation();
          }
        }
      }
    }

    renderHUD();

  }

  private void renderBackground(Map map) {
    //render backing image
    gc.drawImage(background, 0, 0, xResolution, yResolution);

    //Render map base
    Point2D.Double tmpCoord = getIsoCoord(0, 0, tileSizeY);
    Point2D.Double topLeft = new Double(tmpCoord.getX() + 0.5 * tileSizeX,
        tmpCoord.getY() - 0.5 * MAP_BORDER);

    tmpCoord = getIsoCoord(map.getMaxX(), 0, tileSizeY);
    Point2D.Double topRight = new Double(tmpCoord.getX() + MAP_BORDER + tileSizeX,
        tmpCoord.getY() + 0.5 * tileSizeY);

    tmpCoord = getIsoCoord(0, map.getMaxY(), tileSizeY);
    Point2D.Double bottomLeft = new Double(tmpCoord.getX() - 0.5 * MAP_BORDER,
        tmpCoord.getY() + 0.5 * tileSizeY);

    tmpCoord = getIsoCoord(map.getMaxX(), map.getMaxY(), tileSizeY);
    Point2D.Double bottomRight = new Double(tmpCoord.getX() + 0.5 * tileSizeX,
        tmpCoord.getY() + 0.5 * MAP_BORDER + tileSizeY);

    setFillColour(palette.getRGB(0, 0));
    gc.fillPolygon(
        new double[]{topLeft.getX(), topRight.getX(), bottomRight.getX(), bottomLeft.getX()},
        new double[]{topLeft.getY(), topRight.getY(), bottomRight.getY(), bottomLeft.getY()}, 4);

    //Render Pyramid underside

    double yChange = topRight.getX() - bottomRight.getX();
    double xChange = topRight.getY() - bottomRight.getY();

    double percentageXRes = 0.04;
    double ratio = ((percentageXRes * xResolution) / yChange) * (map.getMaxY() / (double) 20);

    //double x = bottomRight.getX() - xChange * ratio;
    double x = getIsoCoord(map.getMaxX() / (double) 2, map.getMaxY() / (double) 2, tileSizeY)
        .getX();
    double y = bottomRight.getY() + yChange * ratio;

    Point2D.Double pyramidVertex = new Point2D.Double(x, y);

    setFillColour(palette.getRGB(2, 0));
    gc.fillPolygon(
        new double[]{topRight.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[]{topRight.getY(), bottomRight.getY(), pyramidVertex.getY()}, 3);

    setFillColour(palette.getRGB(1, 0));
    gc.fillPolygon(
        new double[]{bottomLeft.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[]{bottomLeft.getY(), bottomRight.getY(), pyramidVertex.getY()}, 3);

    //Draw outline
    gc.setStroke(Color.BLACK);
    gc.strokePolygon(new double[]{bottomLeft.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[]{bottomLeft.getY(), bottomRight.getY(), pyramidVertex.getY()}, 3);

    gc.strokePolygon(
        new double[]{topRight.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[]{topRight.getY(), bottomRight.getY(), pyramidVertex.getY()}, 3);

    gc.strokePolygon(
        new double[]{topLeft.getX(), topRight.getX(), bottomRight.getX(), bottomLeft.getX()},
        new double[]{topLeft.getY(), topRight.getY(), bottomRight.getY(), bottomLeft.getY()}, 4);
  }

  /**
   * @param x cartesian x coordinate
   * @param y cartesian Y coordinate
   * @param spriteHeight vertical offset
   */
  private Point2D.Double getIsoCoord(double x, double y, double spriteHeight) {
    double isoX = mapRenderingCorner.getX() - (y - x) * (this.tileSizeX / (double) 2);
    double isoY =
        mapRenderingCorner.getY() + (y + x) * (this.tileSizeY / (double) 2) + (tileSizeY
            - spriteHeight);
    return new Point2D.Double(isoX, isoY);
  }

  /**
   * @param e entity to render
   */
  private void renderEntity(Renderable e) {
    Image currentSprite = e.getImage().get(0);
    Point2D.Double rendCoord = getIsoCoord(e.getLocation().getX() + 0.5,
        e.getLocation().getY() + 0.5,
        currentSprite.getHeight());
    gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());
  }

  /**
   * @return The top right corner coordinate to start rendering game map from
   */
  private Point2D.Double getMapRenderingCorner() {
    return new Point2D.Double(this.xResolution / (double) 2, this.yResolution / (double) 3);
    //return new Point2D.Double(getIsoCoord(0,map),getIsoCoord(0,0,tileSizeY).getY())
  }

  private void renderHUD() {

  }

  private boolean isBetween(double lowerBound, double upperBound, double num) {
    return num <= upperBound && num >= lowerBound;
  }

  private void setFillColour(int colour) {
    gc.setFill(new Color((colour >> 16 & 0xFF) / (double) 255, (colour >> 8 & 0xFF) / (double) 255,
        (colour & 0xFF) / (double) 255, 1));
  }

}
