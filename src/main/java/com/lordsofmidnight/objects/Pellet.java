package com.lordsofmidnight.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.Renderable;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * Class for the pellet items and base class for all items
 *
 * @author Matthew Jones
 */
public class Pellet implements Renderable {

  protected Point location;
  protected ArrayList<Image> currentImage;
  protected int respawntime = 4500;
  protected boolean active; // Weather or not the item is visible and able to be interacted with\
  protected int value = 1;
  protected com.lordsofmidnight.objects.powerUps.PowerUp trap;
  protected boolean isTrap = false;
  private int respawnCount = 0;

  public Pellet(double x, double y) {
    this.location = new Point(x, y);
    active = true;
    Random r = new Random();
    respawntime += r.nextInt(500);
  }

  public Pellet(Point p) {
    this.location = p;
    active = true;
    Random r = new Random();
    respawntime += r.nextInt(500);
  }

  public boolean canUse(Entity e) {
    if (isTrap) {
      return true;
    }
    return e.isMipsman();
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  @Override
  public ArrayList<Image> getImage() {
    return currentImage;
  }

  public Direction getDirection() {
    return null;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
    if (!active) {
      respawnCount = 0;
    }
  }

  public void updateImages(ResourceLoader r) {
    currentImage = r.getPellet();
  }

  public void interact(Entity entity, Entity[] agents, ConcurrentHashMap<UUID, PowerUp> activePowerUps) {
    if (isTrap) {
      trap.trigger(entity, activePowerUps);
      isTrap = false;
      setActive(false);
      return;
    }
    if (!active || !canUse(entity)) {
      return;
    }
    entity.incrementScore(this.value);
    setActive(false);
  }

  @Override
  public String toString() {
    String a = active ? "active" : "not active";
    return "x = " + location.getX() + " y= " + location.getY() + " active = " + a;
  }

  /**
   * Called every physics update to increment the counter for respawn
   */
  public void incrementRespawn() {
    if (!active) {
      respawnCount++;
    }
    if (respawnCount == respawntime) {
      this.active = true;
    }
  }

  public boolean isTrap() {
    return isTrap;
  }

  public void setTrap(com.lordsofmidnight.objects.powerUps.PowerUp p) {
    this.trap = p;
    this.active = true;
    this.isTrap = true;
  }

  public boolean isPowerPellet() {
    return false;
  }
}
