package utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import utils.enums.MapElement;

public class ResourceLoader {

  private final String BASE_DIR;

  private String[] themes;

  private Map map;

  private final int spriteWidth = 39;
  private final int spriteHeight = 36;

  private ArrayList<ArrayList<BufferedImage>> mipSprites;
  private BufferedImage mipPalette;
  private int mipColourID;

  private ArrayList<ArrayList<BufferedImage>> ghoulSprites;
  private BufferedImage ghoulPalette;
  private int ghoulColourID;

  private ArrayList<BufferedImage> mapTiles;

  private BufferedImage background;
  private BufferedImage backgroundPalette;

  private BufferedImage mipMarker;
  private BufferedImage clientMarker;

  /**
   * @param baseDir path to the resources folder
   */
  public ResourceLoader(String baseDir) {
    BASE_DIR = baseDir;
    //BASE_DIR = "";

    this.loadMap("default");
    this.loadPlayableMip("default");
    this.loadPlayableGhoul("default");
    this.loadMapTiles("default");
    this.loadBackground("default");
    this.loadThemes();

  }

  public static void main(String[] args) {
    ResourceLoader rl = new ResourceLoader("src/main/resources/");
    rl.loadMap("default");
    System.out.println(
        Arrays.deepToString(rl.getMap().raw())
            .replace("], ", "]\n")
            .replace("[[", "[")
            .replace("]]", "]"));

    rl.setResolution(3840, 2160, false);
  }

  private void loadThemes() {
    File[] themeFolders = new File(BASE_DIR + "sprites/").listFiles(File::isDirectory);
    String[] _themes = new String[themeFolders.length];

    for (int i = 0; i < themeFolders.length; i++) {
      _themes[i] = themeFolders[i].getName();
    }

    this.themes = _themes;
  }

  public String[] getThemes() {
    return this.themes;
  }

