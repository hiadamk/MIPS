package com.lordsofmidnight.audio;

public enum Sounds {
  // The filepaths of the sounds
  intro("/com/lordsofmidnight/audio/pacman_beginning.wav"),
  chomp("/com/lordsofmidnight/audio/pacman_chomp.wav"),
  death("/com/lordsofmidnight/audio/pacman_death.wav"),
  click("/com/lordsofmidnight/audio/click.wav");
  private final String path;

  Sounds(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
