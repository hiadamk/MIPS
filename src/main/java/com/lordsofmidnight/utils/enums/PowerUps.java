package com.lordsofmidnight.utils.enums;


/**
 * An enum for the powerUps that both stores them and implements them
 *
 * @author Matthew Jones
 */
public enum PowerUps {
  WEB("web"), SPEED("speed"), ROCKET("rocket"), INVINCIBLE("invincible"), MINE("mine");

  private final String NAME;

  PowerUps(String name) {
    this.NAME = name;
  }

  /**
   * Used to communicate powerups to clients
   *
   * @return the int corresponding to the powerup's enum
   */
  public int toInt() {
    // TODO Auto-generated method stub
    switch (this) {
      case WEB:
        return 0;
      case SPEED:
        return 1;
      case ROCKET:
        return 2;
      case INVINCIBLE:
        return 3;
      case MINE:
        return 4;
    }
    return -1;
  }

  @Override
  public String toString() {
    return this.NAME;
  }

}




