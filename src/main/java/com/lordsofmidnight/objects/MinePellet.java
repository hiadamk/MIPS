package com.lordsofmidnight.objects;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.Methods;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A pellet object to represent a mine
 */
public class MinePellet extends Pellet {

  private boolean hidden;
  private boolean detonated = false;
  private int hidden_timer = 300;
  private Entity placer;

  /**
   * Constructor
   *
   * @param x The x coordinate of the mine
   * @param y The y coordinate of the mine
   * @param placer The entity who placed the mine
   */
  public MinePellet(double x, double y, Entity placer) {
    super(x, y);
    hidden = false;
    this.respawntime = -1;
    this.placer = placer;
  }

  @Override
  public boolean replace() {
    return detonated;
  }

  /**
   *
   * @return If the mine is hidden from players
   */
  public boolean isHidden() {
    return hidden;
  }

  @Override
  public boolean canUse(Entity e) {
    return !detonated;
  }

  @Override
  public void interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps, AudioController audioController) {
    if(!detonated){
      Methods.kill(placer, entity, audioController);
      detonated = true;
    }


  }

  @Override
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
