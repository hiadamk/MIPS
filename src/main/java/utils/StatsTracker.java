package utils;

public class StatsTracker {

  int kills;
  int deaths;
  int pointsGained;
  int pointsStolen;
  int pointsLost;
  int itemsUsed;

  public StatsTracker() {
  }

  public int getKills() {
    return kills;
  }

  public int getDeaths() {
    return deaths;
  }

  public int getPointsGained() {
    return pointsGained;
  }

  public int getPointsStolen() {
    return pointsStolen;
  }

  public int getPointsLost() {
    return pointsLost;
  }

  public int getItemsUsed() {
    return itemsUsed;
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
