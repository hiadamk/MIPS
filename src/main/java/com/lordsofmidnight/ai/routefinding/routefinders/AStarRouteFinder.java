package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.mapping.Mapping;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.ai.routefinding.AStarData;
import com.lordsofmidnight.ai.routefinding.RouteFinder;

import java.util.Iterator;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * A* route finding algorithm implementation.
 *
 * @author Lewis Ackroyd
 */
public class AStarRouteFinder implements RouteFinder {

  private final PointSet junctions;
  private final PointMap<PointSet> edges;
  private final Map map;

  /**
   * Creates an instance of this routeFinder.
   */
  public AStarRouteFinder(PointSet junctions, PointMap<PointSet> edges, Map map) {
    this.junctions = junctions;
    this.edges = edges;
    this.map = map;
  }

  /**
   * Returns the direction to travel in until the next junction is reached.
   *
   * @param myLocPointDouble The start point.
   * @param targetLocPointDouble The target point.
   * @return The direction to travel in.
   * @throws NullPointerException One or both positions are null.
   */
  @Override
  public Direction getRoute(Point myLocPointDouble, Point targetLocPointDouble) {
    if (myLocPointDouble == null || targetLocPointDouble == null) {
      throw new NullPointerException("One or both positions are null.");
    }
    Point myLocation = myLocPointDouble.getGridCoord();
    Point targetLocation = targetLocPointDouble.getGridCoord();
    if (!junctions.contains(myLocation)) {
      Point nearestJunct = Mapping.findNearestJunction(myLocPointDouble, map, junctions);
      return Mapping.directionBetweenPoints(myLocPointDouble, nearestJunct);
    }
    Point targetJunction;
    if (junctions.contains(targetLocation)) {
      targetJunction = targetLocation;
    } else {
      targetJunction =
          Mapping.findNearestJunction(targetLocPointDouble, map, junctions).getGridCoord();
    }
    PointMap<AStarData> visited = new PointMap<>(map);
    PointMap<AStarData> unVisited = new PointMap<>(map);
    visited.put(myLocation, new AStarData(myLocation, myLocation, 0, heuristicCost(myLocation, targetJunction)));
    Direction outDirection = DEFAULT;
    Point currentPoint = myLocation;
    while (!visited.containsKey(targetJunction) && (visited.size() < junctions.size())) {
      PointSet connections = edges.get(currentPoint);
      Iterator<Point> iterator = connections.iterator();
      while (iterator.hasNext()) {
        Point connection = iterator.next();
        if (!visited.containsKey(connection)) {
          double moveCost =
              visited.get(currentPoint).getMoveCost()
                  + movementCost(currentPoint, connection);
          double estimatedCost = moveCost + heuristicCost(connection, targetJunction);
          unVisited
              .put(connection, new AStarData(connection, currentPoint, moveCost, estimatedCost));
        }
      }
      if (unVisited.size()==0) {  //target location is not reachable
        return DEFAULT;
      }
      double lowestCost = Double.MAX_VALUE;
      for (Point key : unVisited.keySet()) {
        AStarData data = unVisited.get(key);
        if (data.getEstimatedCost() < lowestCost) {
          currentPoint = key;
          lowestCost = data.getEstimatedCost();
        }
      }
      AStarData data = unVisited.get(currentPoint);
      unVisited.remove(currentPoint);
      visited.put(currentPoint, data);

    }
    AStarData data = visited.get(currentPoint);
    while (!data.getMyPosition().equals(data.getParentPosition())) {
      if (data.getParentPosition().equals(myLocation)) {
        outDirection = Mapping.directionBetweenPoints(myLocation, data.getMyPosition());
        break;
      }
      data = visited.get(data.getParentPosition());
    }
    return outDirection;
  }

  private double movementCost(Point start, Point target) {
    return start.distance(target);
  }

  private double heuristicCost(Point start, Point target) {
    return start.distance(target);
  }
}
