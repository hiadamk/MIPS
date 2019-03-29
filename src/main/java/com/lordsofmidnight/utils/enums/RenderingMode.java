package com.lordsofmidnight.utils.enums;

/**
 * Enum to represent the different rendering modes
 */
public enum RenderingMode {
  NO_SCALING("no_scaling"),
  INTEGER_SCALING("integer_scaling"),
  SMOOTH_SCALING("smooth_scaling"),
  STANDARD_SCALING("standard_scaling");

  private String name;

  /**
   * @param name The name of the rendering mode
   */
  RenderingMode(String name) {
    this.name = name;
  }

  /**
   * @param s The string to create it from
   * @return The rendering mode
   */
  public static RenderingMode fromString(String s) {
    for (RenderingMode r : RenderingMode.values()) {
      if (r.getName().equals(s)) {
        return r;
      }
    }
    return null;
  }

  /**
   * @return The name
   */
  public String getName() {
    return name;
  }
}
