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
}
