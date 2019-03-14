package com.lordsofmidnight.utils;

public abstract class GameLoop extends Thread {

  long gameSpeed;
  private boolean running;
  private boolean pause;

  public GameLoop(long gameSpeed) {
    this.gameSpeed = gameSpeed;
    running = true;
  }

  @Override
  public void run() {
    long currentSleepTime = 0;
    long currentTime = System.nanoTime();
    long newTime = System.nanoTime();
    while (running) {
      if (pause) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        continue;
      }
      this.handle();
      while (currentSleepTime < gameSpeed) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        newTime = System.nanoTime();
        currentSleepTime += newTime - currentTime;
        currentTime = newTime;

      }
      currentSleepTime = 0;

    }
  }

  public void pause() {
    pause = true;
  }

  public void unpause() {
    pause = false;
  }

  public void close() {
    running = false;
  }

  public abstract void handle();
}
