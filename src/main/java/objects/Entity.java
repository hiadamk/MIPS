package objects;

import ai.routefinding.RouteFinder;

import java.util.ArrayList;

import javafx.scene.image.Image;
import utils.Point;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Entity implements Renderable {
  
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
  //animation variables
  private final int animationSpeed = 5;
  private long timeSinceLastFrame = 0;
  private int currentFrame = 0;
  
  public Entity(Boolean pacMan, int clientId, Point location) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.location = location;
    this.score = 0;
    this.velocity = pacMan ? 0.08 : 0.06;
    this.direction = Direction.UP;
    //updateImages();
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
  
  public Point getMoveInDirection(double offset, Direction... d) {
    Point loc = this.location.getCopy();
    Direction direction = d.length > 0 ? d[0] : this.direction;
    if (direction != null) {
      switch (direction) {
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
    this.location = getMoveInDirection(this.velocity);
  }
  
  public Point getFaceLocation() {
    return getMoveInDirection(0.5);
  }
  
  public Point getLastGridCoord() {
    return lastGridCoord;
  }
  
  public void setLastGridCoord(Point position) {
    this.lastGridCoord = position;
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
    this.currentFrame = 0;
    this.pacMan = pac;
    this.velocity = pacMan ? 0.08 : 0.06;
  }
  
  public void updateImages(ResourceLoader resourceLoader) {
    currentFrame = 0;
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
  
  public void incrementScore(int... i) {
    if (i.length > 0) {
      score = score + i[0];
    } else {
      score++;
    }
  }
  
  public long getTimeSinceLastFrame() {
    return timeSinceLastFrame;
  }
  
  public void setTimeSinceLastFrame(long n) {
    this.timeSinceLastFrame = n;
  }
  
  public int getAnimationSpeed() {
    return animationSpeed;
  }
  
  public int getCurrentFrame() {
    return currentFrame;
  }
  
  public void nextFrame() {
    if (getImage().size() == 1) {
      currentFrame = 0;
    } else {
      //currentFrame = (currentFrame + 1) % (getImage().size() - 1);
      if (currentFrame >= getImage().size() - 1) {
        currentFrame = 0;
      } else {
        currentFrame++;
      }
    }
  }
}
