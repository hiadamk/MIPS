package utils;

public abstract class GameLoop extends Thread {

  long gameSpeed;

  public GameLoop(long gameSpeed) {
    this.gameSpeed = gameSpeed;
  }

  @Override
  public void run() {
    long currentSleepTime = 0;
    long currentTime = System.nanoTime();
    long newTime = System.nanoTime();
    while (true) {

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

  public abstract void handle();
}
