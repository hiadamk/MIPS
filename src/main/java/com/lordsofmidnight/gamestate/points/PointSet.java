package com.lordsofmidnight.gamestate.points;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.lordsofmidnight.gamestate.maps.Map;

/**
 * A class that can be used with {@link Point} to allow it to have the same functionality as {@link
 * java.util.Set Set}<{@link Point}>. This will treat each {@link Point} as the grid coordinate
 * {@link Point} as given by {@link Point#getGridCoord()}.
 *
 * @author Lewis Ackroyd
 */
public class PointSet implements Iterable, Set, Collection {

  private HashSet<Integer> points;
  private final int MAX_X;

  public PointSet(Map map) {
    this.MAX_X = map.getMaxX();
    points = new HashSet<>();
  }

  private PointSet(int maxX) {
    this.MAX_X = maxX;
    points = new HashSet<>();
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
  public boolean add(Object o) {
    if (o instanceof Point) {
      Point p = (Point) o;
      int key = getKeyValue(p);
      return points.add(key);
    }
    return false;
  }

  @Override
  public boolean remove(Object o) {
    if (o instanceof Point) {
      Point p = (Point) o;
      int key = getKeyValue(p);
      return remove(key);
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
    Iterator<Point> iterator = iterator();
    boolean changed = false;
    while (iterator.hasNext()) {
      Point p = iterator.next();
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
    return new PointSetIterator(this);
  }

  private class PointSetIterator implements Iterator {

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

  private int getKeyValue(Point p) {
    p = p.getGridCoord();
    return (((int) p.getY()) * MAX_X) + (int) p.getX();
  }

  private Point getPointFromKey(int key) {
    int xVal = key % MAX_X;
    int yVal = (key - xVal) / MAX_X;
    return new Point(xVal, yVal);
  }
}
