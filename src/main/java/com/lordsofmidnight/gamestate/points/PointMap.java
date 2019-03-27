package com.lordsofmidnight.gamestate.points;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

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
  private final HashMap<Integer, Point> keyMappings;

  /**Initialises this Map according to the paramaters of the {@link com.lordsofmidnight.gamestate.maps.Map Map}.
   *
   * @param map The map on which the stored points will be held.
   * @author Lewis Ackroyd*/
  public PointMap(com.lordsofmidnight.gamestate.maps.Map map) {
    this.MAX_X = map.getMaxX();
    this.map = new HashMap<>();
    this.keyMappings = new HashMap<>();
  }

  /**Initialises this Map by using the specified value as the size of the x-axis.
   *
   * @param maxX The maximum xValue of any points being passed to this map
   * @author Lewis Ackroyd*/
  private PointMap(int maxX) {
    this.MAX_X = maxX;
    this.map = new HashMap<>();
    this.keyMappings = new HashMap<>();
  }

  /**Creates a clone of this map, but without any of it's elements
   * @author Lewis Ackroyd*/
  public PointMap<V> getShallowClone() {
    return new PointMap<V>(MAX_X);
  }

  @Override
  public void clear() {
    map.clear();
    keyMappings.clear();
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
    int keyValue = getKeyValue(p);
    keyMappings.put(keyValue, p);
    return map.put(keyValue, data);
  }


  @Override
  public V remove(Object o) {
    if (!(o instanceof Point)) {
      return null;
    }
    Point p = (Point) o;
    int key = getKeyValue(p);
    keyMappings.remove(key);
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

  /*@Override
  public boolean equals(Object o) {
    if (!(o instanceof PointMap)) {
      return false;
    }
    PointMap pm = (PointMap) o;
    if (this.MAX_X != pm.MAX_X) {
      return false;
    }
    return this.map.equals(pm.map);
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
