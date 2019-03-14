package com.lordsofmidnight.ai.mapping;

import java.util.Iterator;

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

  public static PointSet getJunctions(Map map) {

    PointSet junctions = new PointSet(map);
    for (int x = 0; x < map.getMaxX(); x++) {
      for (int y = 0; y < map.getMaxY(); y++) {
        // left right down up
        boolean[] isPath = {false, false, false, false};
        if (!map.isWall(new Point(x, y))) {
          if (x > 0) { // left
            if (!map.isWall(new Point(x - 1, y))) {
              isPath[0] = true;
            }
          }
          if (x < (map.getMaxX() - 1)) { // right
            if (!map.isWall(new Point(x + 1, y))) {
              isPath[1] = true;
            }
          }
          if (y > 0) { // down
            if (!map.isWall(new Point(x, y - 1))) {
              isPath[2] = true;
            }
          }
          if (y < (map.getMaxY() - 1)) { // up
            if (!map.isWall(new Point(x, y + 1))) {
              isPath[3] = true;
            }
          }

          // a point is classified as a junction if there are at least 2 adjacent path
          // points to the current one that between them do not share a common x or y
          // coordinate (i.e. they are diagonal to each other
          if ((isPath[0] || isPath[1]) && (isPath[2] || isPath[3])) {
            junctions.add(new Point(x, y));
          }
        }
      }
    }
    return junctions;
  }

  /**
   * Produces a map from all the junctions to all other junctions that are in a direct line to them
   * along path with no obstructions (including walls and other junctions)
   *
   * @param map The map to produce the junction pairs from.
   * @return A mapping of every junction to all connected junctions.
   */
  public static PointMap<PointSet> getEdges(Map map) throws IllegalArgumentException {
    return getEdges(map, getJunctions(map));
  }

  public static PointMap<PointSet> getEdges(Map map, PointSet junctions) {
    PointMap<PointSet> edgeMap = new PointMap<>(map);
    // generates links for every junction
    Iterator<Point> iterator = junctions.iterator();
    while (iterator.hasNext()) {
      Point p = iterator.next();
      PointSet edgeSet = new PointSet(map);
      double currentX = p.getX() - 1;
      // a wall or junction will terminate the search
      while (currentX > 0 && !(map.isWall(new Point(currentX, p.getY())))) {
        Point testPoint = new Point(currentX, p.getY());
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint);
          break;
        }
        currentX--;
      }
      currentX = p.getX() + 1;
      // a wall or junction will terminate the search
      while (currentX < map.getMaxX() && (!map.isWall(new Point(currentX, p.getY())))) {
        Point testPoint = new Point(currentX, p.getY());
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint);
          break;
        }
        currentX++;
      }
      double currentY = p.getY() - 1;
      // a wall or junction will terminate the search
      while (currentY > 0 && !(map.isWall(new Point(p.getX(), currentY)))) {
        Point testPoint = new Point(p.getX(), currentY);
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint);
          break;
        }
        currentY--;
      }
      currentY = p.getY() + 1;
      // a wall or junction will terminate the search
      while (currentY < map.getMaxY() && (!map.isWall(new Point(p.getX(), currentY)))) {
        Point testPoint = new Point(p.getX(), currentY);
        if (junctions.contains(testPoint)) {
          // if a junction is found it is added to the edge pairing
          edgeSet.add(testPoint);
          break;
        }
        currentY++;
      }
      edgeMap.put(p, edgeSet);
    }
    return edgeMap;
  }

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

  public static Point findNearestJunction(Point position, Map map, PointSet junctions) {
    Point output = new Point(Math.floor(position.getX()), Math.floor(position.getY()));
    double vertical = costVertical(output, map, junctions);
    double horizontal = costHorizontal(output, map, junctions);
    if (Math.abs(vertical) < Math.abs(horizontal)) {
      output.setLocation(output.getX(), output.getY() + vertical);
    } else {
      output = new Point(output.getX() + horizontal, output.getY());
    }

    if (withinBounds(map.getMaxX(), map.getMaxY(), output)) {
      return output;
    }
    return position;
  }

  public static Point findNextJunction(Point position, Direction direction, Map map,
      PointSet junctions) {
    position = position.getGridCoord();
    if (!direction.isMovementDirection()) {
      return position;
    }
    Point testPosition = position.getCopy();
    while (withinBounds(map, testPosition) && !map.isWall(testPosition)) {
      if (junctions.contains(testPosition)) {
        return testPosition;
      }
      testPosition = testPosition.moveInDirection(1, direction);
    }
    return position;
  }

  private static double costVertical(Point position, Map map, PointSet junctions) {
    Point up = new Point(position.getX(), position.getY());
    Point down = new Point(position.getX(), position.getY());
    boolean upWall = false;
    boolean downWall = false;
    while ((withinBounds(map, up) && !map.isWall(up))
        || (withinBounds(map, down) && !map.isWall(down))) {
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

  private static double costHorizontal(Point position, Map map, PointSet junctions) {
    Point left = new Point(position.getX(), position.getY());
    Point right = new Point(position.getX(), position.getY());
    boolean leftWall = false;
    boolean rightWall = false;
    while ((withinBounds(map, left) && !map.isWall(left))
        || (withinBounds(map, right) && !map.isWall(right))) {
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

  private static boolean withinBounds(Map map, Point point) {
    return withinBounds(map.getMaxX(), map.getMaxY(), point);
  }

  private static boolean withinBounds(int maxX, int maxY, Point point) {
    boolean x = point.getX() >= 0 && point.getX() < maxX;
    boolean y = point.getY() >= 0 && point.getY() < maxY;
    return x && y;
  }
}
