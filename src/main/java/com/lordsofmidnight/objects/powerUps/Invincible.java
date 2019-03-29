package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The invincible powerUp making the entity that uses it invincible for its duration
 */
public class Invincible extends PowerUp {

  public Invincible() {
    super(200, "invincible");
    this.type = PowerUps.INVINCIBLE;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents,
      AudioController audioController) {
    this.user = user;
    activePowerUps.put(id, this);
    this.effected = user;
    user.setInvincible(true);
    counter = 0;
    audioController.playSound(Sounds.INVINCIBLE);
  }

  @Override
  public boolean incrementTime(AudioController audioController) {
    super.incrementTime(audioController);
    if (counter == EFFECTTIME) {
      effected.setInvincible(false);
      return true;
    }
    return false;
  }
}