  /**
   * @param name name of map: if file is default.png the name is default reads a png map image,
   * converts rbg colour pixels into map tile numbers
   */
  public void loadMap(String name) {

    BufferedImage mapImage = loadImageFile("maps/", name);

    int width = mapImage.getWidth();
    int height = mapImage.getHeight();
    int[][] map_ = new int[width][height];

    // convert image into game map
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map_[x][y] = MapElement.colourToID(mapImage.getRGB(x, y)); // change rgb int into a map int
      }
    }
    this.map = new Map(map_);
  }

  public Map getMap() {
    return map;
  }

  private void resizeSpritesSmooth(ArrayList<BufferedImage> sprites, double ratio) {
    BufferedImage temp;

    for (int i = 0; i < sprites.size(); i++) {
      temp = sprites.get(i);
      int newWidth = (int) (temp.getWidth() * ratio);
      int newHeight = (int) (temp.getHeight() * ratio);
      BufferedImage resizedSprite = new BufferedImage(newWidth, newHeight,
          BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g = resizedSprite.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
          RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

      g.drawImage(temp, 0, 0, newWidth, newHeight, null);
      g.dispose();

      sprites.set(i, resizedSprite);
    }

  }

  /**
   * @param theme name of folder which contains the assets for that theme
   */
  public void loadPlayableMip(String theme) {
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "mip");
    this.mipSprites = splitSpriteSheet(spriteWidth, spriteHeight, spriteSheet);

    this.mipPalette = loadImageFile("sprites/" + theme + "/playable/", "mip_palette");
    this.mipColourID = 0;
  }

  /**
   * @param x new x resolution
   * @param y new y resolution
   * @param integerScaling if true, only scales images by integer scale factor
   */
  public void setResolution(int x, int y, boolean integerScaling) {
    double mapToScreenRatio = 0.7;

    //find dimensions of rendered map we want based on mapToScreenRatio
    int targetX = (int) (x * mapToScreenRatio);
    int targetY = (int) (y * mapToScreenRatio);

    int tileHeight = this.mapTiles.get(MapElement.FLOOR.toInt()).getHeight();
    int tileWidth = this.mapTiles.get(MapElement.FLOOR.toInt()).getWidth();

    //find the dimensions of the rendered map based on default sprite scaling
    int currentX = tileHeight + (int) (0.5 * tileHeight * map.getMaxX());
    int currentY = tileHeight + (int) (0.5 * tileWidth * map.getMaxY());

    //choose the smallest ratio to make sure map fits on screen
    double ratio = Math.min(targetX / (double) currentX, (double) targetY / currentY);
    System.out.println(ratio);

    ratio = (integerScaling) ? Math.floor(ratio) : ratio;

    for (int i = 0; i < mipSprites.size(); i++) {
      resizeSpritesSmooth(mipSprites.get(i), ratio);
    }

    for (int i = 0; i < mipSprites.size(); i++) {
      resizeSpritesSmooth(ghoulSprites.get(i), ratio);
    }

    resizeSpritesSmooth(mapTiles, ratio);

  }


  /**
   * creates coloured sprites of mip according to the colour id selected
   *
   * @param _colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableMip(int _colourID) {

    for (int i = 0; i < this.mipSprites.size(); i++) {
      ArrayList<BufferedImage> tmp = new ArrayList<>();
      for (int j = 0; j < this.mipSprites.get(i).size(); j++) {
        tmp.add(
            recolourSprite(
                this.mipSprites.get(i).get(j), this.mipPalette, this.mipColourID, _colourID));
      }
      this.mipSprites.set(i, tmp);
    }
    this.mipColourID = _colourID;
    return bufferedToJavaFxImage2D(this.mipSprites);
  }

  /**
   * @param theme name of folder which contains the assets for that theme
   */
  public void loadPlayableGhoul(String theme) {
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "ghoul");

    this.ghoulSprites = splitSpriteSheet(spriteWidth, spriteHeight, spriteSheet);

    this.ghoulPalette = loadImageFile("sprites/" + theme + "/playable/", "ghoul_palette");
    this.ghoulColourID = 0;
  }

  /**
   * creates coloured sprites of ghoul according to the colour id selected
   *
   * @param _colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableGhoul(int _colourID) {
    for (ArrayList<BufferedImage> imgs : this.ghoulSprites) {
      for (BufferedImage sprite : imgs) {
        recolourSprite(sprite, this.ghoulPalette, this.ghoulColourID, _colourID);
      }
    }
    this.ghoulColourID = _colourID;
    return bufferedToJavaFxImage2D(this.ghoulSprites);
  }

  /**
   * @param theme name of folder which contains the assets for that theme
   */
  public void loadMapTiles(String theme) {
    ArrayList<BufferedImage> _mapTiles = new ArrayList<>();
    for (MapElement m : MapElement.values()) {
      _mapTiles.add(loadImageFile("sprites/" + theme + "/tiles/", m.toString()));
    }
    this.mapTiles = _mapTiles;
  }

  public ArrayList<Image> getMapTiles() {
    return bufferedToJavaFxImage(this.mapTiles);
  }

  public void loadBackground(String theme) {
    this.background = loadImageFile("sprites/" + theme + "/backgrounds/", theme);
    this.backgroundPalette = loadImageFile("sprites/" + theme + "/backgrounds/",
        theme + "_palette");

  }

  public Image getBackground() {
    return SwingFXUtils.toFXImage(this.background, null);
  }

  public BufferedImage getBackgroundPalette() {
    return this.backgroundPalette;
  }

  public void loadMipMarker(String theme) {
    this.mipMarker = loadImageFile("sprites/" + theme + "/misc/", "mip_marker");
  }

  public Image getMipMarker() {
    return SwingFXUtils.toFXImage(this.mipMarker, null);
  }

  public void loadClientMarker(String theme) {
    this.clientMarker = loadImageFile("sprites/" + theme + "/misc/", "client_marker");
  }

  public Image getMClientMarker() {
    return SwingFXUtils.toFXImage(this.clientMarker, null);
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

    /*code from: https://www.codeproject.com/Questions/542826/getRBGplusdoesn-tplusreturnplusvalueplussetplusb
    creates a new buffered image so that rbg colours can be edited.
     */
    BufferedImage imgUnindexedColourModel =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    imgUnindexedColourModel.getGraphics().drawImage(image, 0, 0, null);
    // end code. Accessed on 29/01/2019

    return imgUnindexedColourModel;
  }

  /**
   * @param spriteWidth x dimension of an individual sprite
   * @param spriteHeight y dimension of an individual sprite
   * @param spriteSheet loaded image containing sprites in a grid - each row contains a direction
   * and each column contains an animation frame of that direction
   */
  private ArrayList<ArrayList<BufferedImage>> splitSpriteSheet(
      int spriteWidth, int spriteHeight, BufferedImage spriteSheet) {
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
   * @param sprites BufferedImages to convert to JavaFX images
   * @return converted images in same arraylist structure
   */
  private ArrayList<Image> bufferedToJavaFxImage(
      ArrayList<BufferedImage> sprites) {
    ArrayList<Image> convertedSprites = new ArrayList<>();

    for (BufferedImage img : sprites) {
      convertedSprites.add(SwingFXUtils.toFXImage(img, null));
    }
    return convertedSprites;
  }

  private ArrayList<ArrayList<Image>> bufferedToJavaFxImage2D(
      ArrayList<ArrayList<BufferedImage>> sprites) {
    ArrayList<ArrayList<Image>> convertedSprites = new ArrayList<>();
    for (ArrayList<BufferedImage> imgs : sprites) {
      convertedSprites.add(bufferedToJavaFxImage(imgs));
    }
    return convertedSprites;
  }

  /**
   * @param sprite image to recolour
   * @param palette colours to choose from
   * @param oldPaletteRow currently used palette row
   * @param newPaletteRow palette row to replace with
   * @return recoloured image
   */
  private BufferedImage recolourSprite(
      BufferedImage sprite, BufferedImage palette, int oldPaletteRow, int newPaletteRow) {
    if (newPaletteRow == oldPaletteRow) {
      return sprite;
    }

    // loop through colours on palette
    for (int i = 0; i < palette.getWidth(); i++) {
      // iterate through every pixel of sprite
      for (int x = 0; x < sprite.getWidth(); x++) {
        for (int y = 0; y < sprite.getHeight(); y++) {
          if (sprite.getRGB(x, y) == palette.getRGB(i, oldPaletteRow)) {
            sprite.setRGB(x, y, palette.getRGB(i, newPaletteRow));
          }
        }
      }
    }

    return sprite;
  }
}
