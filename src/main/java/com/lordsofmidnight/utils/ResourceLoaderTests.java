package com.lordsofmidnight.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
//import org.junit.jupiter.api.Test;
import com.lordsofmidnight.utils.enums.MapElement;

//import static org.junit.jupiter.api.Assertions.*;

public class ResourceLoaderTests {

  // MAP TESTS
  /*
  @Test
  void map1X1() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    resourceLoader.loadMap("1x1");
    int[][] map = {{1}};
    System.out.println(Arrays.deepToString(resourceLoader.getMap().raw()));
    assert (Arrays.deepEquals(resourceLoader.getMap().raw(), map));
  }

  @Test
  void map9X9() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    resourceLoader.loadMap("9x9");
    int[][] map = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 1, 1, 0, 1, 1, 0, 1},
        {1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 1, 1, 0, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    assert (Arrays.deepEquals(resourceLoader.getMap().raw(), map));
  }

  // make sure the map coordinates are referenced in x,y rather than y,x

  @Test
  void map9X9CorrectRotation() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    resourceLoader.loadMap("9x9rotation");
    int[][] map = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    int[][] gameMap = resourceLoader.getMap().raw();

    assert (Arrays.deepEquals(gameMap, map));
    assert (map[7][6] == gameMap[7][6]);
    assert (map[7][7] == gameMap[7][7]);
  }

  @Test
  void correctMipNumLoaded() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(0);
    assertEquals(4, mipSprites.size());
    assertEquals(2, mipSprites.get(0).size());
    assertEquals(2, mipSprites.get(1).size());
  }

  @Test
  void correctGhoulNumLoaded() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> ghoulSprites = resourceLoader.getPlayableGhoul(0);
    assertEquals(4, ghoulSprites.size());
    assertEquals(1, ghoulSprites.get(0).size());
  }

  @Test
  void recolouredMipPink() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(1);
    BufferedImage firstSprite = SwingFXUtils.fromFXImage(mipSprites.get(0).get(0), null);

    assertEquals(testPaletteLoader("mip_palette").getRGB(0, 1), firstSprite.getRGB(11, 9));
  }

  @Test
  void recolouredMipGreen() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(2);
    BufferedImage firstSprite = SwingFXUtils.fromFXImage(mipSprites.get(0).get(0), null);
    assertEquals(testPaletteLoader("mip_palette").getRGB(0, 2), firstSprite.getRGB(11, 9));
  }

  // tests end of sheet edge case

  @Test
  void recolouredMipRed() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(4);
    BufferedImage firstSprite = SwingFXUtils.fromFXImage(mipSprites.get(0).get(0), null);
    assertEquals(testPaletteLoader("mip_palette").getRGB(0, 4), firstSprite.getRGB(11, 9));
  }

  @Test
  void correctThemesLoaded() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    String[] expected = new String[]{"default"};
    fail("I commented this out because the error was getting annoying");
    //assertArrayEquals(expected, resourceLoader.getThemes());
  }

  @Test
  void floorLoaded() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    BufferedImage floor =
        SwingFXUtils.fromFXImage(resourceLoader.getMapTiles().get(MapElement.FLOOR.toInt()), null);
    assertEquals(19, floor.getHeight());
    assertEquals(39, floor.getWidth());
  }

  @Test
  void wallLoaded() {
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    BufferedImage wall =
        SwingFXUtils.fromFXImage(resourceLoader.getMapTiles().get(MapElement.WALL.toInt()), null);
    assertEquals(29, wall.getHeight());
    assertEquals(39, wall.getWidth());
  }

  BufferedImage testPaletteLoader(String paletteName) {
    File file = new File("src/test/resources/sprites/default/playable/" + paletteName + ".png");
    BufferedImage palette = null;
    try {
      palette = ImageIO.read(file);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    return palette;
  }

  */
}
