package com.lordsofmidnight.gamestate.maps;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class which stores pre-generated maps to attempt to speed up the process
 */
public class MapGenerationHandler {

  private BlockingQueue<Map> bigMaps = new LinkedBlockingQueue();
  private BlockingQueue<Map> smallMaps = new LinkedBlockingQueue();
  private Thread smallMapsThread;
  private Thread bigMapsThread;

  public MapGenerationHandler() {
  }

  /**
   * Runnable which maintains a buffer of 20 small generated maps
   */
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

  /**
   * Runnable which maintains a buffer of 20 big generated maps
   */
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

  /**
   * Removes a map from the queue of large maps generated
   *
   * @return The large map
   */
  public Map getBigMap() {
    try {
      return bigMaps.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Map(MapGenerator.generateNewMap(-1, -1));
  }

  /**
   * Removes a small map from the queue of maps
   * @return The small map
   */
  public Map getSmallMap() {
    try {
      return smallMaps.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new Map(MapGenerator.generateNewMap(-1, -1));
  }

  /**
   * Stops map generation
   */
  public void stop() {
    smallMapsThread.interrupt();
    bigMapsThread.interrupt();
  }

  /**
   * Starts map generation.
   */
  public void start() {
    smallMapsThread = new Thread(smallMapGenerator);
    bigMapsThread = new Thread(bigMapGenerator);
    smallMapsThread.start();
    bigMapsThread.start();

  }


}
