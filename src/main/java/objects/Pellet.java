package objects;

import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.Point;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;

/**
 * Class for the pellet items and base class for all items
 *
 * @author Matthew Jones
 */
public class Pellet implements Renderable {

  protected Point location;
  protected ArrayList<Image> currentImage;
  protected int respawntime = 5000;
  protected boolean active; // Weather or not the item is visible and able to be interacted with\
  protected int value = 1;
  private int respawnCount = 0;

  public Pellet(double x, double y) {
    this.location = new Point(x, y);
    active = true;
  }

  public Pellet(Point p) {
    this.location = p;
    active = true;
  }

  public boolean canUse(Entity e) {
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

  public void interact(Entity entity) {
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
}
