package com.lordsofmidnight.ai.mapping;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Methods to facilitate the abstraction of the game {@link Map}, allowing it to be represented as a
 * graph and also to perform move validation.
 *
 * @author Lewis Ackroyd
 */
public abstract class Mapping {

  /**Generates a {@link PointSet} of all {@link Point}s on the given {@link Map}
   * that are a Junction.
   * A junction is defined as any point such that there are 2 or more adjacent path
   * squares to it, of which there are at least two points that do not share any
   * common coordinate x, y values (have no common axis value) with each other, but
   * both share exactly one with the junction. (i.e. they are diagonal to each
   * other).
   *
   * @param map The map to find junctions on.
   *
   * @return The set of {@link Point}s that are classified as a junction.
   * @author Lewis Ackroyd*/
  public static PointSet getJunctions(Map map) {

    PointSet junctions = new PointSet(map);
    Point testPoint = new Point(0,0);
    for (int x = 0; x < map.getMaxX(); x++) { //for all points on the map
      for (int y = 0; y < map.getMaxY(); y++) {
        // left right down up
        boolean[] isPath = {false, false, false, false};
        testPoint.setLocation(x,y);
        if (!map.isWall(testPoint)) {   //assumption that anything that is not a wall is moveable
          if (x > 0) { // left
            if (!map.isWall(testPoint.moveInDirection(1, Direction.LEFT))) {
              isPath[0] = true;
            }
          }
          testPoint.setLocation(x, y);
          if (x < (map.getMaxX() - 1)) { // right
            if (!map.isWall(testPoint.moveInDirection(1, Direction.RIGHT))) {
              isPath[1] = true;
            }
          }
          testPoint.setLocation(x,y);
          if (y > 0) { // down
            if (!map.isWall(testPoint.moveInDirection(1, Direction.DOWN))) {
              isPath[2] = true;
            }
          }
          testPoint.setLocation(x, y);
          if (y < (map.getMaxY() - 1)) { // up
            if (!map.isWall(testPoint.moveInDirection(1, Direction.UP))) {
              isPath[3] = true;
            }
          }

          testPoint.setLocation(x, y);
          // a point is classified as a junction if there are at least 2 adjacent path
          // points to the current one that between them do not share a common x or y
          // coordinate (i.e. they are diagonal to each other)
          if ((isPath[0] || isPath[1]) && (isPath[2] || isPath[3])) {
            junctions.add(testPoint.getCopy());
          }
        }
      }
    }
    return junctions;
  }

  /**
   * Produces a {@link PointMap}<{@link Point}>  of all edges in the current {@link Map}.
   * Edges are defines as all the junctions from a given junction that are in a direct
   * line to that given junction and share at least one common coordinate x, y value
   * (have one common axis value) and have no other junctions that meet this criteria
   * that are closer to the given junction.
   *
   * @param map The map to produce the junction pairs from.
   *
   * @return A mapping of every junction to all connected junctions.
   * @author Lewis Ackroyd
   */
  public static PointMap<PointSet> getEdges(Map map) throws IllegalArgumentException {
    return getEdges(map, getJunctions(map));
  }

