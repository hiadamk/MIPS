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

  public NextJunctionRouteFinder(Entity[] allAgents, Map map, PointSet junctions,
      PointMap<PointSet> edges) {
    this.allAgents = allAgents;
    this.map = map;
    this.junctions = junctions;
    this.edges = edges;
  }

  /**
   * */
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
    targetLocation = Mapping.findNextJunction(mipsmanLocation, mipsmanDirection, map, junctions);
    return new AStarRouteFinder(junctions, edges, map).getRoute(myLocation, targetLocation);
  }
}
