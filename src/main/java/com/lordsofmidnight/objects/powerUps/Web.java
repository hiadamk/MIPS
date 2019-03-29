package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Web powerup leaving a fake item box, that snares those who run into it, behind the entity
 * that uses it
 */
public class Web extends PowerUp {

  public Web() {
    super(100, "web");
    this.type = PowerUps.WEB;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents,
      AudioController audioController) {
    this.user = user;
    this.onMap = true;
    Point loc = user.getMoveInDirection(1.1, user.getFacing().getInverse());
    int x = (int) loc.getX();
    int y = (int) loc.getY();
    PowerUpBox box = new PowerUpBox(x + 0.5, y + 0.5);
    box.setTrap(this);
    pellets.put(loc, box);
  }

  @Override
  public void trigger(
      Entity victim,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      AudioController audioController) {
    if (victim.isInvincible()) {
      return;
    }
    victim.setStunned(true);
    activePowerUps.put(id, this);
    this.effected = victim;
    counter = 0;
    audioController.playSound(Sounds.TRAPPED);
  }

  @Override
  public boolean incrementTime(AudioController audioController) {
    super.incrementTime(audioController);
    if (counter == EFFECTTIME) {
      effected.setStunned(false);
      return true;
    }
    return false;
  }
}
