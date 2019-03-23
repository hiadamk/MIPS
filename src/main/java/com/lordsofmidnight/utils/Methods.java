package com.lordsofmidnight.utils;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  public static void kill(Entity killer, Entity victim) {
    if (!killer.isMipsman() && victim.isMipsman()) {
      victim.setMipsman(false);
      killer.setMipsman(true);
      System.out.println("swapped mips");
    }
    killer.increaseKills();
    victim.setDead(true);
    victim.setKilledBy(killer.getName()+killer.getClientId());
    if (victim.getScore() > 0) {
      int points = (int) (victim.getScore() * 0.1);
      points = points < 1 ? 1 : points;
      victim.incrementScore(-points);
      killer.incrementScore(points);
      killer.increasePointsStolen(points);
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

  public static String[] getRandomNames(int i) {
    String[] namesList = {"Ian", "Ghica", "Eike", "Mark", "Sujoy", "Levy", "Volker"};
    ArrayList<String> names = new ArrayList<String>(Arrays.asList(namesList));
    Random r = new Random();
    String[] selected = new String[i];
    for (int j = 0; j < i; j++) {
      selected[j] = "Bot " + names.remove(r.nextInt(names.size()));
    }
    return selected;
  }
}
