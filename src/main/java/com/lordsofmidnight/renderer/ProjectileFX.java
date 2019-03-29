package com.lordsofmidnight.renderer;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.objects.powerUps.Rocket;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Class to handle the FX of projectiles
 */
public class ProjectileFX {

  private final GraphicsContext gc;
  private final int animationSpeed = 14;
  private final long secondInNanoseconds = (long) Math.pow(10, 9);
  private final long animationInterval = secondInNanoseconds / animationSpeed;
  private long timeSinceLastFrame = 0;
  private final ResourceLoader r;
  private ArrayList<Rocket> rockets;
  private ArrayList<Image> upwardRocketImages;
  private ArrayList<Image> downwardRocketImages;
  private final Renderer renderer;

  private final double launchDuration = 0.3;
  private final double hitDuration = 0.03;
  private double rocketSpriteHeight;
  private double rocketSpriteWidth;

  private Point2D.Double rendCoord = new Double(0,0);

  /**
   * Manages rocket images so that they fly up and off the screen then after a duration
   * target the entity it was aimed at
   * @param gc Graphics context to render the game onto
   * @param r Resource laoder
   * @param renderer render that the projectile manager belongs to
   */
  public ProjectileFX(GraphicsContext gc, ResourceLoader r, Renderer renderer) {
    this.gc = gc;
    this.r = r;
    this.rockets = new ArrayList<>();
    refreshSettings();
    this.renderer = renderer;
  }

  /**
   * MUST be called in the JavaFX frame
   * @param timeElapsed time since last call (nanoseconds)
   * @param activePowerups powerups active in the game
   */
  public void render(long timeElapsed, ConcurrentHashMap<UUID, PowerUp> activePowerups) {
    rockets = new ArrayList<>();
    for (PowerUp p : activePowerups.values()) {
      if (p.getType() == PowerUps.ROCKET) {
        rockets.add((Rocket) p);
      }
    }

    if (rockets.size() == 0) {
      return;
    }

    //render all active rockets
    for (Rocket r : rockets) {
      renderRocket(r);
    }

    //advance frames if necessary
    if (animationInterval < timeSinceLastFrame) {
      timeSinceLastFrame = 0;
      for (Rocket r : rockets) {
        r.incrementFrame();
      }
    } else {
      timeSinceLastFrame += timeElapsed;
    }
  }

  /**
   * update images from resource loader
   */
  public void refreshSettings() {
    this.upwardRocketImages = r.getRocketImages(false);
    this.downwardRocketImages = r.getRocketImages(true);
    this.rocketSpriteHeight = upwardRocketImages.get(0).getHeight();
    this.rocketSpriteWidth = upwardRocketImages.get(0).getWidth();
  }

  /**
   *
   * @param r Rocket to be rendered
   */
  private void renderRocket(Rocket r) {
    //LAUNCH TIME
    if(r.getTime() < r.getMaxTime()*launchDuration){
      //tween towards top of screen
      if(!r.isLaunched()){
        Point entityLoc = r.getUser().getLocation();
        renderer.setIsoCoord(rendCoord,entityLoc.getX(),entityLoc.getY(),rocketSpriteWidth,rocketSpriteHeight);
        r.setStartLocation(rendCoord);

        r.setLaunched(true);
      }
      double launchX = r.getStartLocation().getX();
      double currentTime = r.getTime()/(r.getMaxTime()*launchDuration);
      double launchY = r.getStartLocation().getY()-(r.getStartLocation().getY()*currentTime);

      gc.drawImage(upwardRocketImages.get(r.getCurrentFrame()%upwardRocketImages.size()),launchX,launchY);
    }
    //HIT TIME
    else if(r.getTime() > r.getMaxTime()-(r.getMaxTime()*hitDuration)){
      if(!r.isTargeted()){
        //tween towards target
        Point entityLoc = r.getTargeted().getLocation();
        renderer.setIsoCoord(rendCoord,entityLoc.getX(),entityLoc.getY(),rocketSpriteWidth,rocketSpriteHeight);
        r.setEndLocation(rendCoord);

        r.setTargeted(true);
      }
      double launchX = r.getEndLocation().getX();
      double currentTime = (r.getTime()-(r.getMaxTime()-r.getMaxTime()*hitDuration))/(r.getMaxTime()*hitDuration);
      double launchY = r.getEndLocation().getY()*currentTime;
      //System.out.println("DOWN:"+currentTime);
      gc.drawImage(downwardRocketImages.get(r.getCurrentFrame()%downwardRocketImages.size()),launchX,launchY);
    }
  }
}
