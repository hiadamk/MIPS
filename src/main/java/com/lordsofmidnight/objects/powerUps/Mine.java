package com.lordsofmidnight.objects.powerUps;

import java.util.HashMap;
import java.util.UUID;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.objects.PowerUpBox;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.gamestate.points.Point;

public class Mine extends PowerUp {

  public Mine() {
    super(10, "mine");
    this.type = com.lordsofmidnight.utils.enums.PowerUp.MINE;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents) {
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
  public void trigger(Entity victim, HashMap<UUID, PowerUp> activePowerUps) {
    Methods.kill(user, victim);
    activePowerUps.put(id, this);
    this.effected = victim;
    counter = 0;
  }
}
