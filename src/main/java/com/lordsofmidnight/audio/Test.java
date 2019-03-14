package com.lordsofmidnight.audio;

import java.util.concurrent.TimeUnit;

public class Test {

  public static void main(String[] args) {
    AudioController audioController = new AudioController();
    test2(audioController);
    sleep(10);
  }

  private static void sleep(int x) {
    try {
      TimeUnit.SECONDS.sleep(x);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void test3(AudioController audioController) {
    audioController.playSound(Sounds.death);
    sleep(3);
    audioController.setSoundVolume(0.25);
    audioController.playSound(Sounds.death);
    sleep(3);
    audioController.setSoundVolume(1);
    audioController.playSound(Sounds.death);
    sleep(3);
    audioController.playMusic(Sounds.intro);
    sleep(3);
    audioController.setMusicVolume(0.2);
    sleep(3);
    audioController.setMusicVolume(1);
    sleep(3);
    audioController.setMusicVolume(0.7);
    sleep(3);
    audioController.setMusicVolume(0.4);
    sleep(3);
  }

  private static void test2(AudioController audioController) {
    System.out.println("Playing intro sound as music");
    audioController.playMusic(Sounds.intro);
    sleep(5);
    System.out.println("muting");
    audioController.toggleMute();
    sleep(2);
    System.out.println("un muting");
    audioController.toggleMute();
    sleep(2);
    audioController.playSound(Sounds.death);
    sleep(2);
    audioController.playSound(Sounds.death);
    sleep(5);
    audioController.playMusic(Sounds.death);
  }

  private static void test1(AudioController audioController) {
    System.out.println("Playing intro sound");
    audioController.playSound(Sounds.intro);
    sleep(10);
    System.out.println("Playing chomp sound");
    audioController.playSound(Sounds.chomp);
    sleep(10);
    System.out.println("Muting sound");
    audioController.toggleMute();
    System.out.println("Playing intro sound (but nothing should happen)");
    audioController.playSound(Sounds.intro);
    sleep(10);
    System.out.println("Unmuting sound");
    audioController.toggleMute();
    System.out.println("Playing death sound (should now play)");
    audioController.playSound(Sounds.death);
    sleep(5);
  }
}
