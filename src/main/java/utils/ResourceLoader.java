package utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ResourceLoader {

  private final String BASE_DIR;

  private Map map;
  private ArrayList<ArrayList<BufferedImage>> mipSprites;
  private ArrayList<ArrayList<BufferedImage>> ghoulSprites;

  public ResourceLoader(String baseDir) {
    BASE_DIR = baseDir;
    this.loadMap("default");
    this.loadPlayableMip("default");
    this.loadPlayableGhoul("default");
  }

  public static void main(String[] args) {
    ResourceLoader rl = new ResourceLoader("src/main/resources/");
    rl.loadMap("default");
    System.out.println(
        Arrays.deepToString(rl.getMap().raw()).replace("], ", "]\n").replace("[[", "[")
            .replace("]]", "]"));
  }

  /**
   * @param name name of map: if file is default.png the name is default reads a png map image,
   * converts rbg color pixels into map tile numbers
   */
  public void loadMap(String name) {

    BufferedImage mapImage = loadImageFile("maps/", name);

    int width = mapImage.getWidth();
    int height = mapImage.getHeight();
    int[][] map_ = new int[width][height];

    //convert image into game map
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map_[x][y] = MapColour.toTile(mapImage.getRGB(x, y)); //change rgb int into a map int
      }
    }
    this.map = new Map(map_);
  }

  public Map getMap() {
    return map;
  }

  /**
   * @param theme name of folder which contains the assets for that theme
   */
  public void loadPlayableMip(String theme) {
    final int spriteWidth = 39;
    final int spriteHeight = 36;
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "mip");

    this.mipSprites = splitSpriteSheet(spriteWidth, spriteHeight,
        spriteSheet);
  }

  /**
   * creates coloured sprites of mip according to the color id selected
   *
   * @param colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableMip(int colourID) {
    return bufferedToJavaFxImage(this.mipSprites);
  }

  /**
   * @param theme name of folder which contains the assets for that theme
   */
  public void loadPlayableGhoul(String theme) {
    final int spriteWidth = 39;
    final int spriteHeight = 36;
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "ghoul");

    this.ghoulSprites = splitSpriteSheet(spriteWidth, spriteHeight,
        spriteSheet);
  }

  /**
   * creates coloured sprites of ghoul according to the color id selected
   *
   * @param colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableGhoul(int colourID) {
    return bufferedToJavaFxImage(this.ghoulSprites);
  }

  /**
   * @param folderPath folder that contains the images
   * @param name name of image to load (no file ending)
   */
  private BufferedImage loadImageFile(String folderPath, String name) {
    String path = BASE_DIR + folderPath + name + ".png";
    File mapFile = new File(path);
    BufferedImage image = null;

    try {
      image = ImageIO.read(mapFile);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    return image;
  }

  /**
   * @param spriteWidth x dimension of an individual sprite
   * @param spriteHeight y dimension of an individual sprite
   * @param spriteSheet loaded image containing sprites in a grid - each row contains a direction
   * and each column contains an animation frame of that direction
   */
  private ArrayList<ArrayList<BufferedImage>> splitSpriteSheet(int spriteWidth, int spriteHeight,
      BufferedImage spriteSheet) {
    ArrayList<ArrayList<BufferedImage>> _mipSprites = new ArrayList<>();

    for (int i = 0; i < spriteSheet.getWidth() / spriteWidth; i++) {
      ArrayList<BufferedImage> directionAnimation = new ArrayList<>();
      for (int j = 0; j < spriteSheet.getHeight() / spriteHeight; j++) {
        directionAnimation.add(
            spriteSheet.getSubimage(i * spriteWidth, j * spriteHeight, spriteWidth, spriteHeight));
      }
      _mipSprites.add(directionAnimation);
    }
    return _mipSprites;
  }

  /**
   *
   * @param sprites BufferedImages to convert to JavaFX images
   * @return converted images in same arraylist structure
   */
  private ArrayList<ArrayList<Image>> bufferedToJavaFxImage(
      ArrayList<ArrayList<BufferedImage>> sprites) {
    ArrayList<ArrayList<Image>> convertedSprites = new ArrayList<>();

    for (ArrayList<BufferedImage> imgs : sprites) {
      ArrayList<Image> tmp = new ArrayList<>();
      for (BufferedImage img : imgs) {
        tmp.add(SwingFXUtils.toFXImage(img, null));
      }
      convertedSprites.add(tmp);
    }
    return convertedSprites;
  }
}
