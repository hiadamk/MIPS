package com.lordsofmidnight.utils.enums;

public enum Direction {
  UP(0) {
    @Override
    public String toString() {
      return "up";
    }
  },
  DOWN(1) {
    @Override
    public String toString() {
      return "down";
    }
  },
  LEFT(2) {
    @Override
    public String toString() {
      return "left";
    }
  },
  RIGHT(3) {
    @Override
    public String toString() {
      return "right";
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

  private int id;

  Direction(int id) {
    this.id = id;
  }

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
        return null;
    }
  }

  public int toInt() {
    return id;
  }
}
