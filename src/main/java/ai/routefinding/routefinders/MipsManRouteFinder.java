package ai.routefinding.routefinders;

import ai.routefinding.RouteFinder;
import utils.Point;
import utils.enums.Direction;

/**
 * Route finding algorithm that controls Mipsman. Will aim to reach the nearest pellet whilst
 * avoiding any ghouls.
 *
 * @author Lewis Ackroyd
 */
public class MipsManRouteFinder implements RouteFinder {

  private static final boolean COMPLETE = false;

  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    if (!COMPLETE) {
      return new RandomRouteFinder().getRoute(myLocation, targetLocation);
    }

    return null;
  }
}
