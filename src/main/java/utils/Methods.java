package utils;

import objects.Entity;
import utils.enums.Direction;

public class Methods {

  public static void updateImages(Entity[] entities, ResourceLoader r) {
    for (Entity e : entities) {
      e.updateImages(r);
    }
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

    Point gridPoint = p.getGridCoord().centralise();
    Point movedPoint = gridPoint.getCopy().moveInDirection(1, d);
    return !m.isWall(movedPoint) && p.isCentered();
  }

}
