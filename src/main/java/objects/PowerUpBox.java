package objects;

import java.util.Random;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.PowerUp;

public class PowerUpBox extends Pellet {

  private static PowerUp[] powerUps = {PowerUp.BLUESHELL, PowerUp.SPEED, PowerUp.WEB};

  public PowerUpBox(double x, double y) {
    super(x, y);
    init();
  }

  public PowerUpBox(Point p) {
    super(p);
    init();
  }

  private void init() {
    this.respawntime = 500;
    this.value = 0;
  }

  /**
   * Gets a random PowerUp
   *
   * @return the PowerUp
   */
  public PowerUp getPowerUp() {
    Random r = new Random();
    int i = r.nextInt(powerUps.length - 1);
    this.setActive(false);
    return powerUps[i];
  }

  @Override
  public void updateImages(ResourceLoader r) {
    currentImage = r.getPowerBox();
  }

  @Override
  public void interact(Entity entity) {
    if (!active) {
      return;
    }
    PowerUp newPowerUp = getPowerUp();
    entity.giveItem(newPowerUp);
    this.setActive(false);
  }
}
