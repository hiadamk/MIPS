package com.lordsofmidnight.utils.enums;

/**
 * More detailed enumeration for the different inputs possible.
 */
public enum Direction {
  UP(0) {
    @Override
    public String toString() {
      return "UP";
    }
  },
  DOWN(1) {
    @Override
    public String toString() {
      return "DOWN";
    }
  },
  LEFT(2) {
    @Override
    public String toString() {
      return "LEFT";
    }
  },
  RIGHT(3) {
    @Override
    public String toString() {
      return "RIGHT";
    }
  },
  USE(4) {
    @Override
    public String toString() {
      return "USE";
    }
  },
  STOP(5) {
    @Override
    public String toString() {
      return "STOP";
    }
  };

  /**All directions that can be used for movement.*/
  public static final Direction[] MOVEMENT_DIRECTIONS = {UP, DOWN, LEFT, RIGHT};

  private int id;

  /**
   * @param id the id of the direction to create
   */
  Direction(int id) {
    this.id = id;
  }

  /**
   * Returns a direction from an integer
   * @param n The directions id
   * @return the direction requested
   */
  public static Direction fromInt(int n) {
    switch (n) {
      case 0:
        return UP;
      case 1:
        return DOWN;
      case 2:
        return LEFT;
      case 3:
        return RIGHT;
      case 4:
        return USE;
      case 5:
        return STOP;
    }
    return null;
  }

  /**@return True if this direction is a movement direction
   * @see #MOVEMENT_DIRECTIONS
   * @author Lewis Ackroyd*/
  public boolean isMovementDirection() {
    switch (this) {
      case UP:
        return true;
      case DOWN:
        return true;
      case LEFT:
        return true;
      case RIGHT:
        return true;
      default:
        return false;
    }
  }

  /**
   * Gives the inverse of a movement direction
   * @return The inverse of the direction this method is called on
   */
  public Direction getInverse() {
    switch (this) {
      case UP:
        return DOWN;
      case DOWN:
        return UP;
      case LEFT:
        return RIGHT;
      case RIGHT:
        return LEFT;
      default:
        return this;
    }
  }

  /**
   *
   * @return The id of the direction
   */
  public int toInt() {
    return id;
  }
}
