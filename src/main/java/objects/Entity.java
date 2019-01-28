package objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import ai.routefinding.RouteFinder;
import javafx.scene.image.Image;
import utils.Renderable;
import utils.enums.Direction;

public class Entity implements Renderable{

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private Boolean pacMan;
  private ArrayList<Image>[] images;
  private RouteFinder routeFinder;

  public Entity(Boolean pacMan, int clientId) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.score = 0;
    this.velocity = 0;
    // images = resourceLoader.getImages(pacMan, clientId)
  }
  
  public void setRouteFinder(RouteFinder routeFinder) {
	  this.routeFinder = routeFinder;
  }
  
  public RouteFinder getRouteFinder() {
	  return routeFinder;
  }

  public Point2D.Double getLocation() {
    return location;
  }

  @Override
  public ArrayList<Image> getImage() {
    if (direction == null) {
      return images[0];
    }
    return images[direction.toInt()];
  }

  public void setLocation(Point2D.Double location) {
    this.location = location;
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
    this.direction = direction;
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
    //images = resourceLoader.getImages(pacMan, clientId)
  }


}
