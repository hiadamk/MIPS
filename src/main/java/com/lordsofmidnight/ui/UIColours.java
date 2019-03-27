package com.lordsofmidnight.ui;

/**
 * Enumneration which stores the colours used for menu components
 */
public enum UIColours {
  GREEN("#199d6e"), QUIT_RED("#ff0202"), RED("#ff436d"),
  WHITE("#ffffff"), YELLOW("#ffff8d"), BLACK("#000000");

  private String hex;

  UIColours(String hexvalue) {
    this.hex = hexvalue;
  }

  /**
   * Gets the hex value of the colour
   *
   * @return The String of the hex
   */
  public String getHex() {
    return this.hex;
  }
}
