package utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ResourceLoader {

  private final String BASE_DIR;
  private Map map;

  public ResourceLoader(String baseDir) {
    BASE_DIR = baseDir;
    map = this.loadMap("default");
  }

  public static void main(String[] args) {
    ResourceLoader rl = new ResourceLoader("src/main/resources/");
    rl.loadMap("default");
    System.out.println(
        Arrays.deepToString(rl.getMap().raw()).replace("], ", "]\n").replace("[[", "[")
            .replace("]]", "]"));
  }

  /**
   * @param name name of map: if file is default.png the name is default
   */
  private Map loadMap(String name) {
    String path = BASE_DIR + "maps/" + name + ".png";
    File mapFile = new File(path);
    BufferedImage mapImage = null;

    try {
      mapImage = ImageIO.read(mapFile);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    int width = mapImage.getWidth();
    int height = mapImage.getHeight();
    int[][] map_ = new int[width][height];

    //convert image into game map
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map_[x][y] = MapColour.toTile(mapImage.getRGB(x, y)); //change rgb int into a map int
      }
    }

    return new Map(map_);
  }

  public Map getMap() {
    return map;
  }
}
