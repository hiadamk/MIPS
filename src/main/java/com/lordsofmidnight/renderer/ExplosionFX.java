package com.lordsofmidnight.renderer;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.lordsofmidnight.utils.ResourceLoader;

public class ExplosionFX {

  private final GraphicsContext gc;
  private final int animationSpeed = 10;
  private final long secondInNanoseconds = (long) Math.pow(10, 9);
  private final long animationInterval = secondInNanoseconds / animationSpeed;
  private ArrayList<ExplosionInstance> explosions;
  private ArrayList<Image> explosionImages;
  private long timeSinceLastFrame = 0;

  public ExplosionFX(GraphicsContext gc, ResourceLoader r) {
    this.gc = gc;
    this.explosions = new ArrayList<>();
    this.explosionImages = r.getExplosion();

  }

  public void addExplosion(double x, double y) {
    explosions.add(new ExplosionInstance(x, y));
  }

  public void render(long timeElapsed) {
    if (explosions.size() == 0) {
      return;
    }

    ArrayList<ExplosionInstance> completedExplosions = new ArrayList<>();
    for (ExplosionInstance e : explosions) {
      if (e.getCurrentFrame() >= explosionImages.size()) {
        completedExplosions.add(e);
        continue;
      }

      gc.drawImage(explosionImages.get(e.getCurrentFrame()), e.getX(), e.getY());
    }

    if (animationInterval < timeSinceLastFrame) {
      timeSinceLastFrame = 0;
      for (ExplosionInstance e : explosions) {
        e.nextFrame();
      }
    } else {
      timeSinceLastFrame += timeElapsed;
    }

    for (ExplosionInstance e : completedExplosions) {
      explosions.remove(e);
    }
  }


  private class ExplosionInstance {

    private int currentFrame;

    private double x;
    private double y;

    public ExplosionInstance(double x, double y) {
      this.currentFrame = 0;
      this.x = x;
      this.y = y;
    }

    public double getX() {
      return x;
    }

    public double getY() {
      return y;
    }

    public void nextFrame() {
      currentFrame++;
    }

    public int getCurrentFrame() {
      return currentFrame;
    }

  }
}
