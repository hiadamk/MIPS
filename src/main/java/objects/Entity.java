package objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utils.enums.Direction;
import utils.enums.EntityType;

public class Entity {

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private EntityType type;
  private ArrayList<Image> imagesUp;
  private ArrayList<Image> imagesDown;
  private ArrayList<Image> imagesLeft;
  private ArrayList<Image> imagesRight;

  public Entity(EntityType type, int clientId) {
    this.type = type;
    this.clientId = clientId;
    this.score = 0;
    this.velocity = 0;
    // images = resourceLoader.getImages(type)
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

  public EntityType getType() {
    return type;
  }

  public void setType(EntityType type) {
    this.type = type;
  }

  public ArrayList<Image> getImage() {
      switch (direction) {
        case UP: return imagesUp;
        case LEFT: return imagesLeft;
        case DOWN: return imagesDown;
        case RIGHT: return imagesRight;
        default: return imagesUp;
      }
  }

}
