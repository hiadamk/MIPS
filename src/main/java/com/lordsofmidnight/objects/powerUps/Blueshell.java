package com.lordsofmidnight.objects.powerUps;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.UUID;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.Methods;
import java.util.concurrent.ConcurrentHashMap;

public class Blueshell extends PowerUp {

  private ConcurrentHashMap<UUID, PowerUp> activePowerUps;

  private int currentFrame = 0;

  private boolean launched = false;
  private boolean targeted = false;

  private Point2D.Double startLocation;
  private Point2D.Double endLocation;

  public Blueshell() {
    super(600, "blueshell");
    this.type = com.lordsofmidnight.utils.enums.PowerUp.BLUESHELL;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents) {
    effected = agents[Methods.findWinner(agents)];
    this.user = user;
    this.activePowerUps = activePowerUps;
    activePowerUps.put(id, this);
    this.effected = agents[Methods.findWinner(agents)];
  }

  @Override
  public boolean incrementTime() {
    super.incrementTime();
    if (counter == EFFECTTIME) {
      PowerUp killer = new Mine();
      killer.user = user;
      killer.trigger(effected, activePowerUps);
      user.increaseKills();
      return true;
    }
    return false;
  }

  public void incrementFrame(){
    this.currentFrame++;
  }

  public int getTime(){
    return this.counter;
  }

  public int getMaxTime(){
    return this.EFFECTTIME;
  }

  public boolean isLaunched() {
    return launched;
  }

  public void setLaunched(boolean launched) {
    this.launched = launched;
  }

  public boolean isTargeted() {
    return targeted;
  }

  public void setTargeted(boolean targeted) {
    this.targeted = targeted;
  }

  public Double getStartLocation() {
    return startLocation;
  }

  public void setStartLocation(Double startLocation) {
    this.startLocation = startLocation;
  }

  public Double getEndLocation() {
    return endLocation;
  }

  public void setEndLocation(Double endLocation) {
    this.endLocation = endLocation;
  }

  public Entity getTargeted(){
    return this.effected;
  }
}
