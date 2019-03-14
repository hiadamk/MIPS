package com.lordsofmidnight.utils.enums;

public enum ScreenResolution {
  LOW("1366x768"),
  MEDIUM("1920x1080"),
  HIGH("2560x1080");

  private String resolutionString;

  ScreenResolution(String resolutionString) {
    this.resolutionString = resolutionString;
  }

  public String getResolutionString() {
    return resolutionString;
  }
}
