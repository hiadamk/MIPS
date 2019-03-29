package com.lordsofmidnight.objects;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.renderer.ResourceLoader;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A blank powerup box used in multiplayer for clients
 */
public class EmptyPowerUpBox extends Pellet {

  /**
   * @param p The location of the box
   */
  public EmptyPowerUpBox(Point p) {
    super(p);
    init();
  }

  private void init() {
    this.respawntime = 300;
    this.value = 0;
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
  public void interact(
      Entity entity,
      Entity[] agents,
      ConcurrentHashMap<UUID, com.lordsofmidnight.objects.powerUps.PowerUp> activePowerUps,
      AudioController audioController) {
    if (!active) {
      return;
    }
    this.setActive(false);
  }

  @Override
  public boolean isPowerUpBox() {
    return true;
  }
}
