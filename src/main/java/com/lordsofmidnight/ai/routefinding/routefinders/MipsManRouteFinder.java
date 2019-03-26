package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.ai.routefinding.SampleSearch;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Route finding algorithm that controls Mipsman. Will aim to reach the nearest pellet whilst
 * avoiding any ghouls.
 *
 * @author Lewis Ackroyd
 */
public class MipsManRouteFinder implements RouteFinder {

  private static final int GHOUL_NEGATIVE_MULTIPLIER = 2;
  private static final int PELLET_SEARCH_DEPTH = 25;
  private static final int GHOUL_SEARCH_DEPTH = 8;
  private PointMap<Pellet> pellets;
  private Entity[] gameAgents;
  private final Map map;

  /**Initialises this {@link RouteFinder} with the current {@link Map} and objects on it.
   *
   * @param pellets A mapping from every point containing a pellet, to that pellet
   * @param gameAgents The array containing all entities in the game
   * @param map The map being used
   * @author Lewis Ackroyd*/
  public MipsManRouteFinder(PointMap<Pellet> pellets, Entity[] gameAgents, Map map) {
    this.pellets = pellets;
    this.gameAgents = gameAgents;
    this.map = map;
  }

  /**
   * Returns the direction to travel in until the next junction is reached such that the direction avoids
   * other {@link Entity Entities} whilst also travelling towards the nearest {@link com.lordsofmidnight.objects.PowerUpBox PowerUpBox}.
   *
   * @param myLocation The start point.
   * @param targetLocation The target point.
   *
   * @return The direction to travel in, or DEFAULT if no direction could be produced.
   * @author Lewis Ackroyd
   */
  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    SampleSearch sampleSearch = new SampleSearch(PELLET_SEARCH_DEPTH, map);
    class GhoulCountCondition implements SampleSearch.ConditionalInterface {
      @Override
      public boolean condition(Point position) {
        for (Entity entity : gameAgents) {
          if (entity.getLocation().getGridCoord().equals(position)) {
            if (!entity.isMipsman()) {
              return true;
            }
          }
        }
        return false;
      }
    }
    int[] ghoulCounts = sampleSearch.getDirectionCounts(myLocation, new GhoulCountCondition());

    class PowerUpBoxCountCondition implements SampleSearch.ConditionalInterface {
      @Override
      public boolean condition(Point position) {
        if (pellets.containsKey(position)) {
            Pellet pellet = pellets.get(position);
            if (pellet.isPowerUpBox()) {
                return true;
            }
        }
        return false;
      }
    }
    sampleSearch = new SampleSearch(GHOUL_SEARCH_DEPTH, map);
    int[] powerUpBoxCounts = sampleSearch.getDirectionCounts(myLocation, new PowerUpBoxCountCondition());

    int[] totals = {1, 1, 1, 1};
    for (int i = 0; i<totals.length; i++) {
        totals[i] += powerUpBoxCounts[i]-(ghoulCounts[i]*GHOUL_NEGATIVE_MULTIPLIER);
    }

    return maxDirection(totals);
  }

  /**Determines which direction has the highest preference and returns it.
   *
   * @param totals The array of values representing all directions for the conditions specified
   *
   * @return The direction with the highest associated value
   * @author Lewis Ackroyd*/
  private Direction maxDirection(int[] totals) {
      int firstTwoIndex;
      if (totals[Direction.UP.toInt()]>totals[Direction.DOWN.toInt()]) {
          firstTwoIndex = Direction.UP.toInt();
      }
      else {
          firstTwoIndex = Direction.DOWN.toInt();
      }
      int secondTwoIndex;
      if (totals[Direction.LEFT.toInt()]>totals[Direction.RIGHT.toInt()]) {
          secondTwoIndex = Direction.LEFT.toInt();
      }
      else {
          secondTwoIndex = Direction.RIGHT.toInt();
      }
      if (totals[firstTwoIndex]>totals[secondTwoIndex]) {
          return Direction.fromInt(firstTwoIndex);
      }
      else {
          return Direction.fromInt(secondTwoIndex);
      }
  }
}
