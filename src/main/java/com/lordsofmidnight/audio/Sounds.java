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
  SPEED("audio/powerup.wav"),
  INVINCIBLE("audio/powerup.wav"); //TODO change
  private final String path;

  Sounds(String path) {
    this.path = path;
  }

  /**
   * @return The path to the audio file for the sound
   */
  public String getPath() {
    return path;
  }
}
