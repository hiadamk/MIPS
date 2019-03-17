package com.lordsofmidnight.objects.powerUps;

import java.util.HashMap;
import java.util.UUID;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;


public class Speed extends PowerUp {

  public Speed() {
    super(300, "speed");
    this.type = com.lordsofmidnight.utils.enums.PowerUp.SPEED;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents) {
    this.user = user;
    user.changeBonusSpeed(0.03);
    activePowerUps.put(id, this);
    this.effected = user;
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
