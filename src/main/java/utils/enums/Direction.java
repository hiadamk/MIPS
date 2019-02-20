package utils.enums;

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
  };
  private int id;
  
  Direction(int id) {
    this.id = id;
  }
  
  public int toInt() {
    return id;
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
    }
    return null;
  }
}

