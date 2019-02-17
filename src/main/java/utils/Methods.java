package utils;

import static java.lang.Math.abs;

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
    Point prevLoc = e.getLocation();
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
}
