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

  AudioClip[] clips;
  private int client;
  private MediaPlayer mediaPlayer;
  private MediaPlayer menuPlayer;
  private MediaPlayer gamePlayer;

  /**
   * @param clientId The ID of the client, this is used to know what sounds to play
   */
  public AudioController(int clientId) {
    mediaPlayer = null;
    client = clientId;
    clips = loadClips();
  }

  /**
   * Stops all music players
   */
  private void stopPlayers() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
    }
    if (menuPlayer != null) {
      menuPlayer.stop();
    }
    if (gamePlayer != null) {
      gamePlayer.stop();
    }
  }
  /**
   * Sets the music volume
   *
   * @param musicVolume The volume to which we want to set it to
   */
  public void setMusicVolume(double musicVolume) {
    Settings.setMusicVolume(musicVolume);
    if (mediaPlayer != null) {
      mediaPlayer.setVolume(musicVolume);
    }
    if (menuPlayer != null) {
      menuPlayer.setVolume(musicVolume);
    }
    if (gamePlayer != null) {
      gamePlayer.setVolume(musicVolume);
    }
  }

  /**
   * Loads all the clips in from the files
   *
   * @return The array of all Clips one for each sound in the Sounds enum
   */
  private AudioClip[] loadClips() {
    AudioClip[] loaded = new AudioClip[Sounds.values().length];
    try {
      for (Sounds sound : Sounds.values()) {
        loaded[sound.id()] =
            new AudioClip(getClass().getResource(sound.getPath()).toURI().toString());
      }
      gamePlayer =
          new MediaPlayer(
              new Media(getClass().getResource(Sounds.GAMELOOP.getPath()).toURI().toString()));
      menuPlayer =
          new MediaPlayer(
              new Media(getClass().getResource(Sounds.MENULOOP.getPath()).toURI().toString()));
    } catch (URISyntaxException e) {
      e.printStackTrace();
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
      // AudioClip newclip = new
      // AudioClip(getClass().getResource(sound.getPath()).toURI().toString());
      AudioClip newclip = clips[sound.id()];
      newclip.setVolume(Settings.getSoundVolume());
      newclip.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Plays the game music intro the after the looping music */
  public void gameIntro() {
    stopPlayers();
    playMusic(Sounds.GAMEINTRO);
    new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(8000);
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
      stopPlayers();
      MediaPlayer current;
      if (sound == Sounds.MENULOOP) {
        current = menuPlayer;
      } else if (sound == Sounds.GAMELOOP) {
        current = gamePlayer;
      } else {
        mediaPlayer =
            new MediaPlayer(new Media(getClass().getResource(sound.getPath()).toURI().toString()));
        current = mediaPlayer;
      }
      current.setVolume(Settings.getMusicVolume());
      current.seek(Duration.ZERO);
      current.setOnEndOfMedia(
          new Runnable() {
            @Override
            public void run() {
              playMusic(sound);
            }
          });
      current.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
