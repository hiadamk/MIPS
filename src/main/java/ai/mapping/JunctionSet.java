package ai.mapping;

import utils.Point;

import java.util.*;

public class JunctionSet implements Iterable, Set, Collection {
    private HashMap<Double, HashSet<Double>> points;
    private int size;

    public JunctionSet(){
        points = new HashMap<>();
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            Point point = p.getCopy();
            if (points.containsKey(point.getX())) {
                HashSet<Double> ys = points.get(point.getX());
                return ys.contains(point.getY());
            }
        }
        return false;
    }

    @Override
    public boolean add(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            Point point = p.getCopy();
            HashSet<Double> ys;
            if (points.containsKey(point.getX())) {
                ys = points.get(point.getX());
            }
            else {
                ys = new HashSet<>();
            }
            if (!ys.contains(point.getY())) {
                ys.add(point.getY());
                points.put(point.getX(), ys);
                size++;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            Point point = p.getCopy();
            if (contains(point)) {
                HashSet<Double> ys = points.get(point.getX());
                ys.remove(point.getY());
                if (ys.size()==0) {
                    points.remove(point.getX());
                }
                size--;
                return true;
            }
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
            if (!(p instanceof Point)) {
                return false;
            }
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
        Point[] pointsArray = new Point[size];
        int index = 0;
        for (double x : points.keySet()) {
            HashSet<Double> ys = points.get(x);
            for (double y : ys) {
                pointsArray[index] = new Point(x, y);
                index++;
            }
        }
        return pointsArray;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        points.clear();
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public JunctionSet clone() {
        JunctionSet outSet = new JunctionSet();
        for (double x : points.keySet()) {
            HashSet<Double> ys = points.get(x);
            for (double y : ys) {
                outSet.add(new Point(x, y));
            }
        }
        return outSet;
    }

    @Override
    public Iterator iterator() {
        final JunctionSetIterator junctionSetIterator = new JunctionSetIterator(this);
        return junctionSetIterator;
    }

    private class JunctionSetIterator implements Iterator {
        private final Point[] points;
        private int currentIndex;

        public JunctionSetIterator(JunctionSet js) {
            points = js.toArray();
            currentIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return currentIndex<points.length;
        }

        @Override
        public Point next() {
            Point nextPoint = points[currentIndex];
            currentIndex++;
            return nextPoint;
        }
    }
}
