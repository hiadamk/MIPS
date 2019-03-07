package objects;

import ai.routefinding.RouteFinder;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.scene.image.Image;
import utils.Point;
import utils.Renderable;
import utils.ResourceLoader;
import utils.enums.Direction;
import utils.enums.PowerUp;

/**
 * Encapsulation of agent on map Represents both MIPS and Ghouls, as they are interchangeable. Can
 * be user or AI controlled
 *
 * @see Renderable
 */
public class Entity implements Renderable {

  // animation variables
  private final int animationSpeed = 5;
  private Point location;
  private double velocity; // The velocity of the entity currently
  private Direction direction;
  private int score;
  private int clientId;
  private String name;
  private Boolean mipsman;
  private ArrayList<ArrayList<Image>> images;
  private ArrayList<Image> currentImage;
  private RouteFinder routeFinder;
  private Point lastGridCoord;
  private LinkedList<PowerUp> items;
  private long timeSinceLastFrame = 0;
  private int currentFrame = 0;
  private static final double MIPS_SPEED = 0.08;
  private static final double GHOUL_SPEED = 0.06;
  private boolean directionSet;
  private boolean stunned;
  private boolean dead;

  public boolean isStunned() {
    return stunned;
  }

  public void setStunned(boolean stunned) {
    this.stunned = stunned;
    if (stunned) {
      velocity = 0;
    } else {
      resetVelocity();
    }
  }

  public boolean isDead() {
    return dead;
  }

  public void setDead(boolean dead) {
    this.dead = dead;
    if (dead) {
      velocity = 0;
    } else {
      resetVelocity();
    }
  }

  /**
   * Constructor
   *
   * @param mipsman true if Entity should be MIPS upon creation
   * @param clientId id of client (user or AI) controlling this entity
   * @param location starting position of entity
   */
  public Entity(Boolean mipsman, int clientId, Point location) {
    this.mipsman = mipsman;
    this.clientId = clientId;
    this.location = location;
    this.score = 0;
    resetVelocity();
    this.direction = Direction.UP;
    this.items = new LinkedList<PowerUp>();
    this.directionSet = false;
    this.name = "Player" + clientId;
    // updateImages();
  }

  /**
   * @return RouteFinder for this instance, null if user controlled
   * @author Lewis Ackroyd
   */
  public RouteFinder getRouteFinder() {
    return routeFinder;
  }

  public LinkedList<PowerUp> getItems() {
    return items;
  }

  public PowerUp getFirstItem() {
    return items.pop();
  }

  public void giveItem(PowerUp powerUp) {
    if (items.size() <= 2) {
      items.add(powerUp);
    }
  }

  /**
   * @param routeFinder routeFinder for this instance
   * @author Lewis Ackroyd
   */
  public void setRouteFinder(RouteFinder routeFinder) {
    this.routeFinder = routeFinder;
  }

  /**
   * @return current location
   * @author Matty Jones, Alex Banks
   */
  public Point getLocation() {
    return location;
  }

  /**
   * @param location new location
   * @author Matty Jones, Alex Banks
   */
  public void setLocation(Point location) {
    this.location = location;
  }

  /**
   * return where center location would be if agent moved in certain motion. Uses util.Point to
   * ensure modularity (wraping around map)
   *
   * @param offset distance to move
   * @param d direction to move, if unspecified then uses current direction
   * @return util.Point of new location
   * @author Alex Banks, Matty Jones
   * @see Point#moveInDirection(double, Direction)
   */
  public Point getMoveInDirection(double offset, Direction... d) {
    Point loc = this.location.getCopy();
    Direction direction = d.length > 0 ? d[0] : this.direction;

    return loc.moveInDirection(offset, direction);
  }

  /**
   * sets current location to internal moveInDirection method with current velocity.
   *
   * @author Alex Banks, Matty Jones
   * @see #getMoveInDirection(double, Direction...)
   */
  public void move() {
    this.location = getMoveInDirection(this.velocity);
  }

  /**
   * FaceLocation is the point in the center of the entity's face, the external face pointing
   * forwards when traveling. Calculated as the point 0.5 (half agent width) offset from the center
   * in the direction of travel. Crucial to collision detection
   *
   * @return FaceLocation
   * @author Alex Banks
   */
  public Point getFaceLocation() {
    return getMoveInDirection(0.5);
  }

  /**
   * @return current location fixed to a 0.5 offset grid
   */
  public Point getLastGridCoord() {
    return lastGridCoord;
  }

