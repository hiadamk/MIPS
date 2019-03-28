package com.lordsofmidnight.objects;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.Renderable;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

/**
 * Class for the pellet items and base class for all items
 *
 * @author Matthew Jones
 */
public class Pellet implements Renderable {

  static Random r = new Random();
  protected Point location;
  protected ArrayList<Image> currentImage;
  protected int respawntime = 2000;
  protected boolean active; // Whether or not the item is visible and able to be interacted with\
  protected int value = 1;
  protected com.lordsofmidnight.objects.powerUps.PowerUp trap;
  protected boolean isTrap = false;
  protected int respawnCount = 0;

  /**
   * @param x The X coordinate of the pellet
   * @param y The Y coordinate of the pellet
   */
  public Pellet(double x, double y) {
    this.location = new Point(x, y);
    active = true;
    respawntime += r.nextInt(500);
  }

  public Pellet(Point p) {
    this.location = p;
    active = true;
    respawntime += r.nextInt(500);
  }

  /**
   * Checks if a given entity can use the item
   * @param e The entity to check
   * @return True if the entity can use the item
   */
  public boolean canUse(Entity e) {
    if (isTrap) {
      return true;
    }
    return e.isMipsman();
  }

  /**
   *
   * @return The location of the pellet
   */
  public Point getLocation() {
    return location;
  }

  @Override
  public ArrayList<Image> getImage() {
    return currentImage;
  }

  /**
   *
   * @return The direction of the pellet
   */
  public Direction getDirection() {
    return null;
  }

  /**
   *
   * @return If the pellet is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Sets if the pellet is active or not
   * @param active If the pellet should be active
   */
  public void setActive(boolean active) {
    this.active = active;
    if (!active) {
      respawnCount = 0;
    }
  }

  /**
   * Updates the pellets image
   * @param r The resource loader to get the image from
   */
  public void updateImages(ResourceLoader r) {
    currentImage = r.getPellet();
  }

  /**
   * Handles the Pellet interacting with entities
   * @param entity The entity interacting with
   * @param agents The list of all Entities
   * @param activePowerUps The list of currently active powerups
   * @param audioController The Audio Controller for sounds
   */
  public void interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps, AudioController audioController) {
    if (isTrap) {
      trap.trigger(entity, activePowerUps, audioController);
      isTrap = false;
      setActive(false);
      return;
    }
    if (!active || !canUse(entity)) {
      return;
    }
    entity.incrementScore(this.value);
    audioController.playSound(Sounds.COIN, entity.getClientId());
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

  /**
   * @return If the pellet needs to be replaced
   */
  public boolean replace() {
    return false;
  }

  /**
   * Sets the pellet to a trap holding a given powerup
   * @param p
   */
  public void setTrap(com.lordsofmidnight.objects.powerUps.PowerUp p) {
    this.trap = p;
    this.active = true;
    this.isTrap = true;
  }

  /**@return True if the current pellet is a {@link PowerUpBox}
   * @author Lewis Ackroyd*/
  public boolean isPowerUpBox() {
    return false;
  }
}
