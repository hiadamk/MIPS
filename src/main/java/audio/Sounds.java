package audio;

public enum Sounds {
  // The filepaths of the sounds
  intro("src/main/resources/audio/pacman_beginning.wav"),
  chomp("src/main/resources/audio/pacman_chomp.wav"),
  death("src/main/resources/audio/pacman_death.wav");
  private final String path;

  Sounds(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
