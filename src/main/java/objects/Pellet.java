package objects;

import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.Point;
import utils.Renderable;
import utils.enums.Direction;

public class Pellet implements Renderable {

  public void setLocation(Point location) {
    this.location = location;
  }

  private Point location;
  private ArrayList<Image> currentImage;
  private boolean active;

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
}
