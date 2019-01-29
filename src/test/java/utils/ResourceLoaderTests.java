package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

public class ResourceLoaderTests {

  //MAP TESTS

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

  /**
   * make sure the map coordinates are referenced in x,y rather than y,x
   */
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
  void correctMipNumLoaded(){
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> mipSprites = resourceLoader.getPlayableMip(0);
    assertEquals(4,mipSprites.size());
    assertEquals(2,mipSprites.get(0).size());
    assertEquals(2,mipSprites.get(1).size());
  }

  @Test
  void correctGhoulNumLoaded(){
    ResourceLoader resourceLoader = new ResourceLoader("src/test/resources/");
    ArrayList<ArrayList<Image>> ghoulSprites = resourceLoader.getPlayableGhoul(0);
    assertEquals(4,ghoulSprites.size());
    assertEquals(1,ghoulSprites.get(0).size());
  }
}
