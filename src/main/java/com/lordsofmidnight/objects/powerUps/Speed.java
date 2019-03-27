package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class Speed extends PowerUp {

  public Speed() {
    super(300, "speed");
    this.type = PowerUps.SPEED;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents, AudioController audioController) {
    this.user = user;
    user.changeBonusSpeed(0.03);
    activePowerUps.put(id, this);
    this.effected = user;
    audioController.playSound(Sounds.SPEED);
    counter = 0;
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      user.changeBonusSpeed(-0.03);
      return true;
    }
    return false;
  }
}
