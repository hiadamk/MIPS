package utils;

import static java.lang.Math.abs;

public class Point {

  private double x;
  private double y;
  private final int MAX_X;
  private final int MAX_Y;
  private final boolean mapped;

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
  }

  public void increaseX(double offset) {
    x = (x + offset + MAX_X) % MAX_X;
    y = (int) y + 0.5;
  }

  public void increaseY(double offset) {
    y = (y + offset + MAX_Y) % MAX_Y;
    x = (int) x + 0.5;
  }

  public boolean inRange(Point p) {
    Point temp = p;
    temp.increaseX(-this.x);
    temp.increaseY(-this.y);
    return (abs(temp.getX()) <= 0.5 && abs(temp.getY()) <= 0.5);
  }

  public String toString() {
    return "[Point] x = " + x + ", y = " + y;
  }
}
