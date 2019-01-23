package audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>A simple system for playing sounds in the clients currently just for events
 * easy to expand to handle the background music too.
 * Each client will create an instance of this class to control audio</p>
 */
public class AudioController {

	private Boolean mute;
	private Clip music;
	private ArrayList<Clip> openClips;
	private double musicVolume;
	private double soundVolume;
	private ClipCloser clipCloser;
	
	public AudioController() {
		mute=false;
		music=null;
		musicVolume=0.5;
		soundVolume=0.5;
		openClips=new ArrayList<>();
		clipCloser=new ClipCloser(openClips);
		clipCloser.start();
	}
	
	public void setMusicVolume(double musicVolume) {
		this.musicVolume=musicVolume;
		refreshMusic();
	}
	
	public void setSoundVolume(double soundVolume) {
		this.soundVolume=soundVolume;
	}
	
	/**
	 * <p>This toggles the mute boolean on and off to be called
	 * by the ui when a mute button is pressed (can become a Setter</p>
	 */
	public void toggleMute() {
		mute=!mute;
		refreshMusic();
	}
	
	private void refreshMusic() {
		if (music==null) return;
		setClipVolume(music, mute ? 0 : musicVolume);
	}
	
	/**
	 * <p>This plays the given sound</p>
	 * @param sound the sound to play
	 */
	public void playSound(Sounds sound) {
		if (mute) return; // IF the client has muted its audio nothing will be played
		InputStream audio = AudioController.class.getResourceAsStream(sound.getPath());
		File audioFile=new File(sound.getPath());
		try {
			AudioInputStream stream=AudioSystem.getAudioInputStream(audio);
			DataLine.Info info=new DataLine.Info(Clip.class, stream.getFormat());
			Clip clip=(Clip) AudioSystem.getLine(info);
			clip.open(stream);
			setClipVolume(clip, soundVolume);
			clip.start();
			openClips.add(clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Sets the volume of the given clip</p>
	 * @param clip   The clip to set the volume of
	 * @param volume the volume (between 0.0 and 1.0)
	 */
	public void setClipVolume(Clip clip, double volume) {
		if (clip==null) {
			// Change to logging once setup
			System.err.println(" null clip sent to set volume");
			return;
		}
		if (volume>1.0) volume=1.0;
		if (volume<0.0) volume=0.0;
		FloatControl control=(FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue((float) (Math.log(volume)/Math.log(10.0)*20.0));
	}
	
	/**
	 * <p>This plays the given sound as the background music (playing it until told to stop or play other music)</p>
	 * @param sound file for the music
	 */
	public void playMusic(Sounds sound) {
		InputStream audio = AudioController.class.getResourceAsStream(sound.getPath());
//		File audioFile=new File(sound.getPath());
		try {
			AudioInputStream stream=AudioSystem.getAudioInputStream(audio);
			DataLine.Info info=new DataLine.Info(Clip.class, stream.getFormat());
			Clip clip=(Clip) AudioSystem.getLine(info);
			clip.open(stream);
			if (music!=null) {
				music.stop();
				music.close();
			}
			music=clip;
			setClipVolume(music, musicVolume);
			music.loop(Clip.LOOP_CONTINUOUSLY);
			openClips.add(music);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>A thread to close audio clips after they have been played</p>
	 */
	private class ClipCloser extends Thread {
		private ArrayList<Clip> clips;
		private Boolean running;
		
		ClipCloser(ArrayList<Clip> clips) {
			this.clips=clips;
		}
		
		@Override
		public void run() {
			running=true;
			Collection<Clip> remove=new ArrayList<>();
			while (running) { // closes any audio clips that have finished
				remove.clear();
				for (Clip clip : clips) {
					if (!clip.isRunning()) {
						clip.close();
						remove.add(clip);
					}
				}
				clips.removeAll(remove);
				try {
					Thread.sleep(10000);
				} catch (Exception e) {
					running=false;
				}
			}
		}
	}
}
