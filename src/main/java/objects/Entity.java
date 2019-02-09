package objects;

import ai.routefinding.RouteFinder;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.image.Image;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;

public class Entity implements Renderable {

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private Boolean pacMan;
  private ArrayList<ArrayList<Image>> images;
  private ArrayList<Image> currentImage;
  private RouteFinder routeFinder;

  public Entity(Boolean pacMan, int clientId, Point2D.Double location) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.location = location;
    this.score = 0;
    this.velocity = pacMan ? 0.08 : 0.06;
    //updateImages();
  }

  public RouteFinder getRouteFinder() {
      return routeFinder;
  }
    
    public void setRouteFinder(RouteFinder routeFinder) {
        this.routeFinder = routeFinder;
    }

  public Point2D.Double getLocation() {
    return location;
  }
    
    public void setLocation(Point2D.Double location) {
        this.location = location;
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
    if(this.direction!=direction){
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
    this.velocity = pacMan ? 0.2 : 0.1;
  }

  public void updateImages(ResourceLoader resourceLoader) {
    images = pacMan ? resourceLoader.getPlayableMip(clientId) : resourceLoader.getPlayableGhoul(clientId);
  }
}