  /**
   * Produces a {@link PointMap}<{@link Point}>  of all edges in the current {@link Map}.
   * Edges are defines as all the junctions from a given junction that are in a direct
   * line to that given junction and share at least one common coordinate x, y value
   * (have one common axis value) and have no other junctions that meet this criteria
   * that are closer to the given junction.
   *
   * @param map The map to produce the junction pairs from.
   * @param junctions The set of junctions on the given {@link Map}.
   *
   * @return A mapping of every junction to all connected junctions.
   * @author Lewis Ackroyd
   */
  public static PointMap<PointSet> getEdges(Map map, PointSet junctions) {
    PointMap<PointSet> edgeMap = new PointMap<>(map);
    // generates links for every junction
    for (Point p : junctions) {
      PointSet edgeSet = new PointSet(map);
      Point testPoint = p.getCopy();
      testPoint.setLocation(p.getX()-1, p.getY());
      // a wall or junction will terminate the search
      while (testPoint.getX() > 0 && !(map.isWall(testPoint))) {
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint.getCopy());
          break;
        }
        testPoint.setLocation(testPoint.getX()-1, testPoint.getY());
      }
      testPoint.setLocation(p.getX()+1, p.getY());
      // a wall or junction will terminate the search
      while (testPoint.getX() < map.getMaxX() && (!map.isWall(testPoint))) {
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint.getCopy());
          break;
        }
        testPoint.setLocation(testPoint.getX()+1, testPoint.getY());
      }
      testPoint.setLocation(p.getX(), p.getY()-1);
      // a wall or junction will terminate the search
      while (testPoint.getY() > 0 && !(map.isWall(testPoint))) {
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint.getCopy());
          break;
        }
        testPoint.setLocation(testPoint.getX(), testPoint.getY()-1);
      }
      testPoint.setLocation(p.getX(), p.getY()+1);
      // a wall or junction will terminate the search
      while (testPoint.getY() < map.getMaxY() && (!map.isWall(testPoint))) {
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint.getCopy());
          break;
        }
        testPoint.setLocation(testPoint.getX(), testPoint.getY()+1);
      }
      edgeMap.put(p, edgeSet);
    }
    return edgeMap;
  }

  /**Returns the {@link Direction} That needs to be travelled in to get from start to target.
   *
   * @param start The starting position
   * @param target The target position
   *
   * @return The direction that needs to be taken to travel between the two points.
   * @author Lewis Ackroyd*/
  public static Direction directionBetweenPoints(Point start, Point target) {
    if (start.getX() == target.getX()) {
      if (start.getY() > target.getY()) {
        return Direction.UP;
      } else {
        return Direction.DOWN;
      }
    } else if (start.getX() > target.getX()) {
      return Direction.LEFT;
    } else {
      return Direction.RIGHT;
    }
  }

  /**From the given position finds the nearest junction to it in any direction along
   * a single axis.
   *
   * @param position The position to start from
   * @param map The map that is being traversed
   * @param junctions The set of junctions on the given {@link Map}
   *
   * @return The nearest junction, or the given position if no junction can be reached
   *        by travelling along a single axis
   * @author Lewis Ackroyd*/
  public static Point findNearestJunction(Point position, Map map, PointSet junctions) {
    double x = position.getX();
    double y = position.getY();
    position = position.getGridCoord();
    double vertical = costVertical(position, map, junctions);
    double horizontal = costHorizontal(position, map, junctions);
    if (Math.abs(vertical) < Math.abs(horizontal)) {
      position.setLocation(position.getX(), position.getY() + vertical);
    } else {
      position.setLocation(position.getX() + horizontal, position.getY());
    }

    if (!Map.withinBounds(map.getMaxX(), map.getMaxY(), position)) {
      position.setLocation(x, y);
    }
    return position;
  }

  /**Finds the next junction from the given position in the direction given.
   *
   * @param position The position to start from
   * @param direction The direction to search in
   * @param map The map that is being traversed
   * @param junctions The set of junctions on the given {@link Map}
   *
   * @return The next junction, or the given position if no junction can be reached
   * @author Lewis Ackroyd*/
  public static Point findNextJunction(Point position, Direction direction, Map map,
      PointSet junctions) {
    position = position.getGridCoord();
    if (!direction.isMovementDirection()) {
      return position;
    }
    Point testPosition = position.getCopy();
    while (Map.withinBounds(map, testPosition) && !map.isWall(testPosition)) {
      if (junctions.contains(testPosition)) {
        return testPosition;
      }
      testPosition = testPosition.moveInDirection(1, direction);
    }
    return position;
  }

  /**Finds the distance to the nearest junction along the vertical axis from the given
   * {@link Point}.
   *
   * @param position The position to start from
   * @param map The map that is being traversed
   * @param junctions The set of junctions on the given {@link Map}
   *
   * @return The distance to the nearest junction
   * @author Lewis Ackroyd*/
  private static double costVertical(Point position, Map map, PointSet junctions) {
    Point up = position.getCopy();
    Point down = position.getCopy();
    boolean upWall = false;
    boolean downWall = false;
    while ((Map.withinBounds(map, up) && !map.isWall(up))
        || (Map.withinBounds(map, down) && !map.isWall(down))) {
      if (!map.isWall(up) && !upWall) {
        if (junctions.contains(up.getGridCoord())) {
          return up.getY() - position.getY();
        }
      } else {
        upWall = true;
      }
      if (!map.isWall(down) && !downWall) {
        if (junctions.contains(down.getGridCoord())) {
          return down.getY() - position.getY();
        }
      } else {
        downWall = true;
      }
      up.setLocation(up.getX(), up.getY() - 1);
      down.setLocation(down.getX(), down.getY() + 1);
    }
    return Double.MAX_VALUE;
  }

  /**Finds the distance to the nearest junction along the horizontal axis from the given
   * {@link Point}.
   *
   * @param position The position to start from
   * @param map The map that is being traversed
   * @param junctions The set of junctions on the given {@link Map}
   *
   * @return The distance to the nearest junction
   * @author Lewis Ackroyd*/
  private static double costHorizontal(Point position, Map map, PointSet junctions) {
    Point left = position.getCopy();
    Point right = position.getCopy();
    boolean leftWall = false;
    boolean rightWall = false;
    while ((Map.withinBounds(map, left) && !map.isWall(left))
        || (Map.withinBounds(map, right) && !map.isWall(right))) {
      if (!map.isWall(left) && !leftWall) {
        if (junctions.contains(left.getGridCoord())) {
          return left.getY() - position.getY();
        }
      } else {
        leftWall = true;
      }
      if (!map.isWall(right) && !rightWall) {
        if (junctions.contains(right.getGridCoord())) {
          return right.getY() - position.getY();
        }
      } else {
        rightWall = true;
      }
      left.setLocation(left.getX() - 1, left.getY());
      right.setLocation(right.getX() + 1, right.getY());
    }
    return Double.MAX_VALUE;
  }
}
