package utils.enums;

public enum Awards {
  KILLS("Bloodthirsty"),
  DEATHS("Punching Bag"),
  POINTS_GAINED("Money Maker"),
  POINTS_STOLEN("Aspiring Thief"),
  POINTS_LOST("Jinxed"),
  ITEMS_USED("Powerup Specialist");

  String name;

  Awards(String name) {
    this.name = name;
  }
}
