package objects;

import ai.routefinding.RouteFinder;
import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.Point;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Entity implements Renderable {

  private boolean test;
  private Point location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private Boolean pacMan;
  private ArrayList<ArrayList<Image>> images;
  private ArrayList<Image> currentImage;
  private RouteFinder routeFinder;
  private Point lastGridCoord;

  public Entity(Boolean pacMan, int clientId, Point location) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.location = location;
    this.score = 0;
    this.velocity = pacMan ? 0.08 : 0.06;
    this.direction = Direction.UP;
    // updateImages();
  }

  public void setTest() {
    test = true;
  }

  public boolean getTest() {
    return test;
  }

  public RouteFinder getRouteFinder() {
    return routeFinder;
  }

  public void setRouteFinder(RouteFinder routeFinder) {
    this.routeFinder = routeFinder;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public Point moveInDirection(double offset, Direction d) {
    Point loc = Point.copyOf(this.location);
    if (d == null) {
      d = this.direction;
    }
    if (d != null) {
      switch (d) {
        case UP:
          loc.increaseY(-offset);
          break;
        case DOWN:
          loc.increaseY(offset);
          break;
        case LEFT:
          loc.increaseX(-offset);
          break;
        case RIGHT:
          loc.increaseX(offset);
          break;
      }
    }

    return loc;
  }

  public void move() {
    this.location = moveInDirection(this.velocity, null);
  }

  public Point getFaceLocation() {
    return moveInDirection(0.5, null);
  }

  public void setLastGridCoord(Point position) {
    this.lastGridCoord = position;
  }

  public Point getLastGridCoord() {
    return lastGridCoord;
  }

  @Override
  public ArrayList<Image> getImage() {
    if (direction != null) {
      return images.get(direction.toInt());
    }
    return currentImage == null ? images.get(0) : currentImage;
  }

  public double getVelocity() {
    return velocity;
  }

  public void setVelocity(double velocity) {
    this.velocity = velocity;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    System.out.println(this.clientId + " " + direction);
    if (this.direction != direction) {
      this.direction = direction;
      if (direction != null) {
        currentImage = images.get(direction.toInt());
      }
    }
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getClientId() {
    return clientId;
  }

  public Boolean isPacman() {
    return pacMan;
  }

  public void setPacMan(Boolean pac) {
    this.pacMan = pac;
    this.velocity = pacMan ? 0.08 : 0.06;
  }

  public void updateImages(ResourceLoader resourceLoader) {
    images =
        pacMan
            ? resourceLoader.getPlayableMip(clientId)
            : resourceLoader.getPlayableGhoul(clientId);
  }

  @Override
  public String toString() {
    String outStr = "";
    if (this.pacMan) {
      outStr += "mip" + clientId;
    } else {
      outStr += "ghoul" + clientId;
    }
    return outStr;
  }

  public String toStringExpanded() {
    String outStr = "";
    if (this.pacMan) {
      outStr += "mip" + clientId;
    } else {
      outStr += "ghoul" + clientId;
    }
    outStr += "\nmyloc " + location.toString();
    outStr += "\nmydir " + direction;
    outStr += "\nroutefinder " + routeFinder;
    outStr += "\nlastPos " + lastGridCoord;
    return outStr;
  }
}
