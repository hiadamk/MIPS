package com.lordsofmidnight.utils;

/**
 * A game loop to run out game on
 * @author Tim
 */
public abstract class GameLoop extends Thread {

  long gameSpeed;
  private boolean running;
  private boolean pause;

  /**
   * @param gameSpeed the length of time each loop should last e.g. gamespeed = 10^9 would mean the
   * loop occurs 10 times per second
   */
  public GameLoop(long gameSpeed) {
    this.gameSpeed = gameSpeed;
    running = true;
  }

  @Override
  public void run() {
    long currentTime = System.nanoTime();
    while (running) {
      if (pause) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        currentTime = System.nanoTime();
        continue;
      }
      this.handle();
      while(System.nanoTime() - currentTime  < gameSpeed){
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      //correct over-run of thread sleep
      currentTime = System.nanoTime()-(System.nanoTime() - currentTime - gameSpeed);
    }
  }

  /**
   * pauses game loop - doesn't execute handle
   */
  public void pause() {
    pause = true;
  }

  /**
   * unpauses game loop
   */
  public void unpause() {
    pause = false;
  }

  /**
   * ends game loop
   */
  public void close() {
    running = false;
  }

  /**
   * method that is called each game loop. OVERRIDE THIS
   */
  public abstract void handle();
}
