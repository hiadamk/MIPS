package audio;

public enum Sounds {
	// The filepaths of the sounds
    intro("/audio/pacman_beginning.wav"),
    chomp("/audio/pacman_chomp.wav"),
    death("/audio/pacman_death.wav"),
    click("/audio/click.wav");
	private final String path;
	Sounds(String path) {
		this.path=path;
	}
	
	public String getPath() {
		return path;
	}
}