  /** @param position location fixed to a 0.5 offset grid */
  public void setLastGridCoord(Point position) {
    this.lastGridCoord = position;
  }

  /**
   * @return arraylist
   * @author Tim Cheung
   * @see Renderable
   */
  @Override
  public ArrayList<Image> getImage() {
    if (direction != null) {
      return images.get(direction.toInt());
    }
    return currentImage == null ? images.get(0) : currentImage;
  }

  /**
   * @return velocity
   */
  public double getVelocity() {
    return velocity;
  }

  /**
   * @param velocity new velocity
   */
  public void setVelocity(double velocity) {
    this.velocity = velocity;
  }

  /**
   * @return direction
   */
  public Direction getDirection() {
    return direction;
  }

  /**
   * set direction and automatically updates entity image for rendering
   *
   * @param direction direction to change to, can be same as previous, can be null
   * @author Tim Cheung, Matty Jones, Alex Banks
   */
  public void setDirection(Direction direction) {
    if (this.direction != direction) {
      this.direction = direction;
      if (direction != null) {
        currentImage = images.get(direction.toInt());
      }
    }
  }

  /**
   * @return current score
   * @author Matthew Jones
   */
  public int getScore() {
    return score;
  }

  /**
   * @param score new score
   * @author Matthew Jones
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * increase score dependent on i
   *
   * @param i if empty: increase by 1, otherwise: increase by i[0]. Will ignore rest of array
   * @author Matthew Jones
   */
  public void incrementScore(int... i) {
    if (i.length > 0) {
      score = score + i[0];
    } else {
      score++;
    }
  }

  /**
   * no set clientID, called in constructor and immutable
   *
   * @return clientID
   */
  public int getClientId() {
    return clientId;
  }

  /**
   * @return true if MIPS
   */
  public Boolean isMipsman() {
    return mipsman;
  }

  /**
   * @param mips if true then now MIPS, if false then Ghoul
   */
  public void setMipsman(Boolean mips) {
    this.currentFrame = 0;
    this.mipsman = mips;
    resetVelocity();
  }

  public void updateImages(ResourceLoader resourceLoader) {
    currentFrame = 0;
    images =
        mipsman
            ? resourceLoader.getPlayableMip(clientId)
            : resourceLoader.getPlayableGhoul(clientId);
  }

  @Override
  public String toString() {
    String outStr = "";
    if (this.mipsman) {
      outStr += "mip" + clientId;
    } else {
      outStr += "ghoul" + clientId;
    }
    return outStr;
  }

  public String toStringExpanded() {
    String outStr = "";
    if (this.mipsman) {
      outStr += "mip" + clientId;
    } else {
      outStr += "ghoul" + clientId;
    }
    outStr += "\nmyloc " + location.toString();
    outStr += "\nmydir " + direction;
    outStr += "\nroutefinder " + routeFinder;
    outStr += "\nlastPos " + lastGridCoord;
    outStr += "\nscore " + score;
    return outStr;
  }

  /**
   * @return time since last frame
   */
  public long getTimeSinceLastFrame() {
    return timeSinceLastFrame;
  }

  /**
   * @param n time since last frame
   */
  public void setTimeSinceLastFrame(long n) {
    this.timeSinceLastFrame = n;
  }

  /**
   * @return animation speed
   */
  public int getAnimationSpeed() {
    return animationSpeed;
  }

  /**
   * @return current frame
   */
  public int getCurrentFrame() {
    if (this.currentFrame >= getImage().size()) {
      this.currentFrame = 0;
    }
    return currentFrame;
  }

  /**
   * update image to next animation step
   *
   * @author Tim Cheung
   */
  public void nextFrame() {
    if (getImage().size() == 1) {
      currentFrame = 0;
    } else {
      // currentFrame = (currentFrame + 1) % (getImage().size() - 1);
      if (currentFrame >= getImage().size() - 1) {
        currentFrame = 0;
      } else {
        currentFrame++;
      }
    }
  }

  public void setName(String s) {
    this.name = s;
  }

  public String getName() {
    return name;
  }

  public boolean isDirectionSet() {
    return directionSet;
  }

  public void setDirectionSetFlag(boolean b) {
    this.directionSet = b;
  }

  public void resetVelocity() {
    this.velocity = mipsman ? MIPS_SPEED : GHOUL_SPEED;
  }
}
