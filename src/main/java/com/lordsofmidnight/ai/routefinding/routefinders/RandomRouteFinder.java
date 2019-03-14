package com.lordsofmidnight.ai.routefinding.routefinders;

import com.lordsofmidnight.ai.routefinding.RouteFinder;
import java.util.Random;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Random direction generator. Twice as likely to produce a route towards the target as one away
 * from it.
 *
 * @author Lewis Ackroyd
 */
public class RandomRouteFinder implements RouteFinder {

  private static final Random R = new Random();

  /**
   * Creates an instance of this routeFinder.
   */
  public RandomRouteFinder() {
  }

  /**
   * Returns the direction to travel in until the next junction is reached.
   *
   * @param myLocation The start of com.lordsofmidnight.gamestate for route finding.
   * @param targetLocation The target com.lordsofmidnight.gamestate for route finding.
   * @return The direction to travel in.
   * @throws NullPointerException One or both of positions are null.
   */
  @Override
  public Direction getRoute(Point myLocation, Point targetLocation) {
    if (myLocation == null || targetLocation == null) {
      throw new NullPointerException("One or both positions are null.");
    }
    Direction dir = DEFAULT;
    int dirValue = R.nextInt(6);
    switch (dirValue) {
      case 0: {
        dir = Direction.UP;
        break;
      }
      case 1: {
        dir = Direction.DOWN;
        break;
      }
      case 2: {
        dir = Direction.LEFT;
        break;
      }
      case 3: {
        dir = Direction.RIGHT;
        break;
      }
      case 4: {
        // makes ghost twice as likely to move towards pacman as away from them
        if (myLocation.getY() > targetLocation.getY()) {
          dir = Direction.UP;
        } else {
          dir = Direction.DOWN;
        }
        break;
      }
      case 5: {
        if (myLocation.getX() > targetLocation.getX()) {
          dir = Direction.LEFT;
        } else {
          dir = Direction.RIGHT;
        }
        break;
      }
    }
    return dir;
  }
}
