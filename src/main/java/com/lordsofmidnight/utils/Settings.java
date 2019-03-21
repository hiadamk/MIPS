package com.lordsofmidnight.utils;

import com.lordsofmidnight.utils.enums.InputKey;
import com.lordsofmidnight.utils.enums.RenderingMode;
import javafx.scene.input.KeyCode;

public class Settings {

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
}
