package com.lordsofmidnight.gamestate.maps;

class MapColour {

  public static int toTile(int rgb) {
    switch (rgb) {
      case -16777216:
        return 1;
      case -1:
        return 0;
      default:
        return -1;
    }
  }
}
