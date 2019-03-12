package objects.powerUps;

import java.util.HashMap;
import java.util.UUID;
import objects.Entity;
import objects.Pellet;
import objects.PowerUpBox;
import utils.Point;

public class Web extends PowerUp {

  public Web() {
    super(100, "web");
    this.type = utils.enums.PowerUp.WEB;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      HashMap<String, Pellet> pellets,
      Entity[] agents) {
    this.user = user;
    this.onMap = true;
    Point loc = user.getMoveInDirection(1.1, user.getFacing().getInverse());
    int x = (int) loc.getX();
    int y = (int) loc.getY();
    PowerUpBox box = new PowerUpBox(x + 0.5, y + 0.5);
    box.setTrap(this);
    pellets.put(x + "," + y, box);
  }

  @Override
  public void trigger(Entity victim, HashMap<UUID, PowerUp> activePowerUps) {
    victim.setStunned(true);
    activePowerUps.put(id, this);
    this.effected = victim;
    counter = 0;
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      effected.setStunned(false);
      return true;
    }
    return false;
  }
}
