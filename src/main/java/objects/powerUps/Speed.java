package objects.powerUps;

import java.util.HashMap;
import java.util.UUID;
import objects.Entity;
import objects.Pellet;


public class Speed extends PowerUp {

  public Speed() {
    super(300, "speed");
    this.type = utils.enums.PowerUp.SPEED;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      HashMap<String, Pellet> pellets,
      Entity[] agents) {
    this.user = user;
    user.changeBonusSpeed(0.03);
    activePowerUps.put(id, this);
    this.effected = user;
    counter = 0;
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      user.changeBonusSpeed(-0.03);
      return true;
    }
    return false;
  }
}
