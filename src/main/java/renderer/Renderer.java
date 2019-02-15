package renderer;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import objects.Entity;
import utils.Map;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.MapElement;

public class Renderer {

  private ResourceLoader r;
  private static final double MAP_BORDER = 10;
  private final GraphicsContext gc;
  private int xResolution;
  private int yResolution;
  private Point2D.Double mapRenderingCorner;

  private ArrayList<Image> mapTiles;
  private Image background;
  private BufferedImage palette;

  private double tileSizeX;
  private double tileSizeY;

  private int clientID;

  private Font geoSmall;
  private Font geoLarge;

  private ArrayList<Point2D.Double> traversalOrder = new ArrayList<>();

  /**
   * @param _xResolution Game x resolution
   * @param _yResolution Game y resolution
   */
  public Renderer(GraphicsContext _gc, int _xResolution, int _yResolution, ResourceLoader r) {
    this.r = r;
    this.gc = _gc;
    this.xResolution = _xResolution;
    this.yResolution = _yResolution;
    this.background = r.getBackground();
    this.palette = r.getBackgroundPalette();

    Map map = r.getMap();

    final int ROW = map.getMaxX();
    final int COL = map.getMaxY();

    for (int line = 1; line <= (ROW + COL - 1); line++) {
      int start_col = Math.max(0, line - ROW);

      int count = Math.min(line, Math.min(COL - start_col, ROW));

      // Print elements of this line
      for (int j = 0; j < count; j++) {
        int x = Math.min(ROW, line) - j - 1;
        int y = start_col + j;
        this.traversalOrder.add(new Double(x, y));
      }
    }

    this.init();
  }

