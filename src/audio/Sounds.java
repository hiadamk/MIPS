package audio;

import javax.sound.sampled.*;
import java.io.File;

/**
 * <p>A simple system for playing sounds in the clients currently just for events
 * easy to expand to handle the background music too.
 * Each client will create an instance of this class to control audio</p>
 */
public enum Sounds {
	// The filepaths of the sounds
	intro ("files/pacman_beginning.wav"),
	chomp ("files/pacman_chomp.wav"),
	death ("files/pacman_death.wav");
	private final String path;
	private Boolean mute;
	
	Sounds(String path){
		this.path = path;
		mute = false;
	}
	
	/**
	 * <p>This toggles the mute boolean on and off to be called
	 * by the ui when a mute button is pressed (can become a Setter</p>
	 */
	public void toggleMute(){
		mute = !mute;
	}
	
	/**
	 * <p>This plays the given sound</p>
	 * @param sound the sound to play
	 */
	public void playSound(Sounds sound) {
		if(mute) return; // IF the client has muted its audio nothing will be played
		File audioFile = new File(sound.path);
		try {
			AudioInputStream stream=AudioSystem.getAudioInputStream(audioFile);
			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
