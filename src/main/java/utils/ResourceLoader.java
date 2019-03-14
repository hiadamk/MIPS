package utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import utils.enums.MapElement;
import utils.enums.PowerUp;
import utils.enums.RenderingMode;

public class ResourceLoader {

  private final String BASE_DIR;
  private RenderingMode renderingMode = Settings.getRenderingMode();
  private int xResolution = Settings.getxResolution();
  private int yResolution = Settings.getyResolution();
  private String theme = Settings.getTheme();

  private final int spriteWidth = 39;
  private final int spriteHeight = 36;
  private Map map;
  private ArrayList<ArrayList<BufferedImage>> mipSprites;
  private ArrayList<ArrayList<BufferedImage>> mipOutlineSprites;
  private BufferedImage mipPalette;

  private ArrayList<ArrayList<BufferedImage>> ghoulSprites;
  private ArrayList<ArrayList<BufferedImage>> ghoulOutlineSprites;
  private BufferedImage ghoulPalette;

  private ArrayList<BufferedImage> pellets;
  private ArrayList<BufferedImage> translucentPellets;
  private ArrayList<BufferedImage> powerUpBox;
  private ArrayList<BufferedImage> mapTiles;

  private BufferedImage background;
  private BufferedImage backgroundPalette;

  private BufferedImage mipMarker;
  private BufferedImage clientMarker;
  private BufferedImage inventory;
  private ArrayList<BufferedImage> powerUpIcons;
  private int inventoryColourID;
  private HashMap<utils.enums.PowerUp, ArrayList<BufferedImage>> powerUps;
  private ArrayList<BufferedImage> explosions;

  /**
   * @param baseDir path to the resources folder
   */
  public ResourceLoader(String baseDir) {
    BASE_DIR = baseDir;
    this.loadMap("default");
    this.init();
  }

  private void init() {
    this.loadPlayableMip();
    this.loadPlayableGhoul();
    this.loadMapTiles();
    this.loadBackground();
    this.loadClientMarker();
    this.loadMipMarker();
    this.loadPellet();
    this.loadInventory();
    this.loadPowerUpIcons();
    this.loadPowerUps();
    this.loadExplosion();
  }

  /**
   * @return returns the hashmap<name of themes, preview image for theme> found in the resources
   * folder
   */
  public HashMap<String, Image> getThemes() {
    File[] themeFolders = new File(BASE_DIR + "sprites/").listFiles(File::isDirectory);
    HashMap<String, Image> themes = new HashMap<>();
    for (File f : themeFolders) {
      String previewURI = new File(f.toString(), "preview.png").toURI().toString();
      Image preview = new Image(previewURI);
      themes.put(f.getName(), preview);
    }
    return themes;
  }

  public BufferedImage getPlayerPalette() {
    return mipPalette;
  }

  public String[] getValidMaps() {
    File[] maps = new File(BASE_DIR + "maps/").listFiles(File::isFile);
    String[] mapNames = getFileNames(maps);
    ArrayList<String> validMaps = new ArrayList<>();
    for (String map : mapNames) {
      if (map.endsWith(".png")) {
        validMaps.add(map.substring(0, map.length() - 4));
      }
    }
    Collections.sort(validMaps, Comparator.naturalOrder());
    return validMaps.toArray(new String[validMaps.size()]);
  }

  private String[] getFileNames(File[] maps) {
    String[] mapNames = new String[maps.length];
    for (int i = 0; i < maps.length; i++) {
      mapNames[i] = maps[i].getName();
    }
    return mapNames;
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
    //this.map = new Map(MapGenerator.generateNewMap());
  }

  public void loadMap(Map map) {
    this.map = map;
  }

  public Map getMap() {
    //return new Map(MapGenerator.generateNewMap());
    return map;
  }

  public void setMap(Map m) {
    this.map = m;
  }

