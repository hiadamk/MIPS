package com.lordsofmidnight.renderer;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ExplosionFX {

  private final GraphicsContext gc;
  private final int ANIMATION_SPEED = 14;
  private final long SECONDS_IN_NANOSECONDS = (long) Math.pow(10, 9);
  private final long animationInterval = SECONDS_IN_NANOSECONDS / ANIMATION_SPEED;
  private long timeSinceLastFrame = 0;
  private ResourceLoader resourceLoader;
  private ArrayList<Image> explosionImages;

  // currently active explosions
  private ArrayList<ExplosionInstance> explosions;

  public ExplosionFX(GraphicsContext gc, ResourceLoader r) {
    this.resourceLoader = r;
    this.gc = gc;
    this.explosions = new ArrayList<>();
    this.explosionImages = r.getExplosion();
  }

  /**
   * @param x the X rendering coordinate
   * @param y the Y rendering coordinate
   */
  public void addExplosion(double x, double y) {
    explosions.add(new ExplosionInstance(x, y));
  }

  /**
   * render all explosions in the explosion manager
   * MUST be called from the javaFX application thread
   * @param timeElapsed time elapsed since last call
   */
  public void render(long timeElapsed) {
    if (explosions.size() == 0) {
      return;
    }
    // remove explosions that have completed their animation
    ArrayList<ExplosionInstance> completedExplosions = new ArrayList<>();
    for (ExplosionInstance e : explosions) {
      if (e.getCurrentFrame() >= explosionImages.size()) {
        completedExplosions.add(e);
        continue;
      }

      gc.drawImage(explosionImages.get(e.getCurrentFrame()), e.getX(), e.getY());
    }

    // Advance frame if necessary
    if (animationInterval < timeSinceLastFrame) {
      timeSinceLastFrame = 0;
      for (ExplosionInstance e : explosions) {
        e.nextFrame();
      }
    } else {
      timeSinceLastFrame += timeElapsed;
    }

    // remove completed explosions
    for (ExplosionInstance e : completedExplosions) {
      explosions.remove(e);
    }
  }

  /** refresh images of explosions (after graphics settings have been changed) */
  public void refreshSettings() {
    this.explosionImages = resourceLoader.getExplosion();
  }

  /** */
  private class ExplosionInstance {

    private int currentFrame;

    private double x;
    private double y;

    /**
     * @param x
     * @param y
     */
    public ExplosionInstance(double x, double y) {
      this.currentFrame = 0;
      this.x = x;
      this.y = y;
    }

    /** @return x rendering coordinate of explosion */
    public double getX() {
      return x;
    }

    /** @return y rendering coordinate of explosion */
    public double getY() {
      return y;
    }

    /** advance frame of explosion instance */
    public void nextFrame() {
      currentFrame++;
    }

    /**
     * current frame of explosion
     *
     * @return
     */
    public int getCurrentFrame() {
      return currentFrame;
    }
  }
}
