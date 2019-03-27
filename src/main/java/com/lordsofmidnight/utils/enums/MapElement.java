package com.lordsofmidnight.utils.enums;

/**
 * Enumeration for the different map elements
 */
public enum MapElement {
  /**
   * Representing a tile that players can occupy
   */
  FLOOR(0, -1) {
    @Override
    public String toString() {
      return "floor";
    }
  },
  /**
   * A tile that players can not move through
   */
  WALL(1, -16777216) {
    public String toString() {
      return "wall";
    }
  };


  private int id;

  private int colour;

  /**
   * @param id the id of the element
   * @param colour the colour for representation in the map image
   */
  MapElement(int id, int colour) {
    this.id = id;
    this.colour = colour;
  }

  /**
   * Converts between a colour id and a MapElement id
   * @param colour the colour
   * @return the corresponding id
   */
  public static int colourToID(int colour) {
    for (MapElement m : MapElement.values()) {
      if (colour == m.toColour()) {
        return m.id;
      }
    }
    return 0;
  }

  /**
   * Converts between a MapElement id and a colour id
   * @param id the id
   * @return the corresponding colour
   */
  public static int idToColour(int id) {
    for (MapElement m : MapElement.values()) {
      if (id == m.toInt()) {
        return m.colour;
      }
    }
    return 0;
  }

  /**
   * Converts the element to an int
   * @return the id of the element
   */
  public int toInt() {
    return id;
  }

  /**
   * Converts the element to a colour number
   * @return the colour
   */
  public int toColour() {
    return colour;
  }
}
