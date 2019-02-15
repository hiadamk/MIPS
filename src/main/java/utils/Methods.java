package utils;

import java.awt.geom.Point2D;
import objects.Entity;
import utils.enums.Direction;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  public static boolean validiateDirection(Direction d, Entity e, Map m) {
    Point2D.Double newLoc = new Point2D.Double(e.getLocation().getX(), e.getLocation().getY());
    double xpart = mod(newLoc.getX(), 1);
    double ypart = mod(newLoc.getY(), 1);
    if( ypart >= 0.60 || ypart <= 0.40 || xpart >= 0.60 || xpart <= 0.40) return false;
    double offset = 0.8 + e.getVelocity();
    switch (d) {
      case RIGHT:
        newLoc.setLocation(newLoc.getX() + offset, newLoc.getY());
        break;
      case LEFT:
        newLoc.setLocation(newLoc.getX() - offset, newLoc.getY());
        break;
      case DOWN:
        newLoc.setLocation(newLoc.getX(), newLoc.getY() + offset);
        break;
      case UP:
        newLoc.setLocation(newLoc.getX(), newLoc.getY() - offset);
        break;
      default:
        return false;
    }
    return !m.isWall(newLoc);
  }

  public static boolean centreOfSquare(Entity e) {
    Point2D.Double newLoc = new Point2D.Double(e.getLocation().getX(), e.getLocation().getY());
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
