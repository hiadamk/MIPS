package com.lordsofmidnight.gamestate.points;

import java.io.Serializable;
import java.util.*;

import com.lordsofmidnight.gamestate.maps.Map;

/**
 * A class that can be used with {@link Point} to allow it to have the same functionality as {@link
 * java.util.Set Set}<{@link Point}>. This will treat each {@link Point} as the grid coordinate
 * {@link Point} as given by {@link Point#getGridCoord()}.
 *
 * @author Lewis Ackroyd
 */
public class PointSet extends AbstractSet<Point> implements Set<Point>, Cloneable, Serializable {

  private final HashSet<Integer> points;
  private final HashMap<Integer, Point> keyMappings;
  private final int MAX_X;

  /**Initialises this Map according to the paramaters of the {@link Map}.
   *
   * @param map The map on which the stored points will be held.
   * @author Lewis Ackroyd*/
  public PointSet(Map map) {
    this.MAX_X = map.getMaxX();
    this.points = new HashSet<>();
    this.keyMappings = new HashMap<>();
  }

  /**Initialises this Map by using the specified value as the size of the x-axis.
   *
   * @param maxX The maximum xValue of any points being passed to this map
   * @author Lewis Ackroyd*/
  private PointSet(int maxX) {
    this.MAX_X = maxX;
    this.points = new HashSet<>();
    this.keyMappings = new HashMap<>();
  }

  /**Creates a clone of this map, but without any of it's elements
   * @author Lewis Ackroyd*/
  public PointSet getShallowClone() {
    return new PointSet(MAX_X);
  }

  @Override
  public boolean contains(Object o) {
    if (o instanceof Point) {
      Point p = (Point) o;
      return points.contains(getKeyValue(p));
    }
    return false;
  }

  @Override
  public boolean add(Point p) {
      int key = getKeyValue(p);
      keyMappings.put(key, p);
      return points.add(key);
  }

  @Override
  public boolean remove(Object o) {
    if (o instanceof Point) {
      Point p = (Point) o;
      int key = getKeyValue(p);
      keyMappings.remove(key);
      return points.remove(key);
    }
    return false;
  }

  @Override
  public boolean addAll(Collection collection) {
    Object[] objects = collection.toArray();
    if (!(objects instanceof Point[])) {
      return false;
    }
    Point[] ps = (Point[]) objects;
    for (Point p : ps) {
      add(p);
    }
    return true;
  }

  @Override
  public boolean removeAll(Collection collection) {
    Object[] objects = collection.toArray();
    if (!(objects instanceof Point[])) {
      return false;
    }
    Point[] ps = (Point[]) objects;
    boolean changed = false;
    for (Point p : ps) {
      if (remove(p)) {
        changed = true;
      }
    }
    return changed;
  }

  @Override
  public boolean retainAll(Collection collection) {
    Object[] objects = collection.toArray();
    if (!(objects instanceof Point[])) {
      return false;
    }
    boolean changed = false;
      for (Point p : this) {
      if (!collection.contains(p)) {
        remove(p);
        changed = true;
      }
    }
    return changed;
  }

  @Override
  public boolean containsAll(Collection collection) {
    Object[] objects = collection.toArray();
    if (!(objects instanceof Point[])) {
      return false;
    }
    Point[] ps = (Point[]) objects;
    for (Point p : ps) {
      if (!contains(p)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Object[] toArray(Object[] objects) {
    if (!(objects instanceof Point[])) {
      throw new ArrayStoreException();
    }
    return toArray();
  }

  @Override
  public Point[] toArray() {
    Point[] pointsArray = new Point[points.size()];
    int index = 0;
    for (int key : points) {
      pointsArray[index] = getPointFromKey(key);
      index++;
    }
    return pointsArray;
  }

  @Override
  public int size() {
    return points.size();
  }

  @Override
  public void clear() {
    keyMappings.clear();
    points.clear();
  }

  @Override
  public boolean isEmpty() {
    return points.isEmpty();
  }

  @Override
  public PointSet clone() {
    PointSet outSet = new PointSet(MAX_X);
    for (int key : points) {
      outSet.add(getPointFromKey(key));
    }
    return outSet;
  }

  @Override
  public Iterator iterator() {
    class PointSetIterator implements Iterator {

    private final Point[] points;
    private int currentIndex;

    public PointSetIterator(PointSet ps) {
      points = ps.toArray();
      currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
      return currentIndex < points.length;
    }

    @Override
    public Point next() {
      Point nextPoint = points[currentIndex];
      currentIndex++;
      return nextPoint;
    }
  }
    return new PointSetIterator(this);
  }

  /*@Override
  public boolean equals(Object o) {
    if (!(o instanceof PointSet)) {
      return false;
    }
    PointSet ps = (PointSet) o;
    if (this.MAX_X != ps.MAX_X) {
      return false;
    }
    return this.points.equals(ps.points);
  }*/

  /**Calculates the key value that will be used for a given point within the map.
   *
   * @param p The point being used as a key
   *
   * @return The key value to be used by the internal map
   * @author Lewis Ackroyd*/
  private int getKeyValue(Point p) {
    p = p.getGridCoord();
    return (((int) p.getY()) * MAX_X) + (int) p.getX();
  }

  /**Calculates the point that generates the given key.
   *
   * @param key The key used for the internal map
   *
   * @return The point that generated this key
   * @author Lewis Ackroyd*/
  private Point getPointFromKey(int key) {
    return keyMappings.get(key);
  }
}
