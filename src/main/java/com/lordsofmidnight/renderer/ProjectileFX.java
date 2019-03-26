package com.lordsofmidnight.renderer;

import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.powerUps.Blueshell;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ProjectileFX {
  private final GraphicsContext gc;
  private final int animationSpeed = 14;
  private final long secondInNanoseconds = (long) Math.pow(10, 9);
  private final long animationInterval = secondInNanoseconds / animationSpeed;
  private long timeSinceLastFrame = 0;
  private final ResourceLoader r;
  private ArrayList<Blueshell> rockets;
  private ArrayList<Image> upwardRocketImages;
  private ArrayList<Image> downwardRocketImages;
  private final Renderer renderer;

  private final double launchDuration = 0.3;
  private final double hitDuration = 0.03;
  private double rocketSpriteHeight;
  private double rocketSpriteWidth;

  public ProjectileFX(GraphicsContext gc, ResourceLoader r, Renderer renderer) {
    this.gc = gc;
    this.r = r;
    this.rockets = new ArrayList<>();
    refreshSettings();
    this.renderer = renderer;
  }

  public void render(long timeElapsed, ConcurrentHashMap<UUID, PowerUp> activePowerups) {
    rockets = new ArrayList<>();
    for (PowerUp p : activePowerups.values()) {
      if (p.getType() == PowerUps.BLUESHELL) {
        rockets.add((Blueshell) p);
      }
    }
    //System.out.println(rockets.size());
    if (rockets.size() == 0) {
      return;
    }



    for (Blueshell r : rockets) {
      renderRocket(r);
    }

    if (animationInterval < timeSinceLastFrame) {
      timeSinceLastFrame = 0;
      for (Blueshell r : rockets) {
        r.incrementFrame();
      }
    } else {
      timeSinceLastFrame += timeElapsed;
    }
  }

  public void refreshSettings() {
    this.upwardRocketImages = r.getRocketImages(false);
    this.downwardRocketImages = r.getRocketImages(true);
    this.rocketSpriteHeight = upwardRocketImages.get(0).getHeight();
    this.rocketSpriteWidth = upwardRocketImages.get(0).getWidth();
  }

  private void renderRocket(Blueshell r) {
    if(r.getTime() < r.getMaxTime()*launchDuration){
      if(!r.isLaunched()){
        Point entityLoc = r.getUser().getLocation();
        Point2D.Double renderCoord = renderer.getIsoCoord(entityLoc.getX(),entityLoc.getY(),rocketSpriteWidth,rocketSpriteHeight);
        r.setStartLocation(renderCoord);

        r.setLaunched(true);
      }
      double launchX = r.getStartLocation().getX();
      double currentTime = r.getTime()/(r.getMaxTime()*launchDuration);
      double launchY = r.getStartLocation().getY()-(r.getStartLocation().getY()*currentTime);
      //System.out.println("UP:"+currentTime);
      gc.drawImage(upwardRocketImages.get(r.getCurrentFrame()%upwardRocketImages.size()),launchX,launchY);
    }
    else if(r.getTime() > r.getMaxTime()-(r.getMaxTime()*hitDuration)){
      if(!r.isTargeted()){
        Point entityLoc = r.getTargeted().getLocation();
        Point2D.Double renderCoord = renderer.getIsoCoord(entityLoc.getX(),entityLoc.getY(),rocketSpriteWidth,rocketSpriteHeight);
        r.setEndLocation(renderCoord);

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
