package utils;

import static java.lang.Math.abs;

import ai.mapping.Mapping;
import objects.Entity;
import utils.enums.Direction;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  /**
   * @deprecated
   * @see #validateDirection(Direction, Point, Map)
   */
  public static boolean validiateDirection(Direction d, Entity e, Map m) {
    Point prevLoc = e.getLocation().getCopy();
    double xpart = abs((prevLoc.getX() % 1) - 0.5);
    double ypart = abs((prevLoc.getY() % 1) - 0.5);
    Point nextLoc = e.getMoveInDirection(1, d);
    return !(xpart >= 0.1 || ypart >= 0.1 || m.isWall(nextLoc));
  }

  /**
   * @see #validateDirection(Direction, Point, Map)
   * @deprecated
   */
  public static boolean validiateDirection(Direction d, Entity e, Point p, Map m) {
    boolean isValid = true;
    Point gridPoint = Mapping.getGridCoord(p).centralise();

    Point movedPoint = gridPoint.getCopy().moveInDirection(1, d);

    double xpart = abs((gridPoint.getX() % 1) - 0.5);
    double ypart = abs((gridPoint.getY() % 1) - 0.5);
    if (xpart >= 0.1 || ypart >= 0.1) {
      isValid = false;
    }

    isValid = isValid && !m.isWall(movedPoint);

    return isValid;
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
   * @deprecated
   * @see Point#moveInDirection(double, Direction)
   */
  public static Point produceMovement(Direction direction, Point p, double offset) {
    return null;
  }

  /**
   * @see Point#isCentered()
   * @deprecated
   */
  public static boolean centreOfSquare(Entity e) {
    return centreOfSquare(e.getLocation());
  }

  /**
   * @see Point#isCentered()
   * @deprecated
   */
  public static boolean centreOfSquare(Point p) {
    Point newLoc = new Point(p.getX(), p.getY());
    double xpart = mod(newLoc.getX(), 1);
    double ypart = mod(newLoc.getY(), 1);
    if (ypart >= 0.60 || ypart <= 0.40 || xpart >= 0.60 || xpart <= 0.40) return false;
    return true;
  }

  /**
   * true modulo instead of %
   *
   * @deprecated only used in depreciated methods so can be yeet
   * @param dividend dividend
   * @param divisor divisor
   * @return dividend mod divisor
   * @author Alex Banks
   */
  public static double mod(double dividend, int divisor) {
    return (dividend + divisor) % divisor;
  }
}
