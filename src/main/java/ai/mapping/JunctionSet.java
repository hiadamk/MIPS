package ai.mapping;

import utils.Point;

import java.util.*;

public class JunctionSet implements Iterable, Set, Collection {
    private HashMap<Integer, HashSet<Integer>> points;
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
            if (points.containsKey((int) point.getX())) {
                HashSet<Integer> ys = points.get((int) point.getX());
                return ys.contains((int) point.getY());
            }
        }
        return false;
    }

    @Override
    public boolean add(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            Point point = p.getCopy();
            HashSet<Integer> ys;
            if (points.containsKey((int) point.getX())) {
                ys = points.get((int) point.getX());
            }
            else {
                ys = new HashSet<>();
            }
            if (!ys.contains((int) point.getY())) {
                ys.add((int)point.getY());
                points.put((int)point.getX(), ys);
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
                HashSet<Integer> ys = points.get((int)point.getX());
                ys.remove((int)point.getY());
                if (ys.size()==0) {
                    points.remove((int)point.getX());
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
        for (int x : points.keySet()) {
            HashSet<Integer> ys = points.get(x);
            for (int y : ys) {
                pointsArray[index] = new Point((double) x, (double) y);
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
        for (int x : points.keySet()) {
            HashSet<Integer> ys = points.get(x);
            for (int y : ys) {
                outSet.add(new Point((double) x, (double) y));
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
