package utils;

import static java.lang.Math.abs;

/**
 * encapsulates point in 2d space on a map if given map, will ensure modularity
 *
 * @author Alex Banks, Matthew Jones
 */
public class Point {

  private static final double EQUALITY_THRESHOLD = 0.01;
  private final int MAX_X;
  private final int MAX_Y;
  private final boolean mapped;
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
    this.MAX_Y = 0;
    this.MAX_X = 0;
    this.mapped = false;
  }

  /**
   * Will ensure modularity.
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
    this.mapped = true;
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
    this.mapped = MAX_X > 0 && MAX_Y > 0;

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
    this.mapped = mapped;
  }

  /**
   * @see #getCopy()
   * @deprecated
   */
  public static Point copyOf(Point p) {
    return new Point(p.x, p.y, p.MAX_X, p.MAX_Y, p.mapped);
  }

  /**
   * can be mutated without affecting original copy
   *
   * @return new instance which is exact duplicate.
   */
  public Point getCopy() {
    return new Point(this.x, this.y, this.MAX_X, this.MAX_Y, this.mapped);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  /**
   * sets new location, check modularity.
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
   * increases x, locks y to int+0.5, checks modularity.
   *
   * @param offset amount to increase x by, can be negative
   * @see #mod()
   */
  public void increaseX(double offset) {
    x += offset;
    y = (int) y + 0.5;
    mod();
  }

  /**
   * increase y, locks x to int+0.5, checks modularity.
   *
   * @param offset amount to increase y by, can be negative
   * @see #mod()
   */
  public void increaseY(double offset) {
    y += offset;
    x = (int) x + 0.5;
    mod();
  }

  /**
   * Puts the point in the centre of its map square, checks modularity.
   *
   * @see #mod()
   * @return the updated point
   */
  public Point centralise() {
    x = (int) x + 0.5;
    y = (int) y + 0.5;
    mod();
    return this;
    /* Does this method really need to return anything? - Yes, so you can set other locations to this.centralise, can be refactored tho */
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
    return "[Point] x = "
        + x
        + ", y = "
        + y
        + ", maxX = "
        + MAX_X
        + ", maxY = "
        + MAX_Y
        + ", mapped = "
        + mapped;
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
    if (mapped) {
      if (MAX_Y <= 0 && MAX_X <= 0) {
        //            System.err.println("Mapped Point has no MaxX or MaxY");
        //            System.out.println(toString());
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

  public String shortString() {
    return "(" + x + ", " + y + ")";
  }

  @Override
  public boolean equals(Object p) {
    if (p instanceof Point) {
      return super.equals(p);
    }
    else {
      Point p2 = (Point)p;
      if (Math.abs(p2.x - this.x) > EQUALITY_THRESHOLD) {
        return false;
      }
      if (Math.abs(p2.y - this.y) > EQUALITY_THRESHOLD) {
        return false;
      }
      return true;
    }
  }
}
