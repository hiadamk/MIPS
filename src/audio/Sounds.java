package audio;

public enum Sounds {
	// The filepaths of the sounds
	intro("files/pacman_beginning.wav"),
	chomp("files/pacman_chomp.wav"),
	death("files/pacman_death.wav");
	private final String path;
	Sounds(String path) {
		this.path=path;
	}
	
	public String getPath() {
		return path;
	}
}
