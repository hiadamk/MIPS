package com.lordsofmidnight.renderer;

import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.CircularIterator;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class to deal with the in game HUD
 */
public class HeadsUpDisplay {

  public static final double ROW_GAP = 0.06;
  private final GraphicsContext gc;
  private final ResourceLoader resourceLoader;
  private final CircularIterator<Integer> iconIterator;
  private final ArrayList<Image> powerUpsIcon;
  private int xResolution;
  private int yResolution;
  private Font geoLarge = null;
  private Font geoSmall = null;
  private BufferedImage playerColours;
  private final double secondaryInventoryRatio = 0.7;
  LinkedList<com.lordsofmidnight.objects.powerUps.PowerUp> items = null;
  private Image inventory;
  private int id = 0;
  private boolean randomPrimary = false;
  private boolean randomSecondary = false;

  private final int randomFrames = 180;
  private int primaryFrameCounter = 0;
  private int secondaryFrameCounter = 0;
  private long currentTime = 0;
  private int currentRandomFrame = 0;

  /**
   *
   * @param gc
   * @param xResolution game X resolution
   * @param yResolution game Y resolution
   * @param r resource loader (for the same theme, pass the same one that is used for renderer)
   */
  public HeadsUpDisplay(GraphicsContext gc, int xResolution, int yResolution,
      ResourceLoader r) {
    this.resourceLoader = r;
    this.gc = gc;
    this.xResolution = xResolution;
    this.yResolution = yResolution;
    this.playerColours = r.getPlayerPalette();
    this.inventory = r.getInventory(id);
    this.powerUpsIcon = r.getPowerUpIcons();

    Integer[] powerupIDs = new Integer[PowerUps.values().length];
    for (int i = 0; i < powerupIDs.length; i++) {
      powerupIDs[i] = i;
    }
    this.iconIterator = new CircularIterator<>(powerupIDs);
    setResolution(xResolution, yResolution);
  }

  /**
   * right pad string. if pad length is less than string length, take substring
   * @param s String to pad
   * @param n length to pad by
   * @return right padded string
   */
  public static String padRight(String s, int n) {
    if (s.length() > n) {
      return s.substring(0, n);
    }
    return String.format("%-" + n + "s", s);
  }

  /**
   * left pad string. if pad length is less than string length, take substring
   * @param s String to pad
   * @param n length to pad by
   * @return left padded string
   */
  public static String padLeft(String s, int n) {
    if (s.length() > n) {
      return s.substring(0, n);
    }
    return String.format("%1$" + n + "s", s);
  }

  /**
   * converts integer to ordinal - e.g. 1=1st,22=22nd,34=34th,etc
   * @param n Integer to convert to ordinal
   * @return
   */
  public static String integerToOrdinal(int n) {
    String[] ords = new String[]{"st", "nd", "rd"};
    int remainder = n % 100;
    switch (remainder) {
      case 11:
      case 12:
      case 13:
        return n + "th";
      case 1:
      case 2:
      case 3:
        return n + ords[remainder - 1];
      default:
        return n + "th";
    }
  }

