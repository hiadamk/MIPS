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
