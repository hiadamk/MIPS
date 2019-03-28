package com.lordsofmidnight.audio;

/**
 * Enum to represent the different sound files for soundFX and music
 */
public enum Sounds {
  // The filepaths of the sounds
  GAMEINTRO("/audio/game_intro.wav", 0),
  GAMELOOP("/audio/game_loop.wav", 1),
  MENULOOP("/audio/menu_loop.wav", 2),
  CLICK("/audio/click.wav", 3),
  EXPLODE("/audio/boomboom.wav", 4),
  COIN("/audio/coin.wav", 5),
  TRAPPED("/audio/trapped.wav", 6),
  DEAD("/audio/dead.wav", 7),
  MIPS("/audio/mips.wav", 8),
  ROCKET("/audio/rocket.wav", 9),
  POWERUP("/audio/powerup.wav", 10),
  ROCKETLAUNCH("/audio/rocketlaunch.wav", 11),
  SPEED("/audio/zoom.wav", 12),
  INVINCIBLE("/audio/invin.wav", 13);
  private final String path;
  private int id;

  /**
   * @param path The path to the file
   * @param id The ID of the sound
   */
  Sounds(String path, int id) {
    this.path = path;
    this.id = id;
  }

  /**
   * @return The path to the audio file for the sound
   */
  public String getPath() {
    return path;
  }

  /**
   *
   * @return The ID of the sound
   */
  public int id() {
    return id;
  }
}