  /**
   * MUST be called from the javaFX application thread
   * @param entities
   * @param time
   */
  public void renderHUD(Entity[] entities, int time) {
    Entity[] entities_ = entities.clone();

    //sort entities by their score for leaderboard
    Arrays.sort(entities_, (o1, o2) -> -Integer.compare(o1.getScore(), o2.getScore()));

    gc.setFill(new Color(1, 1, 1, 0.8));
    gc.setStroke(Color.BLACK);

    int rowGap = (int) (ROW_GAP * yResolution);
    final double SCORE_BOARD_X = xResolution * 0.82;
    final double SCORE_BOARD_Y = 0.07 * yResolution;

    //render each entities scoreboard line
    for (int i = 0; i < entities_.length; i++) {

      Entity e = entities_[i];

      gc.setFill(Renderer.intRGBtoColour(playerColours.getRGB(0, e.getClientId())));

      //format score line
      String place = padRight(integerToOrdinal(i + 1), 5);
      String name = padRight(e.getName(), 10);
      String score = padLeft(Integer.toString(e.getScore()), 4);
      String currentPlayerScoreLine = name + " " + score;

      //render score and place
      gc.setFont(geoSmall);
      gc.setTextAlign(TextAlignment.LEFT);
      gc.fillText(currentPlayerScoreLine, SCORE_BOARD_X, SCORE_BOARD_Y + rowGap * i);
      gc.strokeText(currentPlayerScoreLine, SCORE_BOARD_X, SCORE_BOARD_Y + rowGap * i);
      gc.setFill(Color.WHITE);
      gc.setTextAlign(TextAlignment.RIGHT);
      gc.fillText(place, SCORE_BOARD_X, SCORE_BOARD_Y + rowGap * i);

    }
    //render time
    gc.setFont(geoLarge);
    gc.setTextAlign(TextAlignment.CENTER);
    gc.fillText(Integer.toString(time), xResolution * 0.5, yResolution * 0.1);
  }

