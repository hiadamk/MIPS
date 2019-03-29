package com.lordsofmidnight.renderer;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.EmptyPowerUpBox;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.MinePellet;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.GameLoop;
import com.lordsofmidnight.utils.Settings;
import com.lordsofmidnight.utils.UpDownIterator;
import com.lordsofmidnight.utils.enums.MapElement;
import com.lordsofmidnight.utils.enums.PowerUps;
import com.lordsofmidnight.utils.enums.RenderingMode;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class to render the game to the screen
 */
public class Renderer {

  private final GraphicsContext gc;
  private final long secondInNanoseconds = (long) Math.pow(10, 9);
  private final HeadsUpDisplay hudRender;
  private final ProjectileFX projectileManager;
  private int xResolution;
  final double MAP_BORDER = xResolution * 0.005;
  private int[][] rawMap;
  private Map map;
  private ResourceLoader r;
  Point deathLocation;
  boolean isHidden;
  private int yResolution;
  private Point2D.Double mapRenderingCorner;
  private ArrayList<Image> mapTiles;
  private Image background;
  private BufferedImage palette;
  private double tileSizeX;
  private double tileSizeY;
  private int clientID;
  private Font geoLarge;
  private long lastFrame;
  private int fps = 0;
  private int frameCounter = 0;
  private long timeSum;
  private Entity clientEntity = null;
  private BufferedImage playerColours;
  private ExplosionFX explosionManager;
  private int currentAnimationFrame = 0;
  private ArrayList<Point> traversalOrder = new ArrayList<>();
  private boolean refreshMap;
  // multiple use variables to render the game
  // (uses less memory than re-creating these objects every time)
  private Pellet currentPellet;
  private int entityCounter = 0;
  private Image currentSprite = null;
  private ArrayList<Image> currentSprites = null;
  private Double rendCoord = new Point2D.Double(0, 0);
  private Point spriteCoord;

  /**
   * @param _gc Graphics context to render the game onto
   * @param _xResolution Game x resolution
   * @param _yResolution Game y resolution
   * @param r Asset loader
   */
  public Renderer(GraphicsContext _gc, int _xResolution, int _yResolution, ResourceLoader r) {
    this.r = r;
    this.map = r.getMap();
    this.rawMap = map.raw();
    this.gc = _gc;
    this.xResolution = _xResolution;
    this.yResolution = _yResolution;
    this.background = r.getBackground();
    this.palette = r.getBackgroundPalette();
    this.playerColours = r.getPlayerPalette();
    this.hudRender = new HeadsUpDisplay(gc, _xResolution, _yResolution, r);
    this.explosionManager = new ExplosionFX(gc, r);
    this.projectileManager = new ProjectileFX(gc, r, this);
    this.initMapTraversal(r.getMap());
  }

  /**
   * @param colour intRGB colour
   * @return colour java.scene.Paint Color object representing the intRGB colour
   */
  public static Color intRGBtoColour(int colour) {
    return new Color(
        (colour >> 16 & 0xFF) / (double) 255,
        (colour >> 8 & 0xFF) / (double) 255,
        (colour & 0xFF) / (double) 255,
        1);
  }

  /**
   * @param map Game Map
   * @param entityArr Playable com.lordsofmidnight.objects
   * @param now Current game time in nanoseconds
   * @param pellets Consumable com.lordsofmidnight.objects
   */
  public void render(
      Map map,
      Entity[] entityArr,
      long now,
      PointMap<Pellet> pellets,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      int gameTime) {

    if (refreshMap) {
      this.map = r.getMap();
      this.rawMap = map.raw();
      initMapTraversal(map);
      refreshMap = false;
    }

    this.clientEntity = entityArr[this.clientID];

    long timeElapsed = now - lastFrame;
    // clear screen
    gc.clearRect(0, 0, xResolution, yResolution);
    renderBackground(map);
    renderGameOnly(entityArr, now, pellets, activePowerUps);
    hudRender.renderHUD(entityArr, gameTime);
    hudRender.renderInventory(this.clientEntity, timeElapsed);
    // showFPS(timeElapsed);

    lastFrame = now;

    if (clientEntity.isDead()) {
      int timeUntilRespawn =
          Math.round((clientEntity.getDeathTime() - clientEntity.getDeathCounter()) / 100);
      hudRender.renderDeathScreen(timeUntilRespawn, clientEntity);
    }
  }

