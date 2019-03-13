package objects.powerUps;

import java.util.HashMap;
import java.util.UUID;
import objects.Entity;
import objects.Pellet;
import utils.Methods;

public class Blueshell extends PowerUp {

  private HashMap<UUID, PowerUp> activePowerUps;

  public Blueshell() {
    super(50, "blueshell");
    this.type = utils.enums.PowerUp.BLUESHELL;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      HashMap<String, Pellet> pellets,
      Entity[] agents) {
    effected = agents[Methods.findWinner(agents)];
    this.user = user;
    this.activePowerUps = activePowerUps;
    this.effected = agents[Methods.findWinner(agents)];
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      PowerUp killer = new Mine();
      killer.trigger(effected, activePowerUps);
      killer.user = user;
      user.increaseKills();
      return true;
    }
    return false;
  }

}
