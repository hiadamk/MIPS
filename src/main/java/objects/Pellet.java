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

  private Point location;
  private ArrayList<Image> currentImage;
  private boolean active; // Weather or not the item is visible and able to be interacted with
  public Pellet(double x, double y) {
    this.location = new Point(x, y);
    active = true;
  }

  public Pellet(Point p) {
    this.location = p;
    active = true;
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
  }

  public void updateImages(ResourceLoader r) {
    currentImage = r.getPellet();
  }

  @Override
  public String toString() {
    String a = active ? "active" : "not active";
    return "x = " + location.getX() + " y= " + location.getY() + " active = " + a;
  }
}
