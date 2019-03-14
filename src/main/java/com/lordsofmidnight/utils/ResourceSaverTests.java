package com.lordsofmidnight.utils;

import java.io.File;
import java.io.IOException;
import com.lordsofmidnight.gamestate.maps.Map;
//import org.junit.jupiter.api.Test;

public class ResourceSaverTests {

  //@Test
  void saveTest() {
    int[][] map = {{1, 1, 1},
        {1, 0, 1},
        {1, 1, 1}};

    ResourceSaver rs = new ResourceSaver("src/test/resources/");
    try {
      rs.saveMap(new Map(map), "test");
    } catch (IOException e) {
      e.printStackTrace();
    }
    File saveLocation = new File("src/test/resources/maps/test.png");
    assert (saveLocation.exists());
    saveLocation.delete();
  }
}
