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
    Point2D.Double newLoc = new Point2D.Double(e.getLocation().getX(),
        e.getLocation().getY());
    double offset = 0.5 + e.getVelocity();
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
    }
    return !m.isWall(newLoc);
  }
}
