package objects;

import java.awt.geom.Point2D;
import javafx.scene.canvas.GraphicsContext;
import utils.enums.Direction;
import utils.enums.EntityType;

public class Entity {

  private Point2D.Double location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private EntityType type;

  public Entity(EntityType type) {
    this.type = type;
    this.clientId = type.getId();
    this.score = 0;
    this.velocity = 0;
  }

  public void render(GraphicsContext gc) {
    if (type == EntityType.PACMAN) {
      // TODO implement
    } else if (type == EntityType.GHOST1) {
      // TODO implement
    } else if (type == EntityType.GHOST2) {
      // TODO implement
    } else if (type == EntityType.GHOST3) {
      // TODO implement
    }
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
}
