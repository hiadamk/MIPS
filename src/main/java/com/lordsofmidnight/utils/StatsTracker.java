package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.Awards;

public class StatsTracker {

  int kills;
  int deaths;
  int pointsGained;
  int pointsStolen;
  int pointsLost;
  int itemsUsed;

  public StatsTracker() {
  }

  public int getStat(Awards award){
    switch (award){
      case MOST_KILLS:
        return kills;
      case MOST_DEATHS:
        return deaths;
      case MOST_POINTS:
        return pointsGained;
      case MOST_POINTS_STOLEN:
        return  pointsStolen;
      case MOST_POINTS_LOST:
        return pointsStolen;
      case MOST_ITEMS_USED:
        return itemsUsed;
    }
    return -1;
  }

  public void increaseKills(int... i) {
    if (i.length > 0) {
      kills += i[0];
    } else {
      kills++;
    }
  }

  public void increaseDeaths(int... i) {
    if (i.length > 0) {
      deaths += i[0];
    } else {
      deaths++;
    }
  }

  public void increasePointsGained(int... i) {
    if (i.length > 0) {
      pointsGained += i[0];
    } else {
      pointsGained++;
    }
  }

  public void increasePointsStolen(int... i) {
    if (i.length > 0) {
      pointsStolen += i[0];
    } else {
      pointsStolen++;
    }
  }

  public void increasePointsLost(int... i) {
    if (i.length > 0) {
      pointsLost += i[0];
    } else {
      pointsLost++;
    }
  }

  public void increaseItemsUsed(int... i) {
    if (i.length > 0) {
      itemsUsed += i[0];
    } else {
      itemsUsed++;
    }
  }
}
