package ai.routefinding.routefinders;

import ai.mapping.JunctionSet;
import ai.mapping.Mapping;
import ai.routefinding.AStarData;
import ai.routefinding.RouteFinder;
import java.util.HashMap;
import java.util.HashSet;
import utils.Map;
import utils.Point;
import utils.enums.Direction;

/**
 * A* route finding algorithm implementation.
 *
 * @author Lewis Ackroyd
 */
public class AStarRouteFinder implements RouteFinder {

  private static final boolean COMPLETE = false;

  private final JunctionSet junctions;
  private final HashMap<Point, HashSet<Point>> edges;
  private final Map map;

  /**
   * Creates an instance of this routeFinder.
   */
  public AStarRouteFinder(JunctionSet junctions, HashMap<Point, HashSet<Point>> edges, Map map) {
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

    if (!COMPLETE) {
      return new RandomRouteFinder().getRoute(myLocPointDouble,targetLocPointDouble);
    }
    System.out.println("A");
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
    HashMap<Point, AStarData> visited = new HashMap<Point, AStarData>();
    HashMap<Point, AStarData> unVisited = new HashMap<Point, AStarData>();
    visited.put(myLocation, new AStarData(myLocation, myLocation, 0));
    Direction outDirection = Direction.UP; // default
    Point currentPoint = myLocation;
    while (!visited.containsKey(targetJunction) && (visited.size() < junctions.size())) {
      HashSet<Point> connections = edges.get(currentPoint);
      for (Point connection : connections) {
        if (!visited.containsKey(connection)) {
          double cost =
              visited.get(currentPoint).getCost()
                  + movementCost(currentPoint, connection)
                  + heuristicCost(connection, targetJunction);
          unVisited.put(connection, new AStarData(currentPoint, connection, cost));
        }
      }
      double lowestCost = Double.MAX_VALUE;
      for (Point key : unVisited.keySet()) {
        AStarData data = unVisited.get(key);
        if (data.getCost() < lowestCost) {
          currentPoint = key;
          lowestCost = data.getCost();
        }
      }
      AStarData data = unVisited.get(currentPoint); //
      unVisited.remove(currentPoint);
      visited.put(currentPoint, data);
    }
    for (Point key : visited.keySet()) {
      AStarData data = visited.get(key);
      if (myLocation.equals(data.getParentPosition()) && !myLocation.equals(data.getMyPosition())) {
        outDirection =
            Mapping.directionBetweenPoints(data.getMyPosition(), data.getParentPosition());
        break;
      }
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