  /**
   * initialises map array, map traversal order, map tiles and fonts
   *
   * @param map the map to set the traversal for
   */
  public void initMapTraversal(Map map) {

    final int ROW = map.getMaxX();
    final int COL = map.getMaxY();

    this.traversalOrder = new ArrayList<>();
    // find diagonal traversal order (map depth order traversal)
    for (int line = 1; line <= (ROW + COL - 1); line++) {
      int start_col = Math.max(0, line - ROW);

      int count = Math.min(line, Math.min(COL - start_col, ROW));

      for (int j = 0; j < count; j++) {
        int x = Math.min(ROW, line) - j - 1;
        int y = start_col + j;
        this.traversalOrder.add(new Point(x, y));
      }
    }

    this.mapTiles = r.getMapTiles();
    this.mapRenderingCorner = getMapRenderingCorner();
    tileSizeX = r.getMapTiles().get(0).getWidth();
    tileSizeY = r.getMapTiles().get(0).getHeight();

    // set fonts
    final double fontRatio = 0.07;
    try {
      this.geoLarge =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              xResolution * fontRatio);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param entityArr Entities in the game
   * @param now current time (nanoseconds)
   * @param pellets pellets, powerupboxs, mines and traps on the map
   * @param activePowerUps powerups in use
   */
  public void renderGameOnly(
      Entity[] entityArr,
      long now,
      PointMap<Pellet> pellets,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps) {

    ArrayList<Entity> entities = new ArrayList<>(Arrays.asList(entityArr));

    // sort entities to get depth rendering order
    entities.sort(Comparator.comparingDouble(o -> o.getLocation().getX() + o.getLocation().getY()));

    spriteCoord = new Point(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE);
    entityCounter = 0;
    int x;
    int y;

    // hashmap linking entities to powerups
    HashMap<Entity, HashMap<PowerUps, PowerUp>> entityPowerUps = new HashMap<>();
    for (Entity e : entityArr) {
      entityPowerUps.put(e, new HashMap<>());
    }

    // add powerups to entity
    if (activePowerUps != null) {
      for (PowerUp p : activePowerUps.values()) {
        entityPowerUps.get(p.getUser()).put(p.getType(), p);
      }
    }

    // Render floor first (floors will never be on a higher layer than anything apart form the
    // background
    for (Point coord : traversalOrder) {
      x = (int) coord.getX();
      y = (int) coord.getY();

      if (MapElement.FLOOR.toInt() == rawMap[x][y]) {
        setIsoCoord(
            rendCoord,
            x,
            y,
            mapTiles.get(MapElement.FLOOR.toInt()).getHeight(),
            mapTiles.get(MapElement.FLOOR.toInt()).getWidth());
        gc.drawImage(mapTiles.get(MapElement.FLOOR.toInt()), rendCoord.x, rendCoord.y);
      }
    }

    // Loop through grid in diagonal traversal to render walls and entities by depth
    for (Point coord : traversalOrder) {

      currentPellet = pellets.get(coord);
      if (currentPellet != null && currentPellet.isActive()) {

        isHidden = false;

        // check whether the client should be able to see the pellet
        if (currentPellet.canUse(entityArr[this.clientID])) {
          // is the current pellet a  a fakebox or a powerupbox?
          if (currentPellet instanceof PowerUpBox || currentPellet instanceof EmptyPowerUpBox) {
            currentSprite = r.getPowerBox().get(0);
          }
          // is the current pellet a mine
          else if (currentPellet instanceof MinePellet) {
            currentSprite = r.getMine().get(currentAnimationFrame % r.getMine().size());
            if (((MinePellet) currentPellet).isHidden()) {
              // hide the mine
              isHidden = true;
            }
          } else {
            currentSprite = r.getPellet().get(0);
          }
        } else {
          currentSprite = r.getTranslucentPellet().get(0);
        }

        // render pellet using either translucent or opaque sprite
        double x_ = currentPellet.getLocation().getX() - 0.5;
        double y_ = currentPellet.getLocation().getY() - 0.5;
        setIsoCoord(rendCoord, x_, y_, currentSprite.getHeight(), currentSprite.getWidth());
        if (!isHidden) {
          gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());
        }
      }

      x = (int) coord.getX();
      y = (int) coord.getY();

      currentSprite = mapTiles.get(rawMap[x][y]);
      setIsoCoord(rendCoord, x, y, currentSprite.getHeight(), currentSprite.getWidth());
      if (MapElement.FLOOR.toInt() == rawMap[x][y]) {
        continue;
      }

      // render wall (or any other non passable terrain)
      gc.drawImage(currentSprite, rendCoord.x, rendCoord.y);

      if (entityCounter < entities.size()) {
        spriteCoord = entities.get(entityCounter).getLocation();
      }

      // is the current entities depth the same or deeper than the wall just rendered?
      while (entityCounter < entities.size()
          && ((x + y) >= ((int) spriteCoord.getX() + (int) spriteCoord.getY()))
          && spriteCoord.getX() > x) {

        // is it a collision animation call?
        if (now == 0) {
          renderEntity(entities.get(entityCounter), null, 0);
          entityCounter++;
        }
        // normal render call
        else {
          Entity entityToRender = entities.get(entityCounter);
          renderEntity(entityToRender, entityPowerUps.get(entityToRender), now - lastFrame);
          entityCounter++;
        }

        // point to the next entity
        if (entityCounter < entities.size()) {
          spriteCoord = entities.get(entityCounter).getLocation();
        }
      }
    }

    // render explosions and projectiles if it isn't a collision animation call
    if (now != 0) {
      explosionManager.render(now - lastFrame);
      projectileManager.render(now - lastFrame, activePowerUps);
    }
  }

