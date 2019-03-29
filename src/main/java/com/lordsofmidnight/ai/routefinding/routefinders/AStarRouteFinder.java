package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.mapping.Mapping;
import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.gamestate.points.PointSet;
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
   * Initialises the A* for the specified {@link Map} and corresponding junction set and edge
   * mapping.
   *
   * @param junctions The set of junctions for the specified {@link Map}
   * @param edges The map of edged for the specified {@link Map}
   * @param map The map being searched
   * @author Lewis Ackroyd
   */
  public AStarRouteFinder(PointSet junctions, PointMap<PointSet> edges, Map map) {
    this.junctions = junctions;
    this.edges = edges;
    this.map = map;
  }

  /**
   * Initialises the A* for the specified {@link Map} and corresponding junction set and edge
   * mapping.
   *
   * @param map The map being searched
   * @author Lewis Ackroyd
   */
  public AStarRouteFinder(Map map) {
    this.map = map;
    this.junctions = Mapping.getJunctions(map);
    this.edges = Mapping.getEdges(map, junctions);
  }

  /**
   * Returns the direction to travel in until the next junction is reached such that the direction
   * is the fastest to the target.
   *
   * @param myLocation The start point.
   * @param targetLocation The target point.
   * @return The direction to travel in, or DEFAULT if no direction could be produced.
   * @author Lewis Ackroyd
   */
  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    if (myLocation == null || targetLocation == null) {
      return DEFAULT;
    }
    myLocation = myLocation.getGridCoord();
    targetLocation = targetLocation.getGridCoord();
    if (!junctions.contains(myLocation)) {
      Point nearestJunct = Mapping.findNearestJunction(myLocation, map, junctions);
      return Mapping.directionBetweenPoints(myLocation, nearestJunct);
    }
    Point targetJunction;
    if (junctions.contains(targetLocation)) {
      targetJunction = targetLocation;
    } else {
      targetJunction = Mapping.findNearestJunction(targetLocation, map, junctions).getGridCoord();
    }
    PointMap<AStarData> visited = new PointMap<>(map);
    PointMap<AStarData> unVisited = new PointMap<>(map);
    visited.put(
        myLocation,
        new AStarData(myLocation, myLocation, 0, heuristicCost(myLocation, targetJunction)));
    Direction outDirection = DEFAULT;
    Point currentPoint = myLocation;
    while (!visited.containsKey(targetJunction) && (visited.size() < junctions.size())) {
      PointSet connections = edges.get(currentPoint);
      for (Point connection : connections) {
        if (!visited.containsKey(connection)) {
          double moveCost =
              visited.get(currentPoint).getMoveCost() + movementCost(currentPoint, connection);
          double estimatedCost = moveCost + heuristicCost(connection, targetJunction);
          unVisited.put(
              connection, new AStarData(connection, currentPoint, moveCost, estimatedCost));
        }
      }
      if (unVisited.size() == 0) { // target location is not reachable
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

  /**
   * The cost to move from the current location to the target location
   *
   * @param start The start position
   * @param target The target position
   * @return The cost of movement between the two points
   * @author Lewis Ackroyd
   */
  private double movementCost(Point start, Point target) {
    return start.distance(target);
  }

  /**
   * The estimated cost to move from the current location to the target location
   *
   * @param start The start position
   * @param target The target position
   * @return The estimated cost of movement between the two points
   * @author Lewis Ackroyd
   */
  private double heuristicCost(Point start, Point target) {
    return start.distance(target);
  }

  /**
   * Class that stores data on each point that is required to carry out the A* search
   *
   * @author Lewis Ackroyd
   */
  private class AStarData {

    private final Point myPosition;
    private final Point parent;
    private final double moveCost;
    private final double estimatedCost;

    /**
     * Stores the specified data.
     *
     * @param myPosition The point of which the data is about
     * @param parent The point that was travelled from to reach this point
     * @param moveCost The cost to reach this {@link Point} from the start position
     * @param estimatedCost The estimated cost remaining to reach the target
     * @author Lewis Ackroyd
     */
    public AStarData(Point myPosition, Point parent, double moveCost, double estimatedCost) {
      this.myPosition = myPosition;
      this.parent = parent;
      this.moveCost = moveCost;
      this.estimatedCost = estimatedCost;
    }

    /**
     * @return The point of which this data is about
     * @author Lewis Ackroyd
     */
    public Point getMyPosition() {
      return myPosition;
    }

    /**
     * @return The point that was travelled from to reach this point
     * @author Lewis Ackroyd
     */
    public Point getParentPosition() {
      return parent;
    }

    /**
     * @return The cost to reach this {@link Point} from the start position
     * @author Lewis Ackroyd
     */
    public double getMoveCost() {
      return moveCost;
    }

    /**
     * @return The estimated cost remaining to reach the target
     * @author Lewis Ackroyd
     */
    public double getEstimatedCost() {
      return estimatedCost;
    }
  }
}
