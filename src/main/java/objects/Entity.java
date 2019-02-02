package objects;

import ai.routefinding.RouteFinder;
import javafx.scene.image.Image;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Entity implements Renderable {

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private Boolean pacMan;
  private ArrayList<ArrayList<Image>> images;
  private RouteFinder routeFinder;
  private ResourceLoader resourceLoader;
  public Entity(Boolean pacMan, int clientId, ResourceLoader resourceLoader) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.score = 0;
    this.velocity = 0;
    this.resourceLoader = resourceLoader;
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
    if (direction == null) {
      return images.get(0);
    }
    return images.get(direction.toInt());
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
    updateImages();
  }

  private void updateImages(){
    images = pacMan ? resourceLoader.getPlayableMip(clientId) : resourceLoader.getPlayableGhoul(clientId);
  }
}
