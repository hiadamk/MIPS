package renderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import objects.Entity;

public class HeadsUpDisplay {

  private final GraphicsContext gc;
  private int xResolution;
  private int yResolution;
  private Font geoLarge = null;
  private Font geoSmall = null;
  private BufferedImage playerColours;

  public HeadsUpDisplay(GraphicsContext gc, int xResolution, int yResolution,
      BufferedImage playerColours) {
    this.gc = gc;
    this.xResolution = xResolution;
    this.yResolution = yResolution;
    this.playerColours = playerColours;
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
      gc.setTextAlign(TextAlignment.RIGHT);
      gc.setFill(Renderer.intRGBtoColour(playerColours.getRGB(0, e.getClientId())));

      String place = padRight(integerToOrdinal(i + 1), 4);
      String name = padRight(e.getName(), 10);
      String score = padRight(Integer.toString(e.getScore()), 5);
      String currentPlayerScoreLine = name + " " + score;

      gc.setFont(geoSmall);
      gc.fillText(currentPlayerScoreLine, xResolution * 0.99, 40 + rowGap * i);
      gc.strokeText(currentPlayerScoreLine, xResolution * 0.99, 40 + rowGap * i);
      gc.setFill(Color.WHITE);
      gc.fillText(place, xResolution * 0.78, 40 + rowGap * i);
      gc.setFont(geoLarge);
      gc.setTextAlign(TextAlignment.CENTER);
      gc.fillText(Integer.toString(time), xResolution * 0.5, yResolution * 0.1);
    }
  }

  public void setResolution(int x, int y) {
    this.xResolution = x;
    this.yResolution = y;
  }
}
