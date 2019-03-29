package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.Awards;

/**
 * Class to track the entities statistics throught the game
 */
public class StatsTracker {

  private int kills;
  private int deaths;
  private int pointsGained;
  private int pointsStolen;
  private int pointsLost;
  private int itemsUsed;

  /**
   * @param award The award to return the stat for
   * @return The number for that award
   */
  public int getStat(Awards award) {
    switch (award) {
      case MOST_KILLS:
        return kills;
      case MOST_DEATHS:
        return deaths;
      case MOST_POINTS:
        return pointsGained;
      case MOST_POINTS_STOLEN:
        return pointsStolen;
      case MOST_POINTS_LOST:
        return pointsLost;
      case MOST_ITEMS_USED:
        return itemsUsed;
    }
    return -1;
  }

  /**
   * Increases the number of kills by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increaseKills(int... i) {
    if (i.length > 0) {
      kills += i[0];
    } else {
      kills++;
    }
  }

  /**
   * Increases the number of deaths by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increaseDeaths(int... i) {
    if (i.length > 0) {
      deaths += i[0];
    } else {
      deaths++;
    }
  }

  /**
   * Increases the number of points gained by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increasePointsGained(int... i) {
    if (i.length > 0) {
      pointsGained += i[0];
    } else {
      pointsGained++;
    }
  }

  /**
   * Increases the number of points stolen by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increasePointsStolen(int... i) {
    if (i.length > 0) {
      pointsStolen += i[0];
    } else {
      pointsStolen++;
    }
  }

  /**
   * Increases the number of points lost by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increasePointsLost(int... i) {
    if (i.length > 0) {
      pointsLost += i[0];
    } else {
      pointsLost++;
    }
  }

  /**
   * Increases the number of items used by amount given or by 1
   *
   * @param i optional variable for number to increase by
   */
  public void increaseItemsUsed(int... i) {
    if (i.length > 0) {
      itemsUsed += i[0];
    } else {
      itemsUsed++;
    }
  }
}
