package ai.mapping;

import utils.Map;
import utils.Point;
import java.util.*;

public class PointMap<K> {
    private final int MAX_X;
    private final int MAX_Y;
    private final HashMap<Integer, K> map;

    public PointMap (Map map) {
        this.MAX_X = map.getMaxX();
        this.MAX_Y = map.getMaxY();
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
        for (int value: valueSet) {
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
        int yVal = (key-xVal)/MAX_X;
        return new Point(xVal, yVal);
    }
}
