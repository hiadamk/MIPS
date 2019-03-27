package com.lordsofmidnight.audio;

import com.lordsofmidnight.utils.Settings;
import java.net.URISyntaxException;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * A simple system for playing sounds in the clients currently just for events easy to expand to
 * handle the background music too. Each com.lordsofmidnight.main will create an instance of this
 * class to control audio
 */
public class AudioController {
  private int client;
  private MediaPlayer mediaPlayer;
  AudioClip[] clips;
  /**
   * @param clientId The ID of the client, this is used to know what sounds to play
   */
  public AudioController(int clientId) {
    mediaPlayer = null;
    client = clientId;
    clips = loadClips();
  }

  /**
   * Sets the music volume
   *
   * @param musicVolume The volume to which we want to set it to
   */
  public void setMusicVolume(double musicVolume) {
    Settings.setMusicVolume(musicVolume);
    mediaPlayer.setVolume(Settings.getMusicVolume());
  }

  private AudioClip[] loadClips() {
    AudioClip[] loaded = new AudioClip[Sounds.values().length];
    for (Sounds sound : Sounds.values()) {
      try {
        loaded[sound.id()] = new AudioClip(
            getClass().getResource(sound.getPath()).toURI().toString());
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    return loaded;
  }
  /**
   * This plays the given sound
   *
   * @param sound the sound to play
   */
  public void playSound(Sounds sound, int... id) {
    if (Settings.getMute() || (id.length > 0 && id[0] != client)) {
      return; // IF the com.lordsofmidnight.main has muted its audio nothing will be played
    }
    try {
      //AudioClip newclip = new AudioClip(getClass().getResource(sound.getPath()).toURI().toString());
      AudioClip newclip = clips[sound.id()];
      newclip.setVolume(Settings.getSoundVolume());
      newclip.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Plays the game music intro the after the looping music
   */
  public void gameIntro() {
    mediaPlayer.stop();
    playMusic(Sounds.GAMEINTRO);
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(9000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        playMusic(Sounds.GAMELOOP);
      }
    }.start();
  }
  /**
   * This plays the given sound as the background music (playing it until told to stop or play other
   * music)
   *
   * @param sound file for the music
   */
  public void playMusic(Sounds sound) {
    try {
      if (mediaPlayer != null) {
        mediaPlayer.stop();
      }
      mediaPlayer = new MediaPlayer(
          new Media(getClass().getResource(sound.getPath()).toURI().toString()));
      mediaPlayer.setVolume(Settings.getMusicVolume());
      mediaPlayer.seek(Duration.ZERO);
      mediaPlayer.setOnEndOfMedia(new Runnable() {
        @Override
        public void run() {
          playMusic(sound);
        }
      });
      mediaPlayer.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
