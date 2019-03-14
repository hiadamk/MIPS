package com.lordsofmidnight.utils;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.enums.Direction;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  public static void kill(Entity killer, Entity victim) {
    killer.increaseKills();
    victim.setDead(true);
    if (victim.getScore() > 0) {
      int points = (int) (victim.getScore() * 0.1);
      points = points < 1 ? 1 : points;
      victim.incrementScore(-points);
      killer.incrementScore(points);
    }
  }

  /**
   * checks whether a movement in a certain direction is valid
   *
   * @param d Direction to move in
   * @param p Point to move from
   * @param m Map the point is located on
   * @return true if the move is valid
   * @author Alex Banks, Matty Jones
   */
  public static boolean validateDirection(Direction d, Point p, Map m) {
    Point movedPoint = p.getCopy().moveInDirection(1, d);
    return !m.isWall(movedPoint) && p.isCentered();
  }

  public static int findWinner(Entity[] agents) {
    int winner = 0;
    int maxScore = 0;
    for (Entity e : agents) {
      if (e.getScore() > maxScore) {
        winner = e.getClientId();
        maxScore = e.getScore();
      }
    }
    return winner;
  }
}
