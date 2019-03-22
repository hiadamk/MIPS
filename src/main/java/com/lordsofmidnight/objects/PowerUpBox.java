package com.lordsofmidnight.objects;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import com.lordsofmidnight.objects.powerUps.Blueshell;
import com.lordsofmidnight.objects.powerUps.Invincible;
import com.lordsofmidnight.objects.powerUps.Mine;
import com.lordsofmidnight.objects.powerUps.Speed;
import com.lordsofmidnight.objects.powerUps.Web;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.PowerUp;
import java.util.concurrent.ConcurrentHashMap;

public class PowerUpBox extends Pellet {

  private static PowerUp[] powerUps = {
      PowerUp.BLUESHELL, PowerUp.SPEED, PowerUp.WEB, PowerUp.INVINCIBLE
  };

  private final HashMap<Integer, PowerUp>[] ghostWeights = new HashMap[5];
  private final HashMap<Integer, PowerUp>[] mipsmanWeights = new HashMap[5];

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
    // Init ghost item weights
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
    // Init MIPsman weights
    map = new HashMap<>();
    map.put(5, PowerUp.INVINCIBLE);
    map.put(30, PowerUp.WEB);
    map.put(20, PowerUp.SPEED);
    map.put(20, PowerUp.MINE);
    mipsmanWeights[0] = map;
    map = new HashMap<>();
    map.put(5, PowerUp.BLUESHELL);
    map.put(10, PowerUp.INVINCIBLE); //15
    map.put(40, PowerUp.WEB);
    map.put(30, PowerUp.MINE);
    map.put(30, PowerUp.SPEED);
    mipsmanWeights[1] = map;
    map = new HashMap<>();
    map.put(10, PowerUp.BLUESHELL);
    map.put(10, PowerUp.INVINCIBLE); //10
    map.put(30, PowerUp.WEB);
    map.put(40, PowerUp.SPEED);
    map.put(35, PowerUp.MINE);
    mipsmanWeights[2] = map;
    map = new HashMap<>();
    map.put(15, PowerUp.BLUESHELL);
    map.put(16, PowerUp.INVINCIBLE); //25
    map.put(30, PowerUp.WEB);
    map.put(40, PowerUp.SPEED);
    map.put(35, PowerUp.MINE);
    mipsmanWeights[3] = map;
    map = new HashMap<>();
    map.put(20, PowerUp.BLUESHELL);
    map.put(21, PowerUp.INVINCIBLE); //40
    map.put(30, PowerUp.WEB);
    map.put(31, PowerUp.MINE);
    map.put(36, PowerUp.SPEED);
    mipsmanWeights[4] = map;
  }

  /**
   * Gets a random PowerUp
   *
   * @return the PowerUp
   * @author Matthew Jones
   */
  public com.lordsofmidnight.objects.powerUps.PowerUp getPowerUp(Entity entity, Entity[] agents) {
    int rank = getRank(entity, agents);
    HashMap<Integer, PowerUp> baseWeights = mipsmanWeights[rank];
    //   entity.isMipsman() ? mipsmanWeights[rank] : ghostWeights[rank];
    int totalWeights = 0;
    TreeMap<Integer, PowerUp> weights = new TreeMap<>();
    for (Entry<Integer, PowerUp> entry : baseWeights.entrySet()) {
      weights.put(totalWeights, entry.getValue());
      totalWeights += entry.getKey();
    }
    Random r = new Random();
    int i = (int) ((1 - r.nextDouble()) * totalWeights);
    this.setActive(false);
    switch (weights.floorEntry(i).getValue()) {
      case INVINCIBLE:
        return new Invincible();
      case SPEED:
        return new Speed();
      case WEB:
        return new Web();
      case BLUESHELL:
        return new Blueshell();
      case MINE:
        return new Mine();
      default:
        return null;
    }

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
  public void interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, com.lordsofmidnight.objects.powerUps.PowerUp> activePowerUps) {
    if (isTrap) {
      trap.trigger(entity, activePowerUps);
      isTrap = false;
      setActive(false);
      return;
    }
    if (!active) {
      return;
    }
    com.lordsofmidnight.objects.powerUps.PowerUp newPowerUp = getPowerUp(entity, agents);
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
