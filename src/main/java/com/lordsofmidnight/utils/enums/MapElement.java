package com.lordsofmidnight.utils.enums;

public enum MapElement {
  FLOOR(0, -1) {
    @Override
    public String toString() {
      return "floor";
    }
  },

  WALL(1, -16777216) {
    public String toString() {
      return "wall";
    }
  },

  SPAWNPOINT(1, -16777216) {
    public String toString() {
      return "spawnpoint";
    }
  };

  private int id;

  private int colour;

  MapElement(int id, int colour) {
    this.id = id;
    this.colour = colour;
  }

  public static int colourToID(int colour) {
    for (MapElement m : MapElement.values()) {
      if (colour == m.toColour()) {
        return m.id;
      }
    }
    return 0;
  }

  public static int idToColour(int id) {
    for (MapElement m : MapElement.values()) {
      if (id == m.toInt()) {
        return m.colour;
      }
    }
    return 0;
  }

  public int toInt() {
    return id;
  }

  public int toColour() {
    return colour;
  }
}
