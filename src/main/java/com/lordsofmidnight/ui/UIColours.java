package com.lordsofmidnight.ui;

public enum UIColours {
  GREEN("#199d6e"), QUIT_RED("#ff0202"), RED("#ff436d"),
  WHITE("#ffffff"), YELLOW("#ffff8d"), BLACK("#000000");

  private String hex;

  UIColours(String hexvalue) {
    this.hex = hexvalue;
  }

  public String getHex() {
    return this.hex;
  }
}
