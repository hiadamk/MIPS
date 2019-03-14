package com.lordsofmidnight.gamestate.points;

import java.util.*;

/**
 * A class that can be used with {@link Point} to allow it to have the same functionality as {@link
 * java.util.Map Map}<{@link Point}, K>. K is the data that is mapped to by a given {@link Point}.
 * This will treat each {@link Point} as the grid coordinate {@link Point} as given by {@link
 * Point#getGridCoord()}.
 *
 * @author Lewis Ackroyd
 */
public class PointMap<K> {

  private final int MAX_X;
  private final HashMap<Integer, K> map;

  public PointMap(com.lordsofmidnight.gamestate.maps.Map map) {
    this.MAX_X = map.getMaxX();
    this.map = new HashMap<Integer, K>();
  }

  public void clear() {
    map.clear();
  }

  public boolean containsKey(Object key) {
    if (!(key instanceof Point)) {
      return false;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.containsKey(value);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  public K get(Object key) {
    if (!(key instanceof Point)) {
      return null;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.get(value);
  }

  public K getOrDefault(Object key, K defaultValue) {
    if (!(key instanceof Point)) {
      return defaultValue;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.getOrDefault(value, defaultValue);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public Set<Point> keySet() {
    Set<Integer> valueSet = map.keySet();
    Set<Point> pointSet = new HashSet<>();
    for (int value : valueSet) {
      pointSet.add(getPointFromKey(value));
    }
    return pointSet;
  }

  public K put(Point p, K data) {
    return map.put(getKeyValue(p), data);
  }

  public K remove(Object o) {
    if (!(o instanceof Point)) {
      return null;
    }
    Point p = (Point) o;
    int key = getKeyValue(p);
    return map.remove(key);
  }

  public int size() {
    return map.size();
  }

  public Collection<K> values() {
    return map.values();
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
