package com.lordsofmidnight.gamestate.points;

import static java.lang.Math.abs;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.utils.enums.Direction;

/**
 * encapsulates point in 2d space on a map if given map, will ensure modularity
 *
 * @author Alex Banks, Matthew Jones
 */
public class Point {

  private static int defaultMaxX = -1;
  private static int defaultMaxY = -1;
  private static boolean hasDefault = false;

  private final double EQUALITY_TOLERANCE = 0.001;
  private final double CENTER_TOLERANCE = 0.1;
  private final double CENTER = 0.5;
  private final int MAX_X;
  private final int MAX_Y;
  private final boolean MAPPED;
  private double x;
  private double y;

  /**
   * Basic constructor, will not ensure modularity.
   *
   * @param x x coord
   * @param y y coord
   */
  public Point(double x, double y) {
    this.x = x;
    this.y = y;

    if (hasDefault) {
      this.MAX_X = defaultMaxX;
      this.MAX_Y = defaultMaxY;
      this.MAPPED = true;
    } else {
      System.err.println("Warning: Point has no reference to map");
      this.MAX_X = 0;
      this.MAX_Y = 0;
      this.MAPPED = false;
    }

    mod();
  }

  /**
   * Will ensure modularity if map is not zero.
   *
   * @param x x coord
   * @param y y coord
   * @param map map that point is on. MAX_X and MAX_Y extracted for modularity.
   */
  public Point(double x, double y, Map map) {
    this.x = x;
    this.y = y;
    this.MAX_X = map.getMaxX();
    this.MAX_Y = map.getMaxY();
    this.MAPPED = true;
    mod();
  }

  /**
   * Will ensure modularity if max_x and max_y are both greater than 0
   *
   * @param x x coord
   * @param y y coord
   * @param max_x MAX_X for modularity
   * @param max_y MAX_Y for modularity
   */
  public Point(double x, double y, int max_x, int max_y) {
    this.x = x;
    this.y = y;
    this.MAX_X = max_x;
    this.MAX_Y = max_y;
    this.MAPPED = MAX_X > 0 && MAX_Y > 0;

    mod();
  }

  /**
   * CopyCat constructor that will duplicate an instance exactly
   *
   * @param x x coord
   * @param y y coord
   * @param max_x max_x
   * @param max_y max_y
   * @param mapped mapped
   */
  public Point(double x, double y, int max_x, int max_y, boolean mapped) {
    this.x = x;
    this.y = y;
    this.MAX_X = max_x;
    this.MAX_Y = max_y;
    this.MAPPED = mapped;
  }

  /**
   * Sets a default map for unmapped points to be generated from.
   *
   * @param m map to extract maxX and maxY from
   * @author Alex Banks
   * @see Client#setMap(Map)
   */
  public static void setMap(Map m) {
    hasDefault = true;
    defaultMaxX = m.getMaxX();
    defaultMaxY = m.getMaxY();
  }

  /**
   * can be mutated without affecting original copy
   *
   * @return new instance which is exact duplicate.
   */
  public Point getCopy() {
    return new Point(this.x, this.y, this.MAX_X, this.MAX_Y, this.MAPPED);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  /**
   * sets new com.lordsofmidnight.gamestate, check modularity.
   *
   * @param x x coord
   * @param y y coord
   * @see #mod()
   */
  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
    mod();
  }

  /**
   * increases x, locks y to int+CENTER, checks modularity.
   *
   * @param offset amount to increase x by, can be negative
   * @see #mod()
   */
  private void increaseX(double offset) {
    x += offset;
    y = (int) y + CENTER;
    mod();
  }

  /**
   * increase y, locks x to int+CENTER, checks modularity.
   *
   * @param offset amount to increase y by, can be negative
   * @see #mod()
   */
  private void increaseY(double offset) {
    y += offset;
    x = (int) x + CENTER;
    mod();
  }

  /**
   * Puts the point in the centre of its map square, checks modularity.
   *
   * @return the updated point
   * @author Alex Banks
   * @see #mod()
   */
  public Point centralise() {
    x = (int) x + CENTER;
    y = (int) y + CENTER;
    mod();
    return this;
  }

  /**
   * @return copy of point with coordinate floored
   * @author Alex Banks
   */
  public Point getGridCoord() {
    Point p = this.getCopy();
    p.x = (int) x;
    p.y = (int) y;
    return p;
  }

  /**
   * produce movement amalgamated into one algorithm to reduce duplicate code
   *
   * @param offset distance to move
   * @param direction direction to move in
   * @author Alex Banks
   */
  public Point moveInDirection(double offset, Direction direction) {

    if (direction == null) {
      return this;
    }

    switch (direction) {
      case UP:
        increaseY(-offset);
        break;
      case DOWN:
        increaseY(offset);
        break;
      case LEFT:
        increaseX(-offset);
        break;
      case RIGHT:
        increaseX(offset);
        break;
    }
    return this;
  }

  /**
   * check if two points are close to each other - used in collision detection
   *
   * @param p point to check against
   * @return true if within 0.5 in x and y direction
   * @author Alex Banks
   */
  public boolean inRange(Point p) {
    Point temp = new Point(this.x - p.getX(), this.y - p.getY(), this.MAX_X, this.MAX_Y);
    return (abs(temp.getX()) <= 0.5 && abs(temp.getY()) <= 0.5);
  }

  public String toString() {
    return "[Point] ("
        + x
        + ","
        + y
        + "), max = ("
        + MAX_X
        + ","
        + MAX_Y
        + "), "
        + (MAPPED ? "mapped" : "unmapped");
  }

  /**
   * Calculates the (Euclidean) distance from this point to the point given
   *
   * @param to The point to calculate distance to
   * @return the Euclidean distance between the two points
   */
  public double distance(Point to) {
    double aSquared = Math.pow(this.x - to.getX(), 2);
    double bSquared = Math.pow(this.y - to.getY(), 2);
    return Math.sqrt(aSquared + bSquared);
  }

  /**
   * ensures that the points stay within the bounds of the game map. Only runs if point is mapped
   * and modularisable
   *
   * @author Alex Banks
   */
  private void mod() {
    if (MAPPED) {
      if (MAX_Y <= 0 && MAX_X <= 0) {
        System.err.println("  You're using a method that could cause Point to go off the map,");
        System.err.println("  but haven't constructed with enough information to stop this.");
        System.err.println("  Please consider using a different constructor.");
        System.err.println(this.toString());
        return;
      }
      while (this.x < 0) {
        x += MAX_X;
      }
      while (this.y < 0) {
        y += MAX_Y;
      }
      x = x % MAX_X;
      y = y % MAX_Y;
    }
  }


  /**
   * @return true if point is within central hitbox
   * @author Alex Banks
   */
  public boolean isCentered() {
    double x = abs((this.getX() % 1) - CENTER);
    double y = abs((this.getY() % 1) - CENTER);
    return !(x >= CENTER_TOLERANCE || y >= CENTER_TOLERANCE);
  }

  /**
   * @param object The object being tested for equality.
   * @return If the {@link Object} cannot be cast to a {@link Point} then false. Otherwise if both x
   * and y of both this and the given {@link Point} are within {@link #EQUALITY_TOLERANCE} of each
   * other.
   * @author Lewis Ackroyd
   */
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Point)) {
      return false;
    }
    Point point = (Point) object;
    return (abs(this.x - point.x) < EQUALITY_TOLERANCE
        && abs(this.y - point.y) < EQUALITY_TOLERANCE);
  }
}
