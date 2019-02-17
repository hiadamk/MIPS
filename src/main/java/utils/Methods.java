package utils;

import objects.Entity;
import utils.enums.Direction;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
  }

  public static boolean validiateDirection(Direction d, Entity e, Map m) {
    Point newLoc = e.moveInDirection(0.6, d);
    //Point faceLoc = e.getFaceLocation();
    //double xpart = faceLoc.getX() % 1;
    //double ypart = faceLoc.getY() % 1;
    //if (ypart >= 0.60 || ypart <= 0.40 || xpart >= 0.60 || xpart <= 0.40) {
    //  return false;
    //}
    return !m.isWall(newLoc);
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
