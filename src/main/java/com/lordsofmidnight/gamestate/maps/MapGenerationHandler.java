package com.lordsofmidnight.gamestate.maps;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapGenerationHandler {

  private BlockingQueue<Map> bigMaps = new LinkedBlockingQueue();
  private BlockingQueue<Map> smallMaps = new LinkedBlockingQueue();

  public MapGenerationHandler() {
    smallMapGenerator.start();
    bigMapGenerator.start();
  }

  Thread smallMapGenerator = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
      if (smallMaps.size() < 20) {
        smallMaps.add(new Map(MapGenerator.newRandomMap(-1, -1)));
      } else {

        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  });

  Thread bigMapGenerator = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
      if (bigMaps.size() < 20) {
        bigMaps.add(new Map(MapGenerator.newRandomMap(2, 2)));
      } else {

        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  });

  public Map getBigMap() {
    try {
      return bigMaps.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Map(MapGenerator.generateNewMap(-1, -1));
  }

  public Map getSmallMap() {
    try {
      return smallMaps.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Map(MapGenerator.generateNewMap(-1, -1));
  }


}
