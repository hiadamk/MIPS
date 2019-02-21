package utils;

public abstract class GameLoop extends Thread {

  long gameSpeed;

  public GameLoop(long gameSpeed) {
    this.gameSpeed = gameSpeed;
  }

  @Override
  public void run() {
    long time = System.currentTimeMillis();
    while (true) {
      // System.out.println(1);
      if (System.currentTimeMillis() - time > gameSpeed) {
        time = System.currentTimeMillis();
        this.handle();
      }
      try {
        Thread.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public abstract void handle();
}
