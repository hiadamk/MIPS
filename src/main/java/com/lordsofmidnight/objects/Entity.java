package com.lordsofmidnight.objects;

import com.lordsofmidnight.ai.routefinding.RouteFinder;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.objects.powerUps.PowerUp;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.Renderable;
import com.lordsofmidnight.utils.StatsTracker;
import com.lordsofmidnight.utils.enums.Direction;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.scene.image.Image;


/**
 * Encapsulation of agent on map Represents both MIPS and Ghouls, as they are interchangeable. Can
 * be user or AI controlled
 *
 * @see Renderable
 */
public class Entity implements Renderable {

  private static final double MIPS_SPEED = 0.08;
  private static final double GHOUL_SPEED = 0.06;
  // animation variables
  private final int animationSpeed = 5;
  private Point location;
  private double velocity; // The velocity of the entity currently
  private double bonusSpeed;
  private Direction direction;
  private Direction oldDirection;
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
  private boolean directionSet;
  private boolean powerUpUsed;
  private int powerUpUseAttempts = 0;
  private boolean stunned;
  private boolean dead;
  private final int DEATHTIME = 400;
  private int deathCounter;
  private boolean invincible;
  private boolean hidden;
  private String killedBy = "";


  private StatsTracker statsTracker;
  private Queue<Point> deathLocation = new ConcurrentLinkedQueue();

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
    this.oldDirection = Direction.UP;
    this.items = new LinkedList<>();
    this.directionSet = false;
    this.powerUpUsed = false;
    this.powerUpUseAttempts = 0;
    this.name = "Player" + clientId;
    this.bonusSpeed = 0;
    this.statsTracker = new StatsTracker();
    // updateImages();
  }

  /**
   * @return The Stats Tracker for the entity
   */
  public StatsTracker getStatsTracker() {
    return statsTracker;
  }

  /**
   *
   * @return If the entity is effected by a bonus speed
   */
  public boolean isSpeeding() {
    return bonusSpeed > 0;
  }

  /**
   *
   * @return If the entity is invincible or not
   */
  public boolean isInvincible() {
    return invincible;
  }
  /**
   * Sets if the entity is invincible or not and sets values accordingly
   * @param invincible If the entity is invincible or not
   */
  public void setInvincible(boolean invincible) {
    this.invincible = invincible;
  }

  /**
   *
   * @return If the entity is stunned or not
   */
  public boolean isStunned() {
    return stunned;
  }

  /**
   * Sets if the entity is stunned or not and sets values accordingly
   * @param stunned If the entity is stunned or not
   */
  public void setStunned(boolean stunned) {
    this.stunned = stunned;
    if (stunned) {
      velocity = 0;
    } else {
      resetVelocity();
    }
  }

  /**
   *
   * @return If the entity is dead or not
   */
  public boolean isDead() {
    return dead;
  }

  /**
   * Sets if the entity is dead or not and sets values accordingly
   * @param dead If the entity is dead or not
   */
  public void setDead(boolean dead) {
    this.dead = dead;
    if (dead) {
      statsTracker.increaseDeaths();
      deathLocation.add(new Point(location.getX(), location.getY()));
      velocity = 0;
      deathCounter = 0;
    } else {
      resetVelocity();
    }

  }

  /**
   *
   * @return The location the entity died at
   */
  public Point getDeathLocation() {
    return deathLocation.poll();
  }

  /**
   * Increases the number of kills the entity has by 1
   */
  public void increaseKills() {
    statsTracker.increaseKills();
  }

  /**
   * Changes the bonus speed applied to the entity
   * @param i the amount to change the speed by
   */
  public void changeBonusSpeed(double i) {
    bonusSpeed += i;
    resetVelocity();
  }

  /**
   * @return RouteFinder for this entity
   * @author Lewis Ackroyd
   */
  public RouteFinder getRouteFinder() {
    return routeFinder;
  }

  /**
   * @param routeFinder routeFinder for this instance
   * @author Lewis Ackroyd
   */
  public void setRouteFinder(RouteFinder routeFinder) {
    this.routeFinder = routeFinder;
  }

  public LinkedList<PowerUp> getItems() {
    return items;
  }
  /**
   * Sets the inventory of the entity
   * @param p1 the id of the first PowerUps in the inventory
   * @param p2 the id of the second PowerUps in the inventory
   */
  public void setItems(int p1, int p2){
    this.items = new LinkedList<PowerUp>(Arrays.asList(PowerUp.fromInt(p1),PowerUp.fromInt(p2)));
  }

  /**
   * Sets the inventory of the entity
   * @param p1 the id of the PowerUps in the inventory
   */
  public void setItems(int p1) {
    this.items = new LinkedList<PowerUp>(Arrays.asList(PowerUp.fromInt(p1)));
  }

  /**
   * //TODO write this, Who understands this method?
   */
  public void setItems() {
    this.items = new LinkedList<PowerUp>();
  }

  /**
   * Returns the first powerUp in the entities inventory
   * @return The PowerUps
   */
  public PowerUp getFirstItem() {
    if (items.size() < 1) {
      return null;
    }
    statsTracker.increaseItemsUsed();
    return items.pop();
  }

  /**
   * If the entity has space to hold the powerUp it adds it to its inventory
   * @param powerUp The powerUp to give
   */
  public void giveItem(PowerUp powerUp) {
    if (items.size() < 2) {
      items.add(powerUp);
    }
  }

  /**
   * @return current com.lordsofmidnight.gamestate
   * @author Matty Jones, Alex Banks
   */
  public Point getLocation() {
    return location;
  }

  /**
   * @see #setLocation(double, double)
   * @param location Point to be made direction
   * @author Matty Jones, Alex Banks
   */
  public void setLocation(Point location) {
    this.location = location;
  }

  /**
   * @param x coord
   * @param y coord
   * @author Matty Jones, Alex Banks
   */
  public void setLocation(double x, double y) {
    if (this.location==null) {
      this.location = new Point(x, y);
    }
    else {
      this.location.setLocation(x, y);
    }
  }

  /**
   * return where center com.lordsofmidnight.gamestate would be if agent moved in certain motion.
   * Uses util.Point to ensure modularity (wraping around map)
   *
   * @param offset distance to move
   * @param d direction to move, if unspecified then uses current direction
   * @return util.Point of new com.lordsofmidnight.gamestate
   * @author Alex Banks, Matty Jones
   * @see Point#moveInDirection(double, Direction)
   */
  public Point getMoveInDirection(double offset, Direction... d) {
    Point loc = this.location.getCopy();
    Direction direction = d.length > 0 ? d[0] : this.direction;

    return loc.moveInDirection(offset, direction);
  }

  /**
   * sets current com.lordsofmidnight.gamestate to internal moveInDirection method with current
   * velocity.
   *
   * @author Alex Banks, Matty Jones
   * @see #getMoveInDirection(double, Direction...)
   */
  public void move() {
    if (!stunned && !dead) {
      this.location = getMoveInDirection(this.velocity);
    }
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
   * @return current com.lordsofmidnight.gamestate fixed to a 0.5 offset grid
   */
  public Point getLastGridCoord() {
    return lastGridCoord;
  }

  /**
   * @param position com.lordsofmidnight.gamestate fixed to a 0.5 offset grid
   */
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
    if (direction.toInt() < 4) {
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
      if (direction != Direction.STOP) {
      } else {
        oldDirection = this.direction;
      }
      this.direction = direction;
    }
  }

  /**
   * Gets the direction the entity is facing
   *
   * @return the direction it is facing using old diretion if the diretion is some how null or is
   * set to stop
   */
  public Direction getFacing() {
    if (direction == null || direction == Direction.STOP || direction == Direction.USE) {
      return oldDirection;
    }
    return direction;
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
      if (i[0] > 0) {
        statsTracker.increasePointsGained(i[0]);
      } else {
        statsTracker.increasePointsLost(-i[0]);
      }
    } else {
      score++;
      statsTracker.increasePointsGained();
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

  /**
   * Updates the image of the entity to the correct one
   * @param resourceLoader The resource loader to get the images from
   */
  public void updateImages(ResourceLoader resourceLoader) {
    currentFrame = 0;
    images =
        mipsman
            ? resourceLoader.getPlayableMip(clientId)
            : resourceLoader.getPlayableGhoul(clientId);
  }

  /**
   *
   * @return The relevant info about the entity in string form
   */
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

  /**
   * Increments the re-spawn counter and if its time re-spawns the entity
   * @return If the entity has re-spawned
   */
  public boolean countRespawn() {
    if ( deathCounter == DEATHTIME) {
      deathCounter = 0;
      setDead(false);
      return true;
    }
    deathCounter++;
    return false;
  }

  /**
   * Adds to the tracker the number of points its stolen
   * @param i The number of Points to add
   */
  public void increasePointsStolen(int i) {
    statsTracker.increasePointsStolen(i);
  }

  /**
   *
   * @return The name of the player controlling the entity
   */
  public String getName() {
    return name;
  }

  /**
   *
   * @param s The name of the player controlling the entity
   */
  public void setName(String s) {
    this.name = s;
  }

  /**@return True if the direction for this entity has already been set by AI but not processed by server
   * @author Lewis Ackroyd*/
  public boolean isDirectionSet() {
    return directionSet;
  }

  /**Sets if the {@link Direction} for this entity has been set but not processed by the server (True),
   * or if the {@link Direction} has been processed by the server (False).
   * @author Lewis Ackroyd*/
  public void setDirectionSetFlag(boolean b) {
    this.directionSet = b;
  }

  /**@return True if the {@link PowerUps} for this entity has already been set by AI but not processed by server
   * @author Lewis Ackroyd*/
  public boolean isPowerUpUsed() {
    return powerUpUsed;
  }

  /**Sets if the {@link PowerUps} for this entity has been set but not processed by the server (True),
   * or if the {@link PowerUps} has been processed by the server (False).
   * @author Lewis Ackroyd*/
  public void setPowerUpUsedFlag(boolean b) {
    this.powerUpUsed = b;
    this.powerUpUseAttempts = 0;
  }

  /**Increases the chance out of the total probability of being used by 1 in the total. (e.g. if probability starts at 1/10, will increase use chance by 1/10)
   * @author Lewis Ackroyd*/
  public void incrementPowerUpUseChance() {
    this.powerUpUseAttempts++;
  }

  /**
   * Returns the number of times that the current {@link PowerUps} has been attempted to be used.
   */
  public int powerUpUseAttempts() {
    return powerUpUseAttempts;
  }

  /**
   * Resets the velocity depending on the type of entity
   */
  public void resetVelocity() {
    this.velocity = (mipsman ? MIPS_SPEED : GHOUL_SPEED) + bonusSpeed;
  }

  /**
   *
   * @return the entities death counter
   */
  public int getDeathCounter() {
    return this.deathCounter;
  }

  /**
   *
   * @return how long the entity stays dead for
   */
  public int getDeathTime() {
    return this.DEATHTIME;
  }

  /**
   * Toggles if this entity is hidden from players
   */
  public void toggleHidden() {
    hidden = !hidden;
  }

  /**
   *
   * @return true if the entity is to be hidden from players
   */
  public boolean getHidden() {
    return hidden;
  }

  /**
   *
   * @return The name of the player who killed the entity
   */
  public String getKilledBy() {
    return this.killedBy;
  }

  /**
   * Sets who the entity was killed by
   *
   * @param name The name of who killed the entity
   */
  public void setKilledBy(String name){
    this.killedBy = name;
  }
}
