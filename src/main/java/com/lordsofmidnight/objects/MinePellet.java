package com.lordsofmidnight.objects;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.Methods;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinePellet extends Pellet {

  private boolean hidden;
  private boolean detonated = false;
  private int hidden_timer = 300;
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
  public boolean interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps, AudioController audioController) {
    if(!detonated){
      Methods.kill(placer, entity);
    }
    audioController.playSound(Sounds.EXPLODE);
    detonated = true;
    return false;
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
