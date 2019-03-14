package com.lordsofmidnight.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.maps.MapGenerator;
import com.lordsofmidnight.utils.enums.MapElement;

public class ResourceSaver {

  private final String BASE_DIR;

  public ResourceSaver(String baseDir) {
    this.BASE_DIR = baseDir;
  }

  /**
   * @param map Map to save
   * @param mapName Name of map without file extension
   * @throws IllegalArgumentException the map given is invalid
   * @throws IOException the map with that name already exists
   */
  public void saveMap(Map map, String mapName) throws IllegalArgumentException, IOException {
    if (!MapGenerator.validateMap(map.raw())) {
      throw new IllegalArgumentException("Invalid Map!");
    }
    File saveLocation = new File(BASE_DIR + "maps/" + mapName + ".png");
    if (saveLocation.exists()) {
      throw new IOException("file already exists!");
    }

    int[][] rawMap = map.raw();
    BufferedImage mapImg = new BufferedImage(rawMap.length, rawMap[0].length,
        BufferedImage.TYPE_4BYTE_ABGR);

    //convert each array com.lordsofmidnight.gamestate to a corresponding com.lordsofmidnight.gamestate on the image
    for (int x = 0; x < rawMap.length; x++) {
      for (int y = 0; y < rawMap[x].length; y++) {
        mapImg.setRGB(x, y, MapElement.idToColour(rawMap[x][y]));
      }
    }
    //save image to maps folder
    ImageIO.write(mapImg, "png", saveLocation);
  }
}
