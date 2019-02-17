package utils;

import static java.lang.Math.abs;

/** encapsulates point in 2d space on a map if given map, will ensure modularity */
public class Point {

  private final int MAX_X;
  private final int MAX_Y;
  private final boolean mapped;
  private double x;
  private double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
    this.MAX_Y = 0;
    this.MAX_X = 0;
    this.mapped = false;
  }

  public Point(double x, double y, Map map) {
    this.x = x;
    this.y = y;
    this.MAX_X = map.getMaxX();
    this.MAX_Y = map.getMaxY();
    this.mapped = true;
    mod();
  }

  public Point(double x, double y, int max_x, int max_y) {
    this.x = x;
    this.y = y;
    this.MAX_X = max_x;
    this.MAX_Y = max_y;
    this.mapped = MAX_X > 0 && MAX_Y > 0 ? true : false;

    mod();
  }

  public Point(double x, double y, int max_x, int max_y, boolean mapped) {
    this.x = x;
    this.y = y;
    this.MAX_X = max_x;
    this.MAX_Y = max_y;
    this.mapped = mapped;
  }

  public static Point copyOf(Point p) {
    return new Point(p.x, p.y, p.MAX_X, p.MAX_Y, p.mapped);
  }

  public Point getCopy() {
    return new Point(this.x, this.y, this.MAX_X, this.MAX_Y, this.mapped);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
    mod();
  }

  public void increaseX(double offset) {
    x += offset;
    y = (int) y + 0.5;
    mod();
  }

  public void increaseY(double offset) {
    y += offset;
    x = (int) x + 0.5;
    mod();
  }

  public boolean inRange(Point p) {
    Point temp = new Point(this.x - p.getX(), this.y - p.getY(), this.MAX_X, this.MAX_Y);
    return (abs(temp.getX()) <= 0.5 && abs(temp.getY()) <= 0.5);
  }

  public String toString() {
    return "[Point] x = " + x + ", y = " + y + ", maxX = " + MAX_X + ", maxY = " + MAX_Y
        + ", mapped = " + mapped;
  }

  public double distance(Point to) {
    double aSquared = Math.pow(this.x - to.getX(), 2);
    double bSquared = Math.pow(this.y - to.getY(), 2);
    return Math.sqrt(aSquared + bSquared);
  }

  private void mod() {
    if (mapped) {
      if (MAX_Y <= 0 && MAX_X <= 0) {
        System.err.println("Mapped Point has no MaxX or MaxY");
        System.out.println(toString());
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
}