  /**
   *
   * @param clientEntity the entity's inventory to render
   * @param timeElapsed time since last method call
   */
  public void renderInventory(Entity clientEntity, long timeElapsed) {
    gc.setLineWidth(1);
    if (clientEntity.getClientId() != id) {
      this.inventory = resourceLoader.getInventory(clientEntity.getClientId());
      this.id = clientEntity.getClientId();
    }
    LinkedList currentItems = clientEntity.getItems();

    Point2D.Double primaryInventoryCoord = new Point2D.Double(0.03 * xResolution,
        0.9 * yResolution - inventory.getHeight());
    Point2D.Double secondaryInventoryCoord = new Double(
        primaryInventoryCoord.getX() + inventory.getWidth(),
        0.90 * yResolution - inventory.getHeight() * secondaryInventoryRatio);

    //find if new item has been picked up
    if (items == null) {
      items = (LinkedList<com.lordsofmidnight.objects.powerUps.PowerUp>) currentItems.clone();
    } else if (items.size() != currentItems.size()) {
      //had one item then picked up another
      if (items.size() == 1 && currentItems.size() == 2) {
        randomSecondary = true;
      }
      //had no items then picked up another
      else if (items.size() == 0 && currentItems.size() == 1) {
        randomPrimary = true;
      }
      //first item used while second was still rolling random
      else if (items.size() == 2 && currentItems.size() == 1 && randomSecondary) {
        //transfer random to primary box
        randomSecondary = false;
        randomPrimary = true;
      }
      //otherwise just render whatever is in the inventory
      else {
        randomPrimary = false;
        randomSecondary = false;
      }
      items = (LinkedList<com.lordsofmidnight.objects.powerUps.PowerUp>) currentItems.clone();
    }

    //advance random frame
    currentTime += timeElapsed;
    final long frameTime = (long) Math.pow(10, 9) / 10;
    if (currentTime > frameTime) {
      currentRandomFrame = iconIterator.next();
      currentTime = 0;
    }

    //render primary box item or random roll
    if (randomPrimary) {
      renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(),
          powerUpsIcon.get(currentRandomFrame));
      primaryFrameCounter++;
    } else if (items.size() > 0) {
      renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(),
          powerUpsIcon.get(items.get(0).toInt()));

    }

    //render secondary box item or random roll
    if (randomSecondary) {
      renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
          powerUpsIcon.get(currentRandomFrame));
      secondaryFrameCounter++;
    } else if (items.size() > 1) {
      renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
          powerUpsIcon.get(items.get(1).toInt()));
    }

    //if there are less than two items, make sure secondary box shows empty
    if (items.size() <= 1) {
      renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
          null);
    }
    //if there are zero or less, make sure secondary and primary box shows empty
    if (items.size() <= 0) {
      renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(), null);
    }

    //end random primary box if neccessary
    if (primaryFrameCounter > randomFrames) {
      primaryFrameCounter = 0;
      randomPrimary = false;
    }

    //end random secondary box if neccessary
    if (secondaryFrameCounter > randomFrames) {
      secondaryFrameCounter = 0;
      randomSecondary = false;
    }
  }

  /**
   * @param x X rendering coordinate of primary box
   * @param y Y rendering coordinary of primary box
   * @param item Image to render (use null if no item)
   */
  private void renderPrimaryInventoryBox(double x, double y, Image item) {
    drawInventoryIcon(x, y, inventory.getWidth(), item);
    gc.drawImage(inventory, x, y);
  }

  /**
   * @param x X rendering coordinate of primary box
   * @param y Y rendering coordinary of primary box
   * @param item Image to render (use null if no item)
   */
  private void renderSecondaryInventoryBox(double x, double y, Image item) {
    drawInventoryIcon(x, y, inventory.getWidth() * secondaryInventoryRatio, item);
    gc.drawImage(inventory, x, y, 0.7 * inventory.getWidth(), 0.7 * inventory.getHeight());
  }

  /**
   * @param x X rendering coordinate of powerup icon
   * @param y Y rendering coordinate of powerup icon
   * @param width width to render powerup icon
   * @param item powerup to render
   */
  private void drawInventoryIcon(double x, double y, double width, Image item) {
    final double inventoryIconOffset = 0.0675;
    gc.setFill(Color.GREY);
    width = (1 - inventoryIconOffset * 2) * width;
    if (item == null) {
      gc.fillRect(x + inventoryIconOffset * width, y + inventoryIconOffset * width, width, width);
    } else {
      gc.drawImage(item, x + inventoryIconOffset * width, y + inventoryIconOffset * width, width,
          width);
    }
  }

  /**
   * @param x new X resolution
   * @param y new Y resolution
   */
  public void setResolution(int x, int y) {
    this.xResolution = x;
    this.yResolution = y;
    this.inventory = resourceLoader.getInventory(id);

    final double fontRatio = 0.07;
    try {
      this.geoLarge =
          Font.loadFont(
              new FileInputStream(
                  new File("src/main/resources/font/Geo-Regular.ttf")),
              xResolution * fontRatio);
      this.geoSmall =
          Font.loadFont(
              new FileInputStream(
                  new File("src/main/resources/font/Geo-Regular.ttf")),
              0.4 * xResolution * fontRatio);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    this.playerColours = resourceLoader.getPlayerPalette();
  }

  /**
   *
   * @param timeUntilRespawn time until entity will respawn
   * @param clientEntity entity which is dead
   */
  public void renderDeathScreen(int timeUntilRespawn,Entity clientEntity) {
    //dim game screen
    gc.setStroke(Color.BLACK);
    gc.setFill(new Color(0, 0, 0, 0.65));
    gc.fillRect(0, 0, xResolution, yResolution);
    gc.setFont(geoLarge);
    gc.setFill(Color.WHITE);
    gc.setTextAlign(TextAlignment.CENTER);
    gc.fillText("RESPAWNING IN: " + timeUntilRespawn, xResolution / 2, yResolution / 2);

    //render who killed this player
    String killer = clientEntity.getKilledBy();
    int killerID = Integer.parseInt(killer.substring(killer.length()-1));
    killer = killer.substring(0,killer.length()-1);
    //use colour scheme of killer
    gc.setFill(Renderer.intRGBtoColour(playerColours.getRGB(1,killerID)));
    gc.fillText("KILLED BY " + killer,xResolution/2,yResolution*0.4);
    gc.setStroke(Color.WHITE);
    gc.setLineWidth(2*(yResolution/768));
    gc.strokeText("KILLED BY " + killer,xResolution/2,yResolution*0.4);
  }
}