  /** @param timeElapsed time since last call */
  private void showFPS(long timeElapsed) {

    gc.setTextAlign(TextAlignment.CENTER);
    if (timeSum > secondInNanoseconds) {
      fps = frameCounter / (int) (timeSum / secondInNanoseconds);
      gc.fillText("FPS:" + fps, xResolution / 2, yResolution - 100);
      timeSum = 0;
      frameCounter = 0;
    } else {
      gc.fillText("FPS:" + fps, xResolution / 2, yResolution - 100);
      timeSum += timeElapsed;
      frameCounter++;
    }
  }

  /**
   * allows renderer to show a marker on who is the client's entity and client specific rendering
   * effects (death screen, etc)
   *
   * @param _id the ID of the entity which the client controls
   */
  public void setClientID(int _id) {
    this.clientID = _id;
  }

  /**
   * @param newMipsMan ghoul which caught mipsman
   * @param entities entities in the game
   * @param map Game map
   * @param renderingLoop loop to render Game (used to pause current rendering)
   * @param inputProcessor loop to process inputs (used to pause input processing)
   */
  public void renderCollisionAnimation(
      Entity newMipsMan,
      Entity[] entities,
      Map map,
      AnimationTimer renderingLoop,
      GameLoop inputProcessor) {
    java.lang.Double[] num = {1.0, 1.0, 1.1, 1.25, 1.4};
    UpDownIterator<java.lang.Double> entitySize = new UpDownIterator<>(num);

    java.lang.Double[] opacity = new java.lang.Double[4];
    for (int i = 0; i < opacity.length; i++) {
      opacity[i] = 0.5 + i * 0.06;
    }
    UpDownIterator<java.lang.Double> backgroundOpacity = new UpDownIterator<>(opacity);

    currentSprite =
        r.getPlayableGhoul(newMipsMan.getClientId()).get(newMipsMan.getFacing().toInt()).get(0);
    final double renderAnimationTime = 0.75 * Math.pow(10, 9);
    double startTime = System.nanoTime();
    final int frames = 40;
    final double frameTime = renderAnimationTime / frames;
    new AnimationTimer() {
      double currentTime = System.nanoTime();
      double multiplier = entitySize.next();
      double opacity = backgroundOpacity.next();

      @Override
      public void handle(long now) {
        if (now - startTime > renderAnimationTime) {
          renderingLoop.start();
          inputProcessor.unpause();
          this.stop();
        } else {
          if (System.nanoTime() - currentTime > frameTime) {
            multiplier = entitySize.next();
            opacity = backgroundOpacity.next();
            currentTime = System.nanoTime();
          }
          renderCollision(newMipsMan, entities, map, multiplier, opacity, currentSprite);
        }
      }
    }.start();
  }

