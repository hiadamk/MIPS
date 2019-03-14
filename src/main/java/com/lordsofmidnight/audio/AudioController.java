package com.lordsofmidnight.audio;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

/**
 * A simple system for playing sounds in the clients currently just for events easy to expand to
 * handle the background music too. Each com.lordsofmidnight.main will create an instance of this
 * class to control audio
 */
public class AudioController {

  private Boolean mute;
  private Clip music;
  private ArrayList<Clip> openClips;
  private double musicVolume;
  private double soundVolume;
  private ClipCloser clipCloser;

  public AudioController() {
    mute = false;
    music = null;
    musicVolume = 0.5;
    soundVolume = 0.5;
    openClips = new ArrayList<>();
    clipCloser = new ClipCloser(openClips);
    clipCloser.start();
  }

  /**
   * Gets the current music volume.
   *
   * @return the current music volume.
   */
  public double getMusicVolume() {
    return musicVolume;
  }

  /**
   * Sets the music volume
   *
   * @param musicVolume The volume to which we want to set it to
   */
  public void setMusicVolume(double musicVolume) {
    this.musicVolume = musicVolume;
    refreshMusic();
  }

  /**
   * Gets the current volume of sound effects
   *
   * @return The current volume which sound effects are played at
   */
  public double getSoundVolume() {
    return soundVolume;
  }

  /**
   * Sets the sound effects volume.
   *
   * @param soundVolume The volume we want to set the sound effects volume to.
   */
  public void setSoundVolume(double soundVolume) {
    this.soundVolume = soundVolume;
  }

  /**
   * This toggles the mute boolean on and off to be called by the com.lordsofmidnight.ui when a mute
   * button is pressed (can become a Setter
   */
  public void toggleMute() {
    mute = !mute;
    refreshMusic();
  }

  /**
   * Increases master game volume.
   */
  public void increaseVolume() {
    if (getMusicVolume() < 1) {
      setMusicVolume(getMusicVolume() + 0.1);
    }

    if (getSoundVolume() < 1) {
      setSoundVolume(getSoundVolume() + 0.1);
    }
  }

  /**
   * Decreases master game volume.
   */
  public void decreaseVolume() {
    if (getMusicVolume() >= 0.1) {
      setMusicVolume(getMusicVolume() - 0.1);
    }

    if (getSoundVolume() >= 0.1) {
      setSoundVolume(getSoundVolume() - 0.1);
    }
  }

  private void refreshMusic() {
    if (music == null) {
      return;
    }
    setClipVolume(music, mute ? 0 : musicVolume);
  }

  /**
   * This plays the given sound
   *
   * @param sound the sound to play
   */
  public void playSound(Sounds sound) {
    if (mute) {
      return; // IF the com.lordsofmidnight.main has muted its audio nothing will be played
    }
    InputStream audio = AudioController.class.getResourceAsStream(sound.getPath());
    File audioFile = new File(sound.getPath());
    try {
      AudioInputStream stream = AudioSystem.getAudioInputStream(audio);
      DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
      Clip clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      setClipVolume(clip, soundVolume);
      clip.start();
      openClips.add(clip);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the volume of the given clip
   *
   * @param clip The clip to set the volume of
   * @param volume the volume (between 0.0 and 1.0)
   */
  public void setClipVolume(Clip clip, double volume) {
    if (clip == null) {
      // Change to logging once setup
      System.err.println(" null clip sent to set volume");
      return;
    }
    if (volume > 1.0) {
      volume = 1.0;
    }
    if (volume < 0.1) {
      volume = 0.0;
    }
    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    control.setValue((float) (Math.log(volume) / Math.log(10.0) * 20.0));
  }

  /**
   * This plays the given sound as the background music (playing it until told to stop or play other
   * music)
   *
   * @param sound file for the music
   */
  public void playMusic(Sounds sound) {
    InputStream audio = AudioController.class.getResourceAsStream(sound.getPath());
    //		File audioFile=new File(sound.getPath());
    try {
      AudioInputStream stream = AudioSystem.getAudioInputStream(audio);
      DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat());
      Clip clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      if (music != null) {
        music.stop();
        music.close();
      }
      music = clip;
      setClipVolume(music, musicVolume);
      music.loop(Clip.LOOP_CONTINUOUSLY);
      openClips.add(music);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * A thread to close audio clips after they have been played
   */
  private class ClipCloser extends Thread {

    private ArrayList<Clip> clips;
    private Boolean running;

    ClipCloser(ArrayList<Clip> clips) {
      this.clips = clips;
    }

    @Override
    public void run() {
      running = true;
      Collection<Clip> remove = new ArrayList<>();
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
          running = false;
        }
      }
    }
  }
}