  private void resizeSprites(ArrayList<BufferedImage> sprites, double ratio) {
    BufferedImage temp;

    for (int i = 0; i < sprites.size(); i++) {
      temp = sprites.get(i);
      int newWidth = (int) (temp.getWidth() * ratio);
      int newHeight = (int) (temp.getHeight() * ratio);
      BufferedImage resizedSprite =
          new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
      Graphics2D g = resizedSprite.createGraphics();
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.setRenderingHint(
          RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

      g.drawImage(temp, 0, 0, newWidth, newHeight, null);
      g.dispose();

      sprites.set(i, resizedSprite);
    }
  }

  private BufferedImage resizeSpriteSmooth(BufferedImage sprite, double ratio) {
    int newWidth = (int) (sprite.getWidth() * ratio);
    int newHeight = (int) (sprite.getHeight() * ratio);
    BufferedImage resizedSprite =
        new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = resizedSprite.createGraphics();

    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    g.drawImage(sprite, 0, 0, newWidth, newHeight, null);
    g.dispose();

    return resizedSprite;

  }

  private void resizeSpritesSmooth(ArrayList<BufferedImage> sprites, double ratio) {
    for (BufferedImage s : sprites) {
      resizeSpriteSmooth(s, ratio);
    }
    for (int i = 0; i < sprites.size(); i++) {
      sprites.set(i, resizeSpriteSmooth(sprites.get(i), ratio));
    }
  }

  private BufferedImage resizeSprite(BufferedImage sprite, double ratio) {
    int newWidth = (int) (sprite.getWidth() * ratio);
    int newHeight = (int) (sprite.getHeight() * ratio);
    BufferedImage resizedSprite =
        new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = resizedSprite.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(
        RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

    g.drawImage(sprite, 0, 0, newWidth, newHeight, null);
    g.dispose();

    return resizedSprite;
  }

  public void refreshSettings() {
    this.xResolution = Settings.getxResolution();
    this.yResolution = Settings.getyResolution();
    this.renderingMode = Settings.getRenderingMode();
    this.theme = Settings.getTheme();
    setResolution();
  }

  public void refreshSettings(int x, int y, RenderingMode r, String theme) {
    this.xResolution = x;
    this.yResolution = y;
    this.renderingMode = r;
    this.theme = theme;
    setResolution();
  }

  /**
   *
   */
  private void setResolution() {
    init();
    double mapToScreenRatio = 0.7;
    int x = this.xResolution;
    int y = this.yResolution;
    RenderingMode mode = this.renderingMode;

    // find dimensions of rendered map we want based on mapToScreenRatio
    int targetX = (int) (x * mapToScreenRatio);
    int targetY = (int) (y * mapToScreenRatio);

    int tileHeight = this.mapTiles.get(MapElement.FLOOR.toInt()).getHeight();
    int tileWidth = this.mapTiles.get(MapElement.FLOOR.toInt()).getWidth();

    // choose the smallest ratio to make sure map fits on screen
    double ratioX = (double) targetX / ((map.getMaxX() + map.getMaxY()) * (tileWidth / 2));
    double ratioY = (double) targetY / ((map.getMaxX() + map.getMaxY()) * (tileHeight / 2));
    double ratio = Math.min(ratioX, ratioY);

    double hudRatio = x / (double) 1366;

    boolean smoothEdges = false;

    switch (mode) {
      case NO_SCALING:
        return;
      case INTEGER_SCALING: {
//        System.out.println("ratio before:" + ratio);
        ratio = Math.floor(ratio);
//        System.out.println("ratio after:" + ratio);
        break;
      }
      case SMOOTH_SCALING: {
        smoothEdges = true;
        break;
      }
      case STANDARD_SCALING: {
        smoothEdges = false;
        break;
      }
      default: {
        System.out.println("invalid rendering mode");
        return;
      }
    }
    this.inventory = resizeSprite(inventory, hudRatio);
    resizeSprites(this.powerUpIcons, hudRatio);
    for (ArrayList<BufferedImage> mipSprite : mipSprites) {
      resizeSprites(mipSprite, ratio);
    }
    for (ArrayList<BufferedImage> ghoulSprite : ghoulSprites) {
      resizeSprites(ghoulSprite, ratio);
    }

    if (smoothEdges) {

      for (ArrayList<BufferedImage> mipOutline : mipOutlineSprites) {
        resizeSpritesSmooth(mipOutline, ratio);
      }

      for (ArrayList<BufferedImage> ghoulOutline : ghoulOutlineSprites) {
        resizeSpritesSmooth(ghoulOutline, ratio);
      }
      resizeSpritesSmooth(pellets, ratio);
      resizeSpritesSmooth(powerUpBox, ratio);
      resizeSpritesSmooth(translucentPellets, ratio);
      resizeSpritesSmooth(explosions, ratio);
      for (ArrayList<BufferedImage> powerUp : powerUps.values()) {
        resizeSpritesSmooth(powerUp, ratio);
      }
      resizeSpritesSmooth(mapTiles, ratio);
      mipMarker = resizeSpriteSmooth(mipMarker, ratio);
      clientMarker = resizeSpriteSmooth(clientMarker, ratio);
    } else {
      for (ArrayList<BufferedImage> mipOutline : mipOutlineSprites) {
        resizeSpritesSmooth(mipOutline, ratio);
      }

      for (ArrayList<BufferedImage> ghoulOutline : ghoulOutlineSprites) {
        resizeSpritesSmooth(ghoulOutline, ratio);
      }
      resizeSprites(pellets, ratio);
      resizeSprites(powerUpBox, ratio);
      resizeSprites(translucentPellets, ratio);
      resizeSprites(explosions, ratio);
      for (ArrayList<BufferedImage> powerUp : powerUps.values()) {
        resizeSprites(powerUp, ratio);
      }
      resizeSprites(mapTiles, ratio);
      mipMarker = resizeSprite(mipMarker, ratio);
      clientMarker = resizeSprite(clientMarker, ratio);
    }


  }

  /**
   *
   */
  public void loadPlayableMip() {
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "mip");
    BufferedImage sprites = extractColour(spriteSheet, getOutlineColour(spriteSheet), true);
    BufferedImage outlineSprites = extractColour(spriteSheet, getOutlineColour(spriteSheet), false);
    this.mipSprites = splitSpriteSheet(spriteWidth, spriteHeight, sprites);
    this.mipOutlineSprites = splitSpriteSheet(spriteWidth, spriteHeight, outlineSprites);
    this.mipPalette = loadImageFile("sprites/" + theme + "/playable/", "mip_palette");
  }

  /**
   * creates coloured sprites of mip according to the colour id selected
   *
   * @param _colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableMip(int _colourID) {
    ArrayList<ArrayList<BufferedImage>> recolouredSprites = new ArrayList<>();
    for (int i = 0; i < this.mipSprites.size(); i++) {
      ArrayList<BufferedImage> tmp = new ArrayList<>();
      for (int j = 0; j < this.mipSprites.get(i).size(); j++) {
        tmp.add(mergeImage(
            recolourSprite(
                this.mipSprites.get(i).get(j), this.mipPalette, 0, _colourID),
            this.mipOutlineSprites.get(i).get(j)));
      }
      recolouredSprites.add(tmp);
    }
    return bufferedToJavaFxImage2D(recolouredSprites);
  }

  /**
   *
   */
  public void loadPlayableGhoul() {
    BufferedImage spriteSheet = loadImageFile("sprites/" + theme + "/playable/", "ghoul");
    BufferedImage sprites = extractColour(spriteSheet, getOutlineColour(spriteSheet), true);
    BufferedImage outlineSprites = extractColour(spriteSheet, getOutlineColour(spriteSheet), false);
    this.ghoulSprites = splitSpriteSheet(spriteWidth, spriteHeight, sprites);
    this.ghoulOutlineSprites = splitSpriteSheet(spriteWidth, spriteHeight, outlineSprites);
    this.ghoulPalette = loadImageFile("sprites/" + theme + "/playable/", "ghoul_palette");
  }

  /**
   * creates coloured sprites of ghoul according to the colour id selected
   *
   * @param _colourID row of palette sheet to apply to sprite
   * @return 2d ArrayList of images - first dimension is the direction, second is each animation
   * frame
   */
  public ArrayList<ArrayList<Image>> getPlayableGhoul(int _colourID) {
    ArrayList<ArrayList<BufferedImage>> recolouredSprites = new ArrayList<>();
    for (int i = 0; i < this.ghoulSprites.size(); i++) {
      ArrayList<BufferedImage> tmp = new ArrayList<>();
      for (int j = 0; j < this.ghoulSprites.get(i).size(); j++) {
        tmp.add(mergeImage(
            recolourSprite(
                this.ghoulSprites.get(i).get(j), this.ghoulPalette, 0, _colourID),
            this.ghoulOutlineSprites.get(i).get(j)));
      }
      recolouredSprites.add(tmp);
    }
    return bufferedToJavaFxImage2D(recolouredSprites);
  }

  public void loadPellet() {
    // this.pellets = splitSpriteSheet(14,34,loadImageFile("sprites/" + theme +
    // "/consumable/","pellet")).get(0);
    this.pellets =
        new ArrayList<>(
            Arrays.asList(loadImageFile("sprites/" + theme + "/consumable/", "pellet")));
    this.translucentPellets =
        new ArrayList<>(Arrays.asList(transparentizeSprite(this.pellets.get(0))));
    this.powerUpBox = new ArrayList<>(
        Arrays.asList(loadImageFile("sprites/" + theme + "/consumable/", "powerBox")));
  }

  public ArrayList<Image> getPellet() {
    return bufferedToJavaFxImage(this.pellets);
  }

  public ArrayList<Image> getPowerBox() {
    return bufferedToJavaFxImage(this.powerUpBox);
  }

  public ArrayList<Image> getTranslucentPellet() {
    return bufferedToJavaFxImage(this.translucentPellets);
  }

  /**
   *
   */
  public void loadMapTiles() {
    ArrayList<BufferedImage> _mapTiles = new ArrayList<>();
    for (MapElement m : MapElement.values()) {
      _mapTiles.add(loadImageFile("sprites/" + theme + "/tiles/", m.toString()));
    }
    this.mapTiles = _mapTiles;
  }

  public ArrayList<Image> getMapTiles() {
    return bufferedToJavaFxImage(this.mapTiles);
  }

  public void loadBackground() {
    this.background = loadImageFile("sprites/" + theme + "/backgrounds/", theme);
    this.backgroundPalette =
        loadImageFile("sprites/" + theme + "/backgrounds/", theme + "_palette");
  }

  public Image getBackground() {
    return SwingFXUtils.toFXImage(this.background, null);
  }

  public BufferedImage getBackgroundPalette() {
    return this.backgroundPalette;
  }

  public void loadMipMarker() {
    this.mipMarker = loadImageFile("sprites/" + theme + "/misc/", "mip_marker");
  }

  public Image getMipMarker() {
    return SwingFXUtils.toFXImage(this.mipMarker, null);
  }

  public void loadClientMarker() {
    this.clientMarker = loadImageFile("sprites/" + theme + "/misc/", "client_marker");
  }

  public Image getMClientMarker() {
    return SwingFXUtils.toFXImage(this.clientMarker, null);
  }

  public void loadInventory() {
    this.inventory = loadImageFile("sprites/" + theme + "/HUD/", "inventory");
    this.inventoryColourID = 0;
  }

  public Image getInventory(int colourID) {
    Image recolouredInventory = (SwingFXUtils
        .toFXImage(
            recolourSprite(this.inventory, this.mipPalette, this.inventoryColourID, colourID),
            null));
    this.inventoryColourID = colourID;
    return recolouredInventory;
  }

  public void loadPowerUpIcons() {
    ArrayList<BufferedImage> powerUps = new ArrayList<>();
    for (utils.enums.PowerUp powerUp : utils.enums.PowerUp.values()) {
      powerUps.add(loadImageFile("sprites/" + theme + "/misc/icon/", powerUp.toString()));
    }
    this.powerUpIcons = powerUps;
  }

  public ArrayList<Image> getPowerUpIcons() {
    return bufferedToJavaFxImage(this.powerUpIcons);
  }

  public void loadPowerUps() {
    HashMap<PowerUp, ArrayList<BufferedImage>> powerUps = new HashMap<>();

    //add web powerup
    powerUps.put(PowerUp.WEB,
        splitSpriteSheet(39, 37, loadImageFile("sprites/" + theme + "/powerups/", "web")).get(0));

    //add speedup powerup
    BufferedImage speedAnimation = transparentizeSprite(
        loadImageFile("sprites/" + theme + "/powerups/", "speed"));
    powerUps.put(PowerUp.SPEED, splitSpriteSheet(39, 36, speedAnimation).get(0));

    //add invincible powerup
    BufferedImage invincibleAnimation = transparentizeSprite(
        loadImageFile("sprites/" + theme + "/powerups/", "invincible"));
    powerUps.put(PowerUp.INVINCIBLE, splitSpriteSheet(39, 36, invincibleAnimation).get(0));

    this.powerUps = powerUps;
  }

  public HashMap<utils.enums.PowerUp, ArrayList<Image>> getPowerUps() {
    HashMap<utils.enums.PowerUp, ArrayList<Image>> convertedSprites = new HashMap<>();

    for (utils.enums.PowerUp key : this.powerUps.keySet()) {
      convertedSprites.put(key, bufferedToJavaFxImage(this.powerUps.get(key)));
    }

    return convertedSprites;
  }

  public void loadExplosion() {
    this.explosions = splitSpriteSheet(39, 36,
        loadImageFile("sprites/" + theme + "/fx/", "explosion")).get(0);
  }

  public ArrayList<Image> getExplosion() {
    return bufferedToJavaFxImage(this.explosions);
  }

  /**
   * returns loads a png image in TYPE_4BYTE_ABGR
   *
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
  private ArrayList<Image> bufferedToJavaFxImage(ArrayList<BufferedImage> sprites) {
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
    BufferedImage recolouredSprite = new BufferedImage(sprite.getWidth(), sprite.getHeight(),
        BufferedImage.TYPE_4BYTE_ABGR);
    for (int i = 0; i < palette.getWidth(); i++) {
      // iterate through every pixel of sprite
      for (int x = 0; x < sprite.getWidth(); x++) {
        for (int y = 0; y < sprite.getHeight(); y++) {
          if (sprite.getRGB(x, y) == palette.getRGB(i, oldPaletteRow)) {
            recolouredSprite.setRGB(x, y, palette.getRGB(i, newPaletteRow));
          }
        }
      }
    }
    return mergeImage(sprite, recolouredSprite);
  }

  private BufferedImage transparentizeSprite(BufferedImage sprite) {
    BufferedImage translucentImage =
        new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = translucentImage.createGraphics();
    final float opacity = 0.5f;
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, opacity));
    g.drawImage(sprite, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    g.dispose();

    return translucentImage;
  }

  private BufferedImage extractColour(BufferedImage img, int colour, boolean subtract) {
    int currentColour;
    BufferedImage extractedImg = new BufferedImage(img.getWidth(), img.getHeight(),
        BufferedImage.TYPE_4BYTE_ABGR);

    for (int x = 0; x < img.getWidth(); x++) {
      for (int y = 0; y < img.getHeight(); y++) {
        currentColour = img.getRGB(x, y);
        if (currentColour == colour && subtract) {
          continue;
        }
        if (currentColour != colour && !subtract) {
          continue;
        }
        extractedImg.setRGB(x, y, currentColour);
      }
    }

    return extractedImg;
  }

  private BufferedImage mergeImage(BufferedImage img1, BufferedImage img2) {
    BufferedImage mergedImage =
        new BufferedImage(Math.max(img1.getWidth(), img2.getWidth()),
            Math.max(img1.getHeight(), img2.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g = mergedImage.createGraphics();
    g.drawImage(img1, 0, 0, null);
    g.drawImage(img2, 0, 0, null);
    g.dispose();
    return mergedImage;
  }

  private int getOutlineColour(BufferedImage sprite) {
    return -16777216;
  }
}
