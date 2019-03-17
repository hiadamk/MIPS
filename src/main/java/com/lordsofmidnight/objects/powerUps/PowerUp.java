package com.lordsofmidnight.objects.powerUps;

import java.util.HashMap;
import java.util.UUID;

import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;

public abstract class PowerUp {

  protected final String NAME;
  protected final int EFFECTTIME;
  public UUID id;
  protected Entity effected;
  protected int counter = 500;
  protected Boolean onMap = false;
  protected Entity user;
  protected int currentFrame = 0;
  protected com.lordsofmidnight.utils.enums.PowerUp type;

  public PowerUp(int effectTime, String name) {
    this.EFFECTTIME = effectTime;
    this.NAME = name;
    id = UUID.randomUUID();
  }

  /**
   * Used to communicate powerups to clients
   *
   * @return the PowerUp corresponding to the int provided
   */
  public static PowerUp fromInt(int n) {
    switch (n) {
      case 0:
        return new Web();
      case 1:
        return new Speed();
      case 2:
        return new Blueshell();
      case 3:
        return new Invincible();
      case 4:
        return new Mine();
    }
    return null;
  }

  public com.lordsofmidnight.utils.enums.PowerUp getType() {
    return type;
  }

  public Boolean getOnMap() {
    return onMap;
  }

  /**
   * Called when the powerUp that is placed or used on another player is triggered
   *
   * @param victim The entity effected by the powerUp
   * @param activePowerUps All active powerUps in the game
   */
  public void trigger(Entity victim, HashMap<UUID, PowerUp> activePowerUps) {
  }

  /**
   * Called each physics update to increment the timers
   *
   * @return if the powerUp has finished and should be removed
   */
  public boolean incrementTime() {
    counter++;
    return false;
  }

  /**
   * Called when the player uses this powerUp
   *
   * @param user The entity that used the powerUp
   * @param activePowerUps All active powerUps in the game
   */
  public void use(
      Entity user,
      HashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents) {
  }

  /**
   * Used to communicate powerups to clients
   *
   * @return the int corresponding to the powerup's enum
   */
  public int toInt() {
    switch (type) {
      case WEB:
        return 0;
      case SPEED:
        return 1;
      case BLUESHELL:
        return 2;
      case INVINCIBLE:
        return 3;
      case MINE:
        return 4;
    }
    return -1;
  }

  public Entity getUser() {
    return this.user;
  }

  @Override
  public String toString() {
    return this.NAME;
  }

  public void incrementFrame() {
    this.currentFrame++;
  }

  public int getCurrentFrame() {
    return this.currentFrame;
  }
}
