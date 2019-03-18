package com.lordsofmidnight.gamestate.points;

import java.io.Serializable;
import java.util.*;

/**
 * A class that can be used with {@link Point} to allow it to have the same functionality as {@link
 * java.util.Map Map}<{@link Point}, V>. V is the data that is mapped to by a given {@link Point}.
 * This will treat each {@link Point} as the grid coordinate {@link Point} as given by {@link
 * Point#getGridCoord()}.
 *
 * @author Lewis Ackroyd
 */
public class PointMap<V> extends AbstractMap<Point, V> implements Map<Point, V>, Cloneable, Serializable {
  private final int MAX_X;
  private final HashMap<Integer, V> map;

  public PointMap(com.lordsofmidnight.gamestate.maps.Map map) {
    this.MAX_X = map.getMaxX();
    this.map = new HashMap<>();
  }

  private PointMap(int maxX) {
    this.MAX_X = maxX;
    this.map = new HashMap<>();
  }

  public PointMap<V> getShallowClone() {
    return new PointMap<V>(MAX_X);
  }

  @Override
  public void clear() {
    map.clear();
  }


  @Override
  public boolean containsKey(Object key) {
    if (!(key instanceof Point)) {
      return false;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.containsKey(value);
  }


  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }


  @Override
  public V get(Object key) {
    if (!(key instanceof Point)) {
      return null;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.get(value);
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    if (!(key instanceof Point)) {
      return defaultValue;
    }
    Point p = (Point) key;
    int value = getKeyValue(p);
    return map.getOrDefault(value, defaultValue);
  }


  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }


  @Override
  public Set<Point> keySet() {
    Set<Integer> valueSet = map.keySet();
    Set<Point> pointSet = new HashSet<>();
    for (int value : valueSet) {
      pointSet.add(getPointFromKey(value));
    }
    return pointSet;
  }


  @Override
  public V put(Point p, V data) {
    return map.put(getKeyValue(p), data);
  }


  @Override
  public V remove(Object o) {
    if (!(o instanceof Point)) {
      return null;
    }
    Point p = (Point) o;
    int key = getKeyValue(p);
    return map.remove(key);
  }


  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Collection<V> values() {
    return map.values();
  }

  @Override
  public Set<Entry<Point, V>> entrySet() {
    class PointMapEntry implements Map.Entry<Point, V> {
      private final Point key;
      private V value;

      public PointMapEntry(Point point, V value) {
        this.key = point;
        this.value = value;
      }

      @Override
      public Point getKey() {
        return key;
      }

      @Override
      public V getValue() {
        return value;
      }

      @Override
      public V setValue(V value) {
        V oldValue = value;
        this.value = value;
        return oldValue;
      }
    }
    Set<Entry<Point, V>> returnSet = new HashSet<>();
    for (Point p : keySet()) {
      returnSet.add(new PointMapEntry(p, get(p)));
    }
    return returnSet;
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
