package ai.routefinding;

import utils.Point;
import utils.enums.Direction;

/**
 * Interface used to allow a {@link Direction} to be produced based on implemented conditions.
 *
 * @author Lewis Ackroyd
 */
public interface RouteFinder {
  
  /**
   * Returns the direction to travel in until the next junction is reached.
   *
   * @param myLocation     The start location for route finding.
   * @param targetLocation The target of the route finding.
   * @return The direction to travel in.
   */
  public Direction getRoute(Point myLocation, Point targetLocation);
}
