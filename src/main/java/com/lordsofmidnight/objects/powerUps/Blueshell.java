package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.UUID;
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
    this.type = PowerUps.BLUESHELL;
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
      Methods.kill(user, effected);
      return true;
    }
    return false;
  }

  public void incrementFrame(){
    this.currentFrame++;
  }

  /**
   * @return the counter for the animation of the rocket
   */
  public int getTime(){
    return this.counter;
  }

  /**
   *
   * @return the effect time
   */
  public int getMaxTime(){
    return this.EFFECTTIME;
  }

  /**
   *
   * @return if the rocket has been launched yet
   */
  public boolean isLaunched() {
    return launched;
  }

  /**
   * Sets weather the rocket has been launched or not
   * @param launched True if launched
   */
  public void setLaunched(boolean launched) {
    this.launched = launched;
  }

  /**
   *
   * @return if the rocket has a target
   */
  public boolean isTargeted() {
    return targeted;
  }

  /**
   *
   * @return the location that the rocket was launched from
   */
  public Double getStartLocation() {
    return startLocation;
  }

  /**
   *
   * @param startLocation the location that the rocket was launched from
   */
  public void setStartLocation(Double startLocation) {
    this.startLocation = startLocation;
  }

  /**
   *
   * @return the target location of the rocket
   */
  public Double getEndLocation() {
    return endLocation;
  }

  /**
   *
   * @param endLocation the target location of the rocket
   */
  public void setEndLocation(Double endLocation) {
    this.endLocation = endLocation;
  }

  /**
   *
   * @return the target entity
   */
  public Entity getTargeted(){
    return this.effected;
  }

  /**
   *
   * @param targeted sets if the rocket has been targeted
   */
  public void setTargeted(boolean targeted) {
    this.targeted = targeted;
  }
}
