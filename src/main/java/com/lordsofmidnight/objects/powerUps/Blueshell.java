package com.lordsofmidnight.objects.powerUps;

import java.util.HashMap;
import java.util.UUID;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.Methods;

public class Blueshell extends PowerUp {

  private HashMap<UUID, PowerUp> activePowerUps;

  public Blueshell() {
    super(50, "blueshell");
    this.type = com.lordsofmidnight.utils.enums.PowerUp.BLUESHELL;
  }

  @Override
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents) {
    effected = agents[Methods.findWinner(agents)];
    this.user = user;
    this.activePowerUps = activePowerUps;
    this.effected = agents[Methods.findWinner(agents)];
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      PowerUp killer = new Mine();
      killer.trigger(effected, activePowerUps);
      killer.user = user;
      user.increaseKills();
      return true;
    }
    return false;
  }

}