  /**
   * @param newMipsMan ghoul which capture mipsman
   * @param entities entities in the game
   * @param map Game map
   * @param sizeMultiplier size of ghoul to render
   * @param backgroundOpacity transparency of screen
   * @param currentSprite image of ghoul that captured mipsman
   */
  private void renderCollision(
      Entity newMipsMan,
      Entity[] entities,
      Map map,
      double sizeMultiplier,
      double backgroundOpacity,
      Image currentSprite) {
    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFont(geoLarge);
    renderBackground(map);
    renderGameOnly(entities, 0, new PointMap<>(map), null);
    gc.setFill(new Color(0, 0, 0, backgroundOpacity));
    gc.fillRect(0, 0, xResolution, yResolution);

    double x = newMipsMan.getLocation().getX() - 0.5;
    double y = newMipsMan.getLocation().getY() - 0.5;
    setIsoCoord(
        rendCoord,
        x,
        y,
        currentSprite.getHeight() * sizeMultiplier,
        currentSprite.getWidth() * sizeMultiplier);
    currentSprite =
        r.getPlayableGhoul(newMipsMan.getClientId()).get(newMipsMan.getFacing().toInt()).get(0);
    gc.drawImage(
        currentSprite,
        rendCoord.getX(),
        rendCoord.getY(),
        currentSprite.getWidth() * sizeMultiplier,
        currentSprite.getHeight() * sizeMultiplier);
    gc.setFill(intRGBtoColour(playerColours.getRGB(1, newMipsMan.getClientId())));
    gc.fillText(newMipsMan.getName(), xResolution / 2, yResolution * 0.2);
    gc.fillText("CAPTURED MIPS", xResolution / 2, yResolution * 0.45);
    gc.setStroke(Color.WHITE);
    gc.setLineWidth(2 * (yResolution / 768));
    gc.strokeText(newMipsMan.getName(), xResolution / 2, yResolution * 0.2);
    gc.strokeText("CAPTURED MIPS", xResolution / 2, yResolution * 0.45);
  }

  @Deprecated
  /**
   * @param x cartesian x coordinate
   * @param y cartesian Y coordinate
   * @param spriteHeight vertical offset
   * @param spriteWidth horizontal offset
   */
  public Point2D.Double getIsoCoord(double x, double y, double spriteHeight, double spriteWidth) {
    double isoX =
        mapRenderingCorner.getX()
            - (y - x) * (this.tileSizeX / (double) 2)
            + ((tileSizeX - spriteWidth) / 2);
    double isoY =
        mapRenderingCorner.getY()
            + (y + x) * (this.tileSizeY / (double) 2)
            + (tileSizeY - spriteHeight);
    return new Point2D.Double(isoX, isoY);
  }

  /**
   * @param rendCoord coordinate to modify
   * @param x cartesian x coordinate
   * @param y cartesian Y coordinate
   * @param spriteHeight vertical offset
   * @param spriteWidth horizontal offset
   */
  public void setIsoCoord(
      Point2D.Double rendCoord, double x, double y, double spriteHeight, double spriteWidth) {

    rendCoord.setLocation(
        mapRenderingCorner.getX()
            - (y - x) * (this.tileSizeX / (double) 2)
            + ((tileSizeX - spriteWidth) / 2),
        mapRenderingCorner.getY()
            + (y + x) * (this.tileSizeY / (double) 2)
            + (tileSizeY - spriteHeight));
  }

  /**
   * @param e entitiy to render
   * @param timeElapsed time since last frame to decide whether to move to next animation frame
   */
  private void renderEntity(Entity e, HashMap<PowerUps, PowerUp> selfPowerUps, long timeElapsed) {

    // get sprite based on whether they are mipsman or ghoul
    if (e.isMipsman()) {
      currentSprites = r.getPlayableMip(e.getClientId()).get(e.getFacing().toInt());
    } else {
      currentSprites = r.getPlayableGhoul(e.getClientId()).get(e.getFacing().toInt());
    }

    // advance frame if necessary
    if (secondInNanoseconds / e.getAnimationSpeed() < e.getTimeSinceLastFrame()
        && timeElapsed > 0) {
      e.setTimeSinceLastFrame(0);
      for (PowerUp p : selfPowerUps.values()) {
        p.incrementFrame();
      }
      currentAnimationFrame++;
    } else {
      e.setTimeSinceLastFrame(e.getTimeSinceLastFrame() + timeElapsed);
    }

    // get the correct sprite frame
    currentSprite = currentSprites.get(currentAnimationFrame % currentSprites.size());

    double x = e.getLocation().getX() - 0.5;
    double y = e.getLocation().getY() - 0.5;
    // get coordinate to render entity
    setIsoCoord(rendCoord, x, y, currentSprite.getHeight(), currentSprite.getWidth());

    // add explosion if entity is dead
    deathLocation = e.getDeathLocation();
    if (deathLocation != null) {
      Point loc = e.getLocation();
      e.resetDeathLocation();
      setIsoCoord(
          rendCoord, loc.getX(), loc.getY(), currentSprite.getWidth(), currentSprite.getHeight());
      explosionManager.addExplosion(rendCoord.getX(), rendCoord.getY());
    }

    // show flashing respawn animation
    if (e.isDead() && e.getDeathCounter() > e.getDeathTime() * 0.5) {
      if (secondInNanoseconds / e.getAnimationSpeed() < e.getTimeSinceLastFrame()
          && timeElapsed > 0) {
        e.toggleHidden();
      }
      if (!e.getHidden()) {
        gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());
      }
      return;
    }

