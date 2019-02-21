package utils;

public abstract class GameLoop extends Thread {

  long gameSpeed;

  public GameLoop(long gameSpeed) {
    this.gameSpeed = gameSpeed;
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();
    int iter = 0;
    long currentSleepTime = 0;
    long currentTime = System.nanoTime();
    long newTime = System.nanoTime();
    while (true) {
      if (iter % 100 == 0) {
        System.out.println(iter);
      }
      iter++;

//      if (System.currentTimeMillis() - time > gameSpeed) {
//        time = System.currentTimeMillis();
//        this.handle();
//      }
//      try {
//        Thread.sleep(2);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }

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
