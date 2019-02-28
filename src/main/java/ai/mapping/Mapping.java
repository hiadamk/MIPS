package ai.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import utils.Map;
import utils.Point;
import utils.enums.Direction;

/**
 * Methods to facilitate the abstraction of the game {@link Map}, allowing it to be represented as a
 * graph and also to perform move validation.
 *
 * @author Lewis Ackroyd
 */
public abstract class Mapping {

  /**
   * Calculates the location of all junctions within the map. A value of 0 is classified as "Path".
   *
   * @param map The map having junctions identified on.
   * @return A {@link HashSet}&lt;{@link Point Point}&gt; containing all points of junctions.
   */
  public static HashSet<utils.Point> getjunctions(Map map) {

    HashSet<Point> junctions = new HashSet<Point>();
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

    public static JunctionSet getJunctions(Map map) {

        JunctionSet junctions = new JunctionSet();
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
  public static HashMap<Point, HashSet<Point>> getEdges(Map map) throws IllegalArgumentException {
    return getEdges(map, getJunctions(map));
  }

  /**
   * Produces a map from all the junctions to all other junctions that are in a direct line to them
   * along path with no obstructions (including walls and other junctions)
   *
   * @param map The map to produce the junction pairs from.
   * @param junctions All the junctions within the map. These can be generated using the {@link #
   * getJunctions(Map) getJunctions} method.
   * @return A mapping of every junction to all connected junctions.
   */
  public static HashMap<Point, HashSet<Point>> getEdges(Map map, HashSet<Point> junctions) {
    HashMap<Point, HashSet<Point>> edgeMap = new HashMap<Point, HashSet<Point>>();
    // generates links for every junction
    for (Point p : junctions) {
      HashSet<Point> edgeSet = new HashSet<Point>();
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

  public static HashMap<Point, HashSet<Point>> getEdges(Map map, JunctionSet junctions) {
        HashMap<Point, HashSet<Point>> edgeMap = new HashMap<Point, HashSet<Point>>();
        // generates links for every junction
        Iterator<Point> iterator = junctions.iterator();
        while (iterator.hasNext()) {
            Point p = iterator.next();
            HashSet<Point> edgeSet = new HashSet<Point>();
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

  public static Point findNearestJunction(Point position, Map map, JunctionSet junctions) {
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

  private static double costVertical(Point position, Map map, JunctionSet junctions) {
    Point up = new Point(position.getX(), position.getY());
    Point down = new Point(position.getX(), position.getY());
    boolean upWall = false;
    boolean downWall = false;
    while ((withinBounds(map.getMaxX(), map.getMaxY(), up) && !map.isWall(up))
        || (withinBounds(map.getMaxX(), map.getMaxY(), down) && !map.isWall(down))) {
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

  private static double costHorizontal(Point position, Map map, JunctionSet junctions) {
    Point left = new Point(position.getX(), position.getY());
    Point right = new Point(position.getX(), position.getY());
    boolean leftWall = false;
    boolean rightWall = false;
    while ((withinBounds(map.getMaxX(), map.getMaxY(), left) && !map.isWall(left))
        || (withinBounds(map.getMaxX(), map.getMaxY(), right) && !map.isWall(right))) {
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

  private static boolean withinBounds(int maxX, int maxY, Point point) {
    boolean x = point.getX() >= 0 && point.getX() < maxX;
    boolean y = point.getY() >= 0 && point.getY() < maxY;
    return x && y;
  }
}
