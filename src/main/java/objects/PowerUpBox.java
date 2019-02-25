package objects;

import java.util.Random;
import utils.Point;
import utils.enums.PowerUp;

public class PowerUpBox extends Pellet {

  private PowerUp[] powerUps = {PowerUp.BLUESHELL, PowerUp.SPEED, PowerUp.WEB};

  public PowerUpBox(double x, double y) {
    super(x, y);
  }

  public PowerUpBox(Point p) {
    super(p);
  }


  public PowerUp getPowerUp() {
    Random r = new Random();
    int i = r.nextInt(powerUps.length - 1);
    this.setActive(false);
    return powerUps[i];

  }
}
