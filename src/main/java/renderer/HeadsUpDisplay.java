package renderer;

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
import objects.Entity;
import utils.CircularIterator;
import utils.ResourceLoader;
import utils.enums.PowerUp;

public class HeadsUpDisplay {

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
  LinkedList<PowerUp> items = null;
  private Image inventory;
  private int id = 0;
  private boolean randomPrimary = false;
  private boolean randomSecondary = false;

  private final int randomFrames = 180;
  private int primaryFrameCounter = 0;
  private int secondaryFrameCounter = 0;
  private long currentTime = 0;
  private int currentRandomFrame = 0;


  public HeadsUpDisplay(GraphicsContext gc, int xResolution, int yResolution,
      ResourceLoader r) {
    this.resourceLoader = r;
    this.gc = gc;
    this.xResolution = xResolution;
    this.yResolution = yResolution;
    this.playerColours = r.getPlayerPalette();
    this.inventory = r.getInventory(id);
    this.powerUpsIcon = r.getPowerUpIcons();

    Integer[] powerupIDs = new Integer[PowerUp.values().length];
    for (int i = 0; i < powerupIDs.length; i++) {
      powerupIDs[i] = i;
    }
    this.iconIterator = new CircularIterator<Integer>(powerupIDs);

    final double fontRatio = 0.07;
    try {
      this.geoLarge =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              xResolution * fontRatio);
      this.geoSmall =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/font/Geo-Regular.ttf")),
              0.4 * xResolution * fontRatio);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static String padRight(String s, int n) {
    if (s.length() > n) {
      return s.substring(0, n);
    }
    return String.format("%-" + n + "s", s);
  }

  public static String padLeft(String s, int n) {
    if (s.length() > n) {
      return s.substring(0, n);
    }
    return String.format("%1$" + n + "s", s);
  }

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

  public void renderHUD(Entity[] entities, int time) {
    Entity[] entities_ = entities.clone();
    Arrays.sort(entities_, (o1, o2) -> -Integer.compare(o1.getScore(), o2.getScore()));

    gc.setFill(new Color(1, 1, 1, 0.8));
    gc.setStroke(Color.BLACK);
    int rowGap = (int) (0.06 * yResolution);

    for (int i = 0; i < entities_.length; i++) {

      Entity e = entities_[i];

      gc.setFill(Renderer.intRGBtoColour(playerColours.getRGB(0, e.getClientId())));

      String place = padRight(integerToOrdinal(i + 1), 4);
      String name = padRight(e.getName(), 10);
      String score = padLeft(Integer.toString(e.getScore()), 4);
      String currentPlayerScoreLine = name + " " + score;

      gc.setFont(geoSmall);
      gc.setTextAlign(TextAlignment.LEFT);
      gc.fillText(currentPlayerScoreLine, xResolution * 0.75, 40 + rowGap * i);
      gc.strokeText(currentPlayerScoreLine, xResolution * 0.75, 40 + rowGap * i);
      gc.setFill(Color.WHITE);
      gc.setTextAlign(TextAlignment.RIGHT);
      gc.fillText(place, xResolution * 0.75, 40 + rowGap * i);

    }
    gc.setFont(geoLarge);
    gc.setTextAlign(TextAlignment.CENTER);
    gc.fillText(Integer.toString(time), xResolution * 0.5, yResolution * 0.1);
  }

  public void renderInventory(Entity clientEntity, long timeElapsed) {
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
      items = (LinkedList<PowerUp>) currentItems.clone();
    } else if (items.size() != currentItems.size()) {
      if (items.size() == 1 && currentItems.size() == 2) {
        randomSecondary = true;
      } else if (items.size() == 0 && currentItems.size() == 1) {
        randomPrimary = true;
      } else if (items.size() == 2 && currentItems.size() == 1 && randomSecondary) {
        randomSecondary = false;
        randomPrimary = true;
      } else {
        randomPrimary = false;
        randomSecondary = false;
      }
      items = (LinkedList<PowerUp>) currentItems.clone();
    }

    currentTime += timeElapsed;
    final long frameTime = (long) Math.pow(10, 9) / 10;
    if (currentTime > frameTime) {
      currentRandomFrame = iconIterator.next();
      currentTime = 0;
    }

    if (randomPrimary) {
      renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(),
          powerUpsIcon.get(currentRandomFrame));
      primaryFrameCounter++;
    } else {
      if (items.size() > 0) {
        renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(),
            powerUpsIcon.get(items.get(0).toInt()));
      } else {
        renderPrimaryInventoryBox(primaryInventoryCoord.getX(), primaryInventoryCoord.getY(), null);
      }
    }

    if (randomSecondary) {
      renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
          powerUpsIcon.get(currentRandomFrame));
      secondaryFrameCounter++;
    } else {
      if (items.size() > 1) {
        renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
            powerUpsIcon.get(items.get(1).toInt()));
      } else {
        renderSecondaryInventoryBox(secondaryInventoryCoord.getX(), secondaryInventoryCoord.getY(),
            null);
      }
    }

    if (primaryFrameCounter > randomFrames) {
      primaryFrameCounter = 0;
      randomPrimary = false;
    }
    if (secondaryFrameCounter > randomFrames) {
      secondaryFrameCounter = 0;
      randomSecondary = false;
    }
  }

  private void renderPrimaryInventoryBox(double x, double y, Image item) {
    drawInventoryIcon(x, y, inventory.getWidth(), item);
    gc.drawImage(inventory, x, y);
  }

  private void renderSecondaryInventoryBox(double x, double y, Image item) {
    drawInventoryIcon(x, y, inventory.getWidth() * secondaryInventoryRatio, item);
    gc.drawImage(inventory, x, y, 0.7 * inventory.getWidth(), 0.7 * inventory.getHeight());
  }

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

  public void setResolution(int x, int y) {
    this.xResolution = x;
    this.yResolution = y;
    this.inventory = resourceLoader.getInventory(id);
  }
}
