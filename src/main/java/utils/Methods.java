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

    Point prevLoc = e.getLocation();
    Direction prevDir = e.getDirection();
    boolean isValid = true;

    e.setDirection(d);
    e.move();
    Point faceLoc = e.getFaceLocation();
    double xpart = prevLoc.getX() % 1;
    double ypart = prevLoc.getY() % 1;
    System.out.println(xpart + "," + ypart);
    if (ypart >= 0.6 || ypart <= 0.4 || xpart >= 0.4 || xpart <= 0.6) {
      isValid = false;
    }

    isValid = isValid && !m.isWall(faceLoc);

    if (isValid) {
      System.out.println("~~~" + e.getClientId() + " try " + d);
    } else {
      System.err.println("~~~" + e.getClientId() + " try " + d);
      System.err.println("~~~" + faceLoc + ", " + xpart + ", " + ypart);
    }

    e.setLocation(prevLoc);
    e.setDirection(prevDir);
    return isValid;
  }
}
