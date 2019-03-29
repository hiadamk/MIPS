package com.lordsofmidnight.utils;

import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.utils.enums.InputKey;
import com.lordsofmidnight.utils.enums.RenderingMode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javafx.scene.input.KeyCode;

/**
 * Class which stores the settings of the client.
 */
public class Settings {

  private static final String settingsDirectory = "src/main/resources/settings.cfg";

  private static KeyCode up = KeyCode.UP;
  private static KeyCode down = KeyCode.DOWN;
  private static KeyCode left = KeyCode.LEFT;
  private static KeyCode right = KeyCode.RIGHT;
  private static KeyCode useItem = KeyCode.SPACE;
  private static int xResolution = 1366;
  private static int yResolution = 768;
  private static RenderingMode renderingMode = RenderingMode.SMOOTH_SCALING;
  private static String theme = "default";
  private static String name = "null";

  private static Boolean mute = false;
  private static double musicVolume = 0.5;
  private static double soundVolume = 0.5;

  /**
   * Gets whether or not the audio has been muted
   *
   * @return True is muted, false otherwise.
   */
  public static Boolean getMute() {
    return mute;
  }

  /**
   * Sets the value of mute.
   *
   * @param mute True is the audio is muted, false otherwise
   */
  public static void setMute(Boolean mute) {
    Settings.mute = mute;
  }

  /**
   * Gets the current volume of music.
   *
   * @return The current volume of music in the game
   */
  public static double getMusicVolume() {
    return musicVolume;
  }

  /**
   * Sets the volume of music in the game
   *
   * @param musicVolume The volume to set current volume to.
   */
  public static void setMusicVolume(double musicVolume) {
    Settings.musicVolume = musicVolume;
  }

  /**
   * Gets the volume of sound effects
   *
   * @return The current volume of sound effects
   */
  public static double getSoundVolume() {
    return soundVolume;
  }

  /**
   * Sets the volume of sound effects
   *
   * @param soundVolume The volume which we want to set the sound effects volume to.
   */
  public static void setSoundVolume(double soundVolume) {
    Settings.soundVolume = soundVolume;
  }

  /**
   * Gets the name of the client
   *
   * @return Client's name
   */
  public static String getName() {
    return name;
  }

  /**
   * Sets the name of the client.
   *
   * @param name The client's name
   */
  public static void setName(String name) {
    Settings.name = name;
    saveSettings();
  }

  /**
   * Restores the game settings to system default
   *
   * @param c the current client.
   */
  public static void restoreDefaultSettings(Client c) {
    up = KeyCode.UP;
    down = KeyCode.DOWN;
    left = KeyCode.LEFT;
    right = KeyCode.RIGHT;
    useItem = KeyCode.SPACE;
    xResolution = 1366;
    yResolution = 768;
    renderingMode = RenderingMode.SMOOTH_SCALING;
    theme = "default";
    name = "null";

    mute = false;
    musicVolume = 0.5;
    soundVolume = 0.5;

    c.updateResolution();
    saveSettings();
  }

  /**
   * Gets the current Input Key key code
   *
   * @param key the key we want to query
   * @return The keycode we want
   */
  public static KeyCode getKey(InputKey key) {
    switch (key) {
      case UP:
        return up;
      case DOWN:
        return down;
      case LEFT:
        return left;
      case RIGHT:
        return right;
      default:
        return useItem;
    }
  }

  /**
   * Sets a key
   *
   * @param key The input type
   * @param keyCode The keycode of the input.
   */
  public static void setKey(InputKey key, KeyCode keyCode) {
    switch (key) {
      case UP:
        up = keyCode;
        break;
      case DOWN:
        down = keyCode;
        break;
      case LEFT:
        left = keyCode;
        break;
      case RIGHT:
        right = keyCode;
        break;
      default:
        useItem = keyCode;
    }
    System.err.println("Invalid key to be bound");
  }

  /**
   * Gets the horizontal resolution of the screen.
   *
   * @return The number of pixels across
   */
  public static int getxResolution() {
    return xResolution;
  }

