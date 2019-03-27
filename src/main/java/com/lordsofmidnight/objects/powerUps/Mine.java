package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.MinePellet;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Mine extends PowerUp {

  public Mine() {
    super(10, "mine");
    this.type = PowerUps.MINE;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents, AudioController audioController) {
    this.user = user;
    this.onMap = true;
    Point loc = user.getMoveInDirection(1.1, user.getFacing().getInverse());
    int x = (int) loc.getX();
    int y = (int) loc.getY();
    MinePellet mine = new MinePellet(x + 0.5, y + 0.5, user);
    pellets.put(loc, mine);
  }

  @Override
  public void trigger(Entity victim, ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      AudioController audioController) {
    Methods.kill(user, victim, audioController);
    counter = 0;
  }
}
