package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import utils.Point;
import utils.ResourceLoader;
import utils.enums.PowerUp;

public class PowerUpBox extends Pellet {

	private static PowerUp[] powerUps = {PowerUp.BLUESHELL, PowerUp.SPEED, PowerUp.WEB,
			PowerUp.INVINCIBLE};

  private final HashMap<Integer, PowerUp>[] ghostWeights = new HashMap[5];
  private final HashMap<Integer, PowerUp>[] pacmanWeights = new HashMap[5];
  public PowerUpBox(double x, double y) {
    super(x, y);
    init();
  }

  public PowerUpBox(Point p) {
    super(p);
    init();
  }

  private void init() {
    this.respawntime = 300;
    this.value = 0;
    //Init ghost item weights
    HashMap<Integer, PowerUp> map = new HashMap<>();
    map.put(50, PowerUp.WEB);
    map.put(51, PowerUp.SPEED);
    ghostWeights[0] = map;
    map = new HashMap<>();
    map.put(60, PowerUp.SPEED);
    map.put(40, PowerUp.WEB);
    map.put(10, PowerUp.BLUESHELL);
    ghostWeights[1] = map;
    map = new HashMap<>();
    map.put(75, PowerUp.SPEED);
    map.put(25, PowerUp.WEB);
    map.put(20, PowerUp.BLUESHELL);
    ghostWeights[2] = map;
    map = new HashMap<>();
    map.put(75, PowerUp.SPEED);
    map.put(26, PowerUp.WEB);
    map.put(25, PowerUp.BLUESHELL);
    ghostWeights[3] = map;
    map = new HashMap<>();
    map.put(75, PowerUp.SPEED);
    map.put(25, PowerUp.WEB);
    map.put(40, PowerUp.BLUESHELL);
    ghostWeights[4] = map;
    //Init PacMan weights
    map = new HashMap<>();
    map.put(10, PowerUp.INVINCIBLE);
    map.put(50, PowerUp.WEB);
    map.put(20, PowerUp.SPEED);
    pacmanWeights[0] = map;
    map = new HashMap<>();
    map.put(5, PowerUp.BLUESHELL);
    map.put(15, PowerUp.INVINCIBLE);
    map.put(40, PowerUp.WEB);
    map.put(30, PowerUp.SPEED);
    pacmanWeights[1] = map;
    map = new HashMap<>();
    map.put(10, PowerUp.BLUESHELL);
    map.put(20, PowerUp.INVINCIBLE);
    map.put(30, PowerUp.WEB);
    map.put(40, PowerUp.SPEED);
    pacmanWeights[2] = map;
    map = new HashMap<>();
    map.put(15, PowerUp.BLUESHELL);
    map.put(25, PowerUp.INVINCIBLE);
    map.put(30, PowerUp.WEB);
    map.put(40, PowerUp.SPEED);
    pacmanWeights[3] = map;
    map = new HashMap<>();
    map.put(20, PowerUp.BLUESHELL);
    map.put(40, PowerUp.INVINCIBLE);
    map.put(21, PowerUp.WEB);
    map.put(41, PowerUp.SPEED);
    pacmanWeights[4] = map;
  }

  /**
   * Gets a random PowerUp
   * @author Matthew Jones
   * @return the PowerUp
   */
  public PowerUp getPowerUp(Entity entity, Entity[] agents) {
    int rank = getRank(entity, agents);
    HashMap<Integer, PowerUp> baseWeights =
        entity.isMipsman() ? pacmanWeights[rank] : ghostWeights[rank];
    int totalWeights = 0;
    TreeMap<Integer, PowerUp> weights = new TreeMap<>();
    for (Entry<Integer, PowerUp> entry : baseWeights.entrySet()) {
      weights.put(totalWeights, entry.getValue());
      totalWeights += entry.getKey();
    }
    Random r = new Random();
    int i = (int) ((1 - r.nextDouble()) * totalWeights);
    this.setActive(false);
    return weights.floorEntry(i).getValue();
  }

  @Override
  public boolean canUse(Entity e) {
    return true;
  }

  @Override
  public void updateImages(ResourceLoader r) {
    currentImage = r.getPowerBox();
  }

  @Override
  public void interact(Entity entity, Entity[] agents, ArrayList<PowerUp> activePowerUps) {
    if (isTrap) {
      trap.trigger(entity, activePowerUps);
      isTrap = false;
      setActive(false);
      return;
    }
    if (!active) {
      return;
    }
    PowerUp newPowerUp = getPowerUp(entity, agents);
    entity.giveItem(newPowerUp);
    this.setActive(false);
  }

  @Override
  public boolean isPowerPellet() {
    return true;
  }
  private int getRank(Entity entity, Entity[] agents) {
    int score = entity.getScore();
    int rank = 0;
    for (Entity e : agents) {
      if (e != entity && e.getScore() > score) {
        rank++;
      }
    }
    return rank;
  }
}
