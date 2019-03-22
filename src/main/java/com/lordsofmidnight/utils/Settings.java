package com.lordsofmidnight.utils;

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

  private static Boolean mute = false;
  private static double musicVolume = 0.5;
  private static double soundVolume = 0.5;

  public static Boolean getMute() {
    return mute;
  }

  public static void setMute(Boolean mute) {
    Settings.mute = mute;
  }

  public static double getMusicVolume() {
    return musicVolume;
  }

  public static void setMusicVolume(double musicVolume) {
    Settings.musicVolume = musicVolume;
  }

  public static double getSoundVolume() {
    return soundVolume;
  }

  public static void setSoundVolume(double soundVolume) {
    Settings.soundVolume = soundVolume;
  }

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

  public static int getxResolution() {
    return xResolution;
  }

  public static void setxResolution(int xResolution) {
    Settings.xResolution = xResolution;
  }

  public static int getyResolution() {
    return yResolution;
  }

  public static void setyResolution(int yResolution) {
    Settings.yResolution = yResolution;
  }

  public static RenderingMode getRenderingMode() {
    return renderingMode;
  }

  public static void setRenderingMode(RenderingMode renderingMode) {
    Settings.renderingMode = renderingMode;
  }

  public static String getTheme() {
    return theme;
  }

  public static void setTheme(String theme) {
    Settings.theme = theme;
  }

  public static void saveSettings() {
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(settingsDirectory));

      writeSetting("UP", up.getName(), bw);
      writeSetting("DOWN", down.getName(), bw);
      writeSetting("LEFT", left.getName(), bw);
      writeSetting("RIGHT", right.getName(), bw);
      writeSetting("USE_ITEM",useItem.getName(),bw);

      writeSetting("X_RES", Integer.toString(xResolution), bw);
      writeSetting("Y_RES", Integer.toString(yResolution), bw);
      writeSetting("RENDERING_MODE", renderingMode.getName(), bw);
      writeSetting("THEME", theme, bw);

      writeSetting("MUTE", (mute) ? "TRUE" : "FALSE", bw);
      writeSetting("MUSIC_VOL", Double.toString(musicVolume), bw);
      writeSetting("SFX_VOL", Double.toString(soundVolume), bw);
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

  private static void writeSetting(String name, String setting, BufferedWriter bw)
      throws IOException {
      bw.write(name + "=" + setting);
      bw.newLine();
  }

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
    }finally {
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

    mute = (settings.get("MUTE").equals("TRUE")) ? true : false;
    musicVolume = Double.parseDouble(settings.get("MUSIC_VOL"));
    soundVolume = Double.parseDouble(settings.get("SFX_VOL"));
  }
}
