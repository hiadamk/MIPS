package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.mapping.Mapping;
import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Route finding algorithm that will aim to reach the junction that will next be reached by Mipsman.
 *
 * @author Lewis Ackroyd
 */
public class NextJunctionRouteFinder implements RouteFinder {

  private final Entity[] allAgents;
  private final Map map;
  private final PointSet junctions;
  private final PointMap<PointSet> edges;

  /**
   * Initialises this {@link RouteFinder} for the specified {@link Map} and corresponding junction set and edge mapping.
   *
   * @param allAgents The array of all {@link Entity Entities} in the game
   * @param map The map being searched
   * @param junctions The set of junctions for the specified {@link Map}
   * @param edges The map of edged for the specified {@link Map}
   * @author Lewis Ackroyd
   */
  public NextJunctionRouteFinder(Entity[] allAgents, Map map, PointSet junctions,
      PointMap<PointSet> edges) {
    this.allAgents = allAgents;
    this.map = map;
    this.junctions = junctions;
    this.edges = edges;
  }

  /**
   * Initialises this {@link RouteFinder} for the specified {@link Map}.
   *
   * @param allAgents The array of all {@link Entity Entities} in the game
   * @param map The map being searched
   * @author Lewis Ackroyd
   */
  public NextJunctionRouteFinder(Entity[] allAgents, Map map) {
    this.allAgents = allAgents;
    this.map = map;
    this.junctions = Mapping.getJunctions(map);
    this.edges = Mapping.getEdges(map, junctions);
  }

  /**
   * Returns the direction to travel in until the next junction is reached such that the direction is the fastest
   * to the junction in front of the target.
   *
   * @param myLocation The start point.
   * @param targetLocation The target point.
   *
   * @return The direction to travel in, or DEFAULT if no direction could be produced.
   * @author Lewis Ackroyd
   */
  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    Point mipsmanLocation = null;
    Direction mipsmanDirection = null;
    for (Entity entity : allAgents) {
      if (entity.isMipsman()) {
        mipsmanLocation = entity.getLocation().getCopy();
        mipsmanDirection = entity.getDirection();
        break;
      }
    }
    if (mipsmanLocation == null || mipsmanDirection == null) {
      return DEFAULT;
    }
    if (mipsmanDirection.isMovementDirection()) {
      targetLocation = Mapping.findNextJunction(mipsmanLocation, mipsmanDirection, map, junctions);
    }
    return new AStarRouteFinder(junctions, edges, map).getRoute(myLocation, targetLocation);
  }
}