    // don't render powerup effects if dead
    if (e.isDead()) {
      return;
    }

    gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY());

    if (selfPowerUps != null) {
      renderPowerUpEffects(e, selfPowerUps, rendCoord);
    }
    // render marker for entity
    if (e.getClientId() != clientID && !e.isMipsman()) {
      return;
    }

    // render the marker of Mipsman/Client if neccessary
    currentSprite = (e.isMipsman()) ? r.getMipMarker() : r.getMClientMarker();
    setIsoCoord(
        rendCoord,
        x,
        y,
        currentSprite.getHeight() + currentSprite.getHeight(),
        currentSprite.getWidth());

    gc.drawImage(currentSprite, rendCoord.getX(), rendCoord.getY() - currentSprite.getHeight() * 2);
  }

  /**
   * @param e Entity which has the powerup
   * @param selfPowerUps Powerups that affects the entity
   * @param rendCoord where to render the powerups
   */
  private void renderPowerUpEffects(
      Entity e, HashMap<PowerUps, PowerUp> selfPowerUps, Double rendCoord) {
    if (e.isSpeeding()) {
      PowerUp speed = selfPowerUps.get(PowerUps.SPEED);
      ArrayList<Image> sprites = r.getPowerUps().get(PowerUps.SPEED);
      gc.drawImage(
          sprites.get(speed.getCurrentFrame() % sprites.size()),
          rendCoord.getX(),
          rendCoord.getY());
    }
    if (e.isInvincible()) {
      PowerUp invincible = selfPowerUps.get(PowerUps.INVINCIBLE);
      gc.drawImage(
          r.getPowerUps()
              .get(PowerUps.INVINCIBLE)
              .get(invincible.getCurrentFrame() % r.getPowerUps().get(PowerUps.INVINCIBLE).size()),
          rendCoord.getX(),
          rendCoord.getY());
    }
    // is the entity stunned?
    if (e.isStunned()) {
      gc.drawImage(r.getPowerUps().get(PowerUps.WEB).get(0), rendCoord.getX(), rendCoord.getY());
    }
  }

  /**
   * render the background image and pyramid under game map
   *
   * @param map game map
   */
  private void renderBackground(Map map) {
    // render backing image
    gc.drawImage(background, 0, 0, xResolution, yResolution);

    // Render map base
    setIsoCoord(rendCoord, -1, -1, tileSizeY, tileSizeX);
    Point2D.Double topLeft =
        new Double(rendCoord.getX() + 0.5 * tileSizeX, rendCoord.getY() - 0.5 * MAP_BORDER);

    setIsoCoord(rendCoord, map.getMaxX(), -1, tileSizeY, tileSizeX);
    Point2D.Double topRight =
        new Double(rendCoord.getX() + MAP_BORDER + tileSizeX, rendCoord.getY() + 0.5 * tileSizeY);

    setIsoCoord(rendCoord, -1, map.getMaxY(), tileSizeY, tileSizeX);
    Point2D.Double bottomLeft =
        new Double(rendCoord.getX() - 0.5 * MAP_BORDER, rendCoord.getY() + 0.5 * tileSizeY);

    setIsoCoord(rendCoord, map.getMaxX(), map.getMaxY(), tileSizeY, tileSizeX);
    Point2D.Double bottomRight =
        new Double(
            rendCoord.getX() + 0.5 * tileSizeX, rendCoord.getY() + 0.5 * MAP_BORDER + tileSizeY);

    // get first colour from palette (lightest tone)
    gc.setFill(intRGBtoColour(palette.getRGB(0, 0)));
    gc.fillPolygon(
        new double[] {topLeft.getX(), topRight.getX(), bottomRight.getX(), bottomLeft.getX()},
        new double[] {topLeft.getY(), topRight.getY(), bottomRight.getY(), bottomLeft.getY()},
        4);

    // Render Pyramid underside

    double yChange = topRight.getX() - bottomRight.getX();

    double percentageXRes = 0.04;
    double ratio = ((percentageXRes * xResolution) / yChange) * (map.getMaxY() / (double) 20);

    double x =
        getIsoCoord(map.getMaxX() / (double) 2, map.getMaxY() / (double) 2, tileSizeY, tileSizeX)
            .getX();
    double y = bottomRight.getY() + yChange * ratio;

    Point2D.Double pyramidVertex = new Point2D.Double(x, y);

    // get third colour from palette (darkest tone)
    gc.setFill(intRGBtoColour(palette.getRGB(2, 0)));
    gc.fillPolygon(
        new double[] {topRight.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[] {topRight.getY(), bottomRight.getY(), pyramidVertex.getY()},
        3);

    // get second colour from palette (medium tone)
    gc.setFill(intRGBtoColour(palette.getRGB(1, 0)));
    gc.fillPolygon(
        new double[] {bottomLeft.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[] {bottomLeft.getY(), bottomRight.getY(), pyramidVertex.getY()},
        3);

    // Draw black outline
    gc.setStroke(Color.BLACK);
    gc.strokePolygon(
        new double[] {bottomLeft.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[] {bottomLeft.getY(), bottomRight.getY(), pyramidVertex.getY()},
        3);

    gc.strokePolygon(
        new double[] {topRight.getX(), bottomRight.getX(), pyramidVertex.getX()},
        new double[] {topRight.getY(), bottomRight.getY(), pyramidVertex.getY()},
        3);

    gc.strokePolygon(
        new double[] {topLeft.getX(), topRight.getX(), bottomRight.getX(), bottomLeft.getX()},
        new double[] {topLeft.getY(), topRight.getY(), bottomRight.getY(), bottomLeft.getY()},
        4);
  }

  /** @return The top right corner coordinate to start rendering game map from */
  private Point2D.Double getMapRenderingCorner() {

    double bottomLeftX = -map.getMaxY() * (this.tileSizeX / (double) 2);
    double topRightX = map.getMaxX() * (this.tileSizeX / (double) 2);
    double mapMidPointX = bottomLeftX + 0.5 * Math.abs(topRightX - bottomLeftX);
    //    System.out.println("offset: " + mapMidPointX);
    return new Point2D.Double((this.xResolution / (double) 2) - mapMidPointX, yResolution / 6);
  }

  /**
   * use to override settings given by the settings class
   *
   * @param x new X resolution
   * @param y new Y resolution
   */
  public void setResolution(int x, int y) {
    r.refreshSettings(x, y, RenderingMode.SMOOTH_SCALING, Settings.getTheme());
    hudRender.setResolution(x, y);
    this.xResolution = x;
    this.yResolution = y;
    this.map = r.getMap();
    this.initMapTraversal(this.map);
    this.tileSizeX = r.getMapTiles().get(0).getWidth();
    this.tileSizeY = r.getMapTiles().get(0).getHeight();
    this.mapRenderingCorner = getMapRenderingCorner();
    this.background = r.getBackground();
    this.palette = r.getBackgroundPalette();
    this.explosionManager.refreshSettings();
  }

  /** refresh fields of renderer based on changes in Settings class */
  public void refreshSettings() {
    r.refreshSettings();
    hudRender.setResolution(Settings.getxResolution(), Settings.getyResolution());
    this.xResolution = Settings.getxResolution();
    this.yResolution = Settings.getyResolution();
    this.map = r.getMap();
    this.initMapTraversal(this.map);
    this.mapRenderingCorner = getMapRenderingCorner();
    this.background = r.getBackground();
    this.palette = r.getBackgroundPalette();
    this.explosionManager.refreshSettings();
  }

  /**
   * set to true to reset map traversal
   *
   * @param b whether map should be refreshed
   */
  public void setRefreshMap(boolean b) {
    this.refreshMap = b;
  }
}
