package com.lordsofmidnight.gamestate.maps;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapGenerationHandler {

  private BlockingQueue<Map> bigMaps = new LinkedBlockingQueue();
  private BlockingQueue<Map> smallMaps = new LinkedBlockingQueue();
  private Thread smallMapsThread;
  private Thread bigMapsThread;

  public MapGenerationHandler() {

//    smallMapGenerator.start();
//    bigMapGenerator.start();
  }

  Runnable smallMapGenerator =
      (() -> {
        while (!Thread.currentThread().isInterrupted()) {
          if (smallMaps.size() < 20) {
            smallMaps.add(new Map(MapGenerator.newRandomMap(-1, -1)));
          } else {

            try {
              Thread.sleep(8000);
            } catch (InterruptedException e) {
              System.out.println("Small Map Generator shut down...");
            }
          }
        }
      });

  Runnable bigMapGenerator = (() -> {
    while (!Thread.currentThread().isInterrupted()) {
      if (bigMaps.size() < 20) {
        bigMaps.add(new Map(MapGenerator.newRandomMap(2, 2)));
      } else {

        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          System.out.println("Big Map Generator shut down...");
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

  public void stop() {
    smallMapsThread.interrupt();
    bigMapsThread.interrupt();
  }

  public void start() {
    smallMapsThread = new Thread(smallMapGenerator);
    bigMapsThread = new Thread(bigMapGenerator);
    smallMapsThread.start();
    bigMapsThread.start();

  }


}
