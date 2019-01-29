package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
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
}
