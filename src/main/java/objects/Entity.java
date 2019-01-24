package objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.enums.Direction;
import utils.Renderable;

public class Entity implements Renderable{

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private Boolean pacMan;
  private ArrayList<Image>[] images;

  public Entity(Boolean pacMan, int clientId) {
    this.pacMan = pacMan;
    this.clientId = clientId;
    this.score = 0;
    this.velocity = 0;
    // images = resourceLoader.getImages(pacMan, clientId)
  }

  public void render(GraphicsContext gc) {

  }

  public Point2D.Double getLocation() {
    return location;
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

  public ArrayList<Image> getImage() { //Change that to a queue
    if (direction == null) {
      return images[0];
    }
    return images[direction.toInt()];
  }

}
