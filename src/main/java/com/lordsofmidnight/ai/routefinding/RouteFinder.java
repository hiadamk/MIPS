package com.lordsofmidnight.ai.routefinding;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Interface used to allow a {@link Direction} to be produced based on implemented conditions.
 *
 * @author Lewis Ackroyd
 */
public interface RouteFinder {

  Direction DEFAULT = Direction.STOP;
  /**
   * Returns the direction to travel in until the next junction is reached.
   *
   * @param myLocation The start position for route finding.
   * @param targetLocation The target position for route finding.
   * @return The direction to travel in.
   * @author Lewis Ackroyd
   */
  public Direction getRoute(Point myLocation, Point targetLocation);
}
