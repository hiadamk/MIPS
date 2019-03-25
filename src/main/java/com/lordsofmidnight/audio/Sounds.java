package com.lordsofmidnight.audio;

public enum Sounds {
  // The filepaths of the sounds
  GAMEINTRO("/audio/game_intro.wav"),
  GAMELOOP("/audio/game_loop.wav"),
  MENULOOP("/audio/menu_loop.wav"),
  CLICK("/audio/click.wav"),
  EXPLODE("/audio/boomboom.wav"),
  COIN("/audio/coin.wav"),
  TRAPPED("/audio/traooed.wav"),
  DEAD("/audio/dead.wav"),
  MIPS("/audio/mips.wav"),
  ROCKET("/audio/rocket.wav"),
  POWERUP("audio/powerup.wav"),
  ROCKETLAUNCH("audio/rocketlaunch.wav"),
  private final String path;

  Sounds(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