  /**
   * Sets the horizontal resolution of the game
   *
   * @param xResolution The number of pixels axross
   */
  public static void setxResolution(int xResolution) {
    Settings.xResolution = xResolution;
  }

  /**
   * Gets the vertical resolution of the screen
   *
   * @return the number of vertical pixels there are
   */
  public static int getyResolution() {
    return yResolution;
  }

  /**
   * Sets the number of vertical pixels in the game
   *
   * @param yResolution The number of vertical pixels
   */
  public static void setyResolution(int yResolution) {
    Settings.yResolution = yResolution;
  }

  /**
   * Gets the current rendering mode being used
   *
   * @return The rendering mode being used
   */
  public static RenderingMode getRenderingMode() {
    return renderingMode;
  }

  /**
   * Sets the rendering mode
   *
   * @param renderingMode The rendering mode we want to use
   */
  public static void setRenderingMode(RenderingMode renderingMode) {
    Settings.renderingMode = renderingMode;
  }

  /**
   * Gets the current theme
   *
   * @return The theme being used
   */
  public static String getTheme() {
    return theme;
  }

  /**
   * Changes the theme being used
   *
   * @param theme The theme to change the theme to
   */
  public static void setTheme(String theme) {
    Settings.theme = theme;
  }

  /**
   * Saves the settings by persisting them to a file.
   */
  public static void saveSettings() {
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(settingsDirectory));

      writeSetting("UP", up.getName(), bw);
      writeSetting("DOWN", down.getName(), bw);
      writeSetting("LEFT", left.getName(), bw);
      writeSetting("RIGHT", right.getName(), bw);
      writeSetting("USE_ITEM", useItem.getName(), bw);

      writeSetting("X_RES", Integer.toString(xResolution), bw);
      writeSetting("Y_RES", Integer.toString(yResolution), bw);
      writeSetting("RENDERING_MODE", renderingMode.getName(), bw);
      writeSetting("THEME", theme, bw);

      writeSetting("MUTE", (mute) ? "TRUE" : "FALSE", bw);
      writeSetting("MUSIC_VOL", Double.toString(musicVolume), bw);
      writeSetting("SFX_VOL", Double.toString(soundVolume), bw);

      writeSetting("NAME", name, bw);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (bw != null) {
        try {
          bw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Writes a setting to the settings file
   *
   * @param name The setting name
   * @param setting The setting value
   * @param bw The buffered writer.
   * @throws IOException Caused by Buffered Writer
   */
  private static void writeSetting(String name, String setting, BufferedWriter bw)
      throws IOException {
    bw.write(name + "=" + setting);
    bw.newLine();
  }

  /** Loads the settings from a file */
  public static void loadSettings() {
    File settingsFile = new File(settingsDirectory);
    if (!settingsFile.exists()) {
      return;
    }
    HashMap<String, String> settings = new HashMap<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(settingsFile));
      String line = br.readLine();
      while (line != null) {
        String key = line.substring(0, line.indexOf("="));
        String value = line.substring(line.indexOf("=") + 1, line.length());
        value = value.replace(System.lineSeparator(), "");
        settings.put(key, value);
        line = br.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    up = KeyCode.getKeyCode(settings.get("UP"));
    down = KeyCode.getKeyCode(settings.get("DOWN"));
    left = KeyCode.getKeyCode(settings.get("LEFT"));
    right = KeyCode.getKeyCode(settings.get("RIGHT"));
    useItem = KeyCode.getKeyCode(settings.get("USE_ITEM"));

    xResolution = Integer.parseInt(settings.get("X_RES"));
    yResolution = Integer.parseInt(settings.get("Y_RES"));
    renderingMode = RenderingMode.fromString(settings.get("RENDERING_MODE"));
    theme = settings.get("THEME");
    name = settings.get("NAME");
    mute = (settings.get("MUTE").equals("TRUE")) ? true : false;
    musicVolume = Double.parseDouble(settings.get("MUSIC_VOL"));
    soundVolume = Double.parseDouble(settings.get("SFX_VOL"));
  }
}
