package com.lordsofmidnight.objects;

import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.Methods;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinePellet extends Pellet {

  private boolean hidden;
  private boolean detonated = false;
  private int hidden_timer = 30;
  private Entity placer;

  public MinePellet(double x, double y, Entity placer) {
    super(x, y);
    hidden = false;
    this.respawntime = -1;
    this.placer = placer;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override
  public boolean canUse(Entity e) {
    return !detonated;
  }

  @Override
  public void interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps) {
    Methods.kill(placer, entity);
    detonated = true;
  }

  public void incrementRespawn() {
    if (detonated) {
      return;
    }
    respawnCount++;
    if (respawnCount == hidden_timer) {
      this.hidden = true;
    }
  }
}
