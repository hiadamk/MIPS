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
   * checks whether an entity is allowed to move in a certain direction
   *
   * @param d Direction to move in
   * @param e Entity to be checked
   * @param m Map the entity is moving on
   * @return true if the move is valid
   * @author Alex Banks, Matty Jones
   */
  public static boolean validiateDirection(Direction d, Entity e, Map m) {
    Point prevLoc =  Point.copyOf(e.getLocation());
    Direction prevDir = e.getDirection();
    boolean isValid = true;

    e.setDirection(d);
    e.move();
    Point faceLoc = e.getFaceLocation();
    double xpart = abs((prevLoc.getX() % 1) - 0.5);
    double ypart = abs((prevLoc.getY() % 1) - 0.5);
    if (xpart >= 0.1 || ypart >= 0.1) {
      isValid = false;
    }

    isValid = isValid && !m.isWall(faceLoc);

    e.setLocation(prevLoc);
    e.setDirection(prevDir);
    return isValid;
  }

  public static boolean validiateDirection(Direction d, Entity e, Point p, Map m) {
    boolean isValid = true;
    Point gridPoint = Mapping.getGridCoord(p);
    gridPoint.increaseX(0);
    gridPoint.increaseY(0);

    Point movedPoint = produceMovement(d,gridPoint,1);
    double xpart = abs((gridPoint.getX() % 1) - 0.5);
    double ypart = abs((gridPoint.getY() % 1) - 0.5);
    if (xpart >= 0.1 || ypart >= 0.1) {
      isValid = false;
    }

    isValid = isValid && !m.isWall(movedPoint);

    return isValid;
  }

  public static Point produceMovement(Direction direction, Point p, double offset) {
    Point loc = Point.copyOf(p);
    if (direction != null) {
      switch (direction) {
        case UP:
          loc.increaseY(-offset);
          break;
        case DOWN:
          loc.increaseY(offset);
          break;
        case LEFT:
          loc.increaseX(-offset);
          break;
        case RIGHT:
          loc.increaseX(offset);
          break;
      }
    }
    return loc;
  }

  public static boolean centreOfSquare(Entity e) {
    Point newLoc = new Point(e.getLocation().getX(), e.getLocation().getY());
    double xpart = mod(newLoc.getX(), 1);
    double ypart = mod(newLoc.getY(), 1);
    if( ypart >= 0.60 || ypart <= 0.40 || xpart >= 0.60 || xpart <= 0.40) return false;
    return true;
  }

  public static boolean centreOfSquare(Point p) {
    Point newLoc = new Point(p.getX(), p.getY());
    double xpart = mod(newLoc.getX(), 1);
    double ypart = mod(newLoc.getY(), 1);
    if( ypart >= 0.60 || ypart <= 0.40 || xpart >= 0.60 || xpart <= 0.40) return false;
    return true;
  }

  /**
   * true modulo instead of %
   *
   * @param dividend dividend
   * @param divisor divisor
   * @return dividend mod divisor
   * @author Alex Banks
   */
  public static double mod(double dividend, int divisor) {
    return (dividend + divisor) % divisor;
  }
}
