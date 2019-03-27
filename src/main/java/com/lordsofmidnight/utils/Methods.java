package com.lordsofmidnight.utils;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Methods {

  @Deprecated //TODO remove
  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  /**
   * Handles everything to do with an entity killing another
   *
   * @param killer The entity doing the killing
   * @param victim The entity being killed
   */
  public static void kill(Entity killer, Entity victim) {
    if (victim.isInvincible()) {
      return;
    }
    if (!killer.isMipsman() && victim.isMipsman()) {
      victim.setMipsman(false);
      killer.setMipsman(true);
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

  public static void kill(Entity killer, Entity victim, AudioController audioController) {
    if (victim.isInvincible()) {
      return;
    }
    if (!killer.isMipsman() && victim.isMipsman()) {
      victim.setMipsman(false);
      killer.setMipsman(true);
      audioController.playSound(Sounds.MIPS);
    }
    killer.increaseKills();
    victim.setDead(true);
    audioController.playSound(Sounds.EXPLODE);
    victim.setKilledBy(killer.getName() + killer.getClientId());
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

  /**
   * Finds the player in first place
   * @param agents the list of game agents
   * @return the id of the entity with the highest score
   */
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

  /**
   * Generates a list of random names for the AI players in the game
   * @param i The number of names to pick
   * @return The array of names
   */
  public static String[] getRandomNames(int i) {
    String[] namesList = {"Ian", "Ghica", "Eike", "Mark", "Sujoy", "Levy", "Volker"};
    ArrayList<String> names = new ArrayList<String>(Arrays.asList(namesList));
    Random r = new Random();
    String[] selected = new String[i];
    for (int j = 0; j < i; j++) {
      selected[j] = names.remove(r.nextInt(names.size())) + "Bot";
    }
    return selected;
  }
}
