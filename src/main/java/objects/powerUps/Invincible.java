package objects.powerUps;

import java.util.HashMap;
import java.util.UUID;
import objects.Entity;
import objects.Pellet;

public class Invincible extends PowerUp {

  public Invincible() {
    super(200, "invincible");
    this.type = utils.enums.PowerUp.INVINCIBLE;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      HashMap<String, Pellet> pellets,
      Entity[] agents) {
    this.user = user;
    activePowerUps.put(id, this);
    this.effected = user;
    user.setInvincible(true);
    counter = 0;
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      effected.setInvincible(false);
      return true;
    }
    return false;
  }
}