  public void init() {
    this.mapTiles = r.getMapTiles();
    this.mapRenderingCorner = getMapRenderingCorner();
    tileSizeX = r.getMapTiles().get(0).getWidth();
    tileSizeY = r.getMapTiles().get(0).getHeight();

    //set fonts
    final double fontRatio = 0.07;
    try {
      this.geoLarge = Font
          .loadFont(new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              xResolution * fontRatio);
      this.geoSmall = Font
          .loadFont(new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              0.8 * xResolution * fontRatio);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param map Game map
   * @param entityArr Playable entities
   */
  public void render(Map map, Entity[] entityArr) {
    gc.clearRect(0, 0, xResolution, yResolution);
    renderBackground(map);
    int[][] rawMap = map.raw();
    ArrayList<Entity> entities = new ArrayList<>(Arrays.asList(entityArr));
    //sort entities to get rendering order
    entities.sort(Comparator.comparingDouble(
        o -> o.getLocation().getX() + o.getLocation().getY()));

    int entityCounter = 0;
    Image currentSprite;
    Point2D.Double rendCoord;
    Point spriteCoord = new Point(java.lang.Double.MAX_VALUE,
        java.lang.Double.MAX_VALUE);

    int x;
    int y;

    //Render floor first (floors will never be on a higher layer than anything apart form the background
    for (Point2D.Double coord : traversalOrder) {
      x = (int) coord.getX();
      y = (int) coord.getY();

      if (MapElement.FLOOR.toInt() == rawMap[x][y]) {
        rendCoord = getIsoCoord(x, y, mapTiles.get(MapElement.FLOOR.toInt()).getHeight(),
            mapTiles.get(MapElement.FLOOR.toInt()).getWidth());
        gc.drawImage(mapTiles.get(MapElement.FLOOR.toInt()), rendCoord.x, rendCoord.y);
      }
    }

    //Loop through grid in diagonal traversal to render walls and entities by depth
    for (Point2D.Double coord : traversalOrder) {
      x = (int) coord.getX();
      y = (int) coord.getY();

      currentSprite = mapTiles.get(rawMap[x][y]);
      rendCoord = getIsoCoord(x, y, currentSprite.getHeight(), currentSprite.getWidth());
      if (MapElement.FLOOR.toInt() == rawMap[x][y]) {
        continue;
      }

      //render wall (or any other non passable terrain)
      gc.drawImage(currentSprite, rendCoord.x, rendCoord.y);

      if (entityCounter < entities.size()) {
        spriteCoord = entities.get(entityCounter).getLocation();
      }

      //is the current entities depth the same or deeper than the wall just rendered?
      while (entityCounter < entities.size()
          && ((x + y) >= ((int) spriteCoord.getX() + (int) spriteCoord.getY()))
          && spriteCoord.getX() > x) {
        renderEntity(entities.get(entityCounter));
        entityCounter++;

        //point to the next entity
        if (entityCounter < entities.size()) {
          spriteCoord = entities.get(entityCounter).getLocation();
        }
      }

    }

    renderHUD(entityArr);
  }

  public void setClientID(int _id) {
    this.clientID = _id;
  }

  private void renderBackground(Map map) {
    //render backing image
    gc.drawImage(background, 0, 0, xResolution, yResolution);

    //Render map base
    Point2D.Double tmpCoord = getIsoCoord(0, 0, tileSizeY, tileSizeX);
    Point2D.Double topLeft = new Double(tmpCoord.getX() + 0.5 * tileSizeX,
        tmpCoord.getY() - 0.5 * MAP_BORDER);

    tmpCoord = getIsoCoord(map.getMaxX(), 0, tileSizeY, tileSizeX);
    Point2D.Double topRight = new Double(tmpCoord.getX() + MAP_BORDER + tileSizeX,
        tmpCoord.getY() + 0.5 * tileSizeY);

    tmpCoord = getIsoCoord(0, map.getMaxY(), tileSizeY, tileSizeX);
    Point2D.Double bottomLeft = new Double(tmpCoord.getX() - 0.5 * MAP_BORDER,
        tmpCoord.getY() + 0.5 * tileSizeY);

    tmpCoord = getIsoCoord(map.getMaxX(), map.getMaxY(), tileSizeY, tileSizeX);
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
    double x = getIsoCoord(map.getMaxX() / (double) 2, map.getMaxY() / (double) 2, tileSizeY,
        tileSizeX)
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
  private Point2D.Double getIsoCoord(double x, double y, double spriteHeight, double spriteWidth) {
    double isoX = mapRenderingCorner.getX() - (y - x) * (this.tileSizeX / (double) 2) + (tileSizeX
        - spriteWidth);
    double isoY =
        mapRenderingCorner.getY() + (y + x) * (this.tileSizeY / (double) 2) + (tileSizeY
            - spriteHeight);
    return new Point2D.Double(isoX, isoY);
  }

  /**
   * @param e entity to render
   */
  private void renderEntity(Entity e) {
    Image currentSprite = e.getImage().get(0);
    Point2D.Double rendCoord = getIsoCoord(e.getLocation().getX() - 0.5,
        e.getLocation().getY() - 0.5,
        currentSprite.getHeight(), currentSprite.getWidth());
    gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());

    //render marker for entity
    if (e.getClientId() != clientID || !e.isPacman()) {
      return;
    }

    Image marker = (e.isPacman()) ? r.getMipMarker() : r.getMClientMarker();
    Point2D.Double coord = getIsoCoord(e.getLocation().getX(), e.getLocation().getY(),
        marker.getHeight(), marker.getWidth());

    gc.drawImage(marker, coord.getX(), coord.getY() + currentSprite.getHeight());
  }

  /**
   * @return The top right corner coordinate to start rendering game map from
   */
  private Point2D.Double getMapRenderingCorner() {
    return new Point2D.Double(this.xResolution / (double) 2, this.yResolution / (double) 10);
    //return new Point2D.Double(getIsoCoord(0,map),getIsoCoord(0,0,tileSizeY).getY())
  }

  private void renderHUD(Entity[] entities) {
    gc.setFill(Color.WHITE);
    final double paddingRatio = 0.1;
    final double xOffset = paddingRatio * yResolution;
    final double yOffset = paddingRatio * xResolution;
    double textLength = 270;
    double nameScoreGap = 100;

    //calculate corner coordinate to render other players scores from
    Point2D.Double topLeft = new Double(xOffset, yOffset - nameScoreGap);
    Point2D.Double topRight = new Double(xResolution - xOffset - textLength,
        yOffset - nameScoreGap);
    Point2D.Double botLeft = new Double(xOffset, yResolution - yOffset);
    Point2D.Double botRight = new Double(xResolution - xOffset - textLength,
        yResolution - yOffset);

    ArrayList<Point2D.Double> scoreCoord = new ArrayList<>(
        Arrays.asList(topLeft, topRight, botLeft, botRight));

    int cornerCounter = 0;
    gc.setFont(geoSmall);
    for (Entity e : entities) {
      if (e.getClientId() == clientID) { //render own score
        gc.setFont(geoLarge);
        gc.fillText("Score:" + e.getScore(), xResolution / 2 - textLength / 2, yResolution / 15);
      } else {//render other players score and name
        Point2D.Double cornerCoord = scoreCoord.get(cornerCounter);
        cornerCounter++;
        gc.setFont(geoSmall);
        gc.fillText("Score:" + e.getScore(), cornerCoord.getX(), cornerCoord.getY() + nameScoreGap);
        gc.setFont(geoLarge);
        gc.fillText("Player" + e.getClientId(), cornerCoord.getX(), cornerCoord.getY());
      }
    }
  }

  private void setFillColour(int colour) {
    gc.setFill(new Color((colour >> 16 & 0xFF) / (double) 255, (colour >> 8 & 0xFF) / (double) 255,
        (colour & 0xFF) / (double) 255, 1));
  }

  public void setResolution(int x, int y, boolean integerScaling) {
    r.setResolution(x, y, integerScaling);
    xResolution = x;
    yResolution = y;
    this.init();
  }

}
