package com.lordsofmidnight.renderer;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.utils.Settings;

public class EndGameScreen {

  private GraphicsContext gc;
  private int xResolution;
  private int yResolution;

  public EndGameScreen(GraphicsContext gc) {
    this.gc = gc;
    this.xResolution = Settings.getxResolution();
    this.yResolution = Settings.getyResolution();
  }

  public void showEndSequence(Entity[] entities) {
    final int targetFrames = 120;

    new AnimationTimer() {
      int frames = 0;

      @Override
      public void handle(long now) {
        if (frames >= targetFrames) {
          this.stop();
        }
        renderFrame();
        frames += 1;
      }
    }.start();
  }

  private void renderFrame() {
  }
}
