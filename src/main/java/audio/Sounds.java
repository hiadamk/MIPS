package audio;

public enum Sounds {
	// The filepaths of the sounds
	intro("/audioFiles/pacman_beginning.wav"),
	chomp("/audioFiles/pacman_chomp.wav"),
	death("/audioFiles/pacman_death.wav"),
	click("/audioFiles/click.wav");
	private final String path;
	Sounds(String path) {
		this.path=path;
	}
	
	public String getPath() {
		return path;
	}
}
