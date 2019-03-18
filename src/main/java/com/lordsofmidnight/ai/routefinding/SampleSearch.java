package com.lordsofmidnight.ai.routefinding;

import com.lordsofmidnight.ai.routefinding.routefinders.condition.ConditionalInterface;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.utils.enums.Direction;

import java.util.LinkedList;
import java.util.Queue;

public class SampleSearch {

    private final int sampleDepth;
    private final Map map;

    public SampleSearch(int sampleDepth, Map map) {
        this.sampleDepth = sampleDepth;
        this.map = map;
    }


    public int[] getDirectionCounts(Point position, ConditionalInterface condition) {
        position = position.getGridCoord();
        PointSet visited = new PointSet(map);
        Queue<SampleSearchData> unVisited = new LinkedList<>();
        SampleSearchData data = new SampleSearchData(position, 0, Direction.STOP);
        visited.add(position);
        int[] outArray = {0, 0, 0, 0};
        for (Direction d : Direction.MOVEMENT_DIRECTIONS) {
            addSingleDirection(position, condition, visited, unVisited, data, outArray, d, d);
        }
        if (unVisited.size()>0) {
            data = unVisited.poll();
            position = data.getMyPosition();
            visited.add(position);
        }
        while ((unVisited.size()>0)&&(data.getCost()<=sampleDepth)) {
            for (Direction d : Direction.MOVEMENT_DIRECTIONS) {
                addSingleDirection(position, condition, visited, unVisited, data, outArray, d, data.getOriginalDirection());
            }
            data = unVisited.poll();
            position = data.getMyPosition();
            visited.add(position);
        }
        return outArray;
    }

    private void addSingleDirection(Point position, ConditionalInterface condition, PointSet visited, Queue<SampleSearchData> unVisited, SampleSearchData data, int[] outArray, Direction d, Direction d2) {
        Point nextPos = position.getCopy().moveInDirection(1, d).getGridCoord();
        if (!map.isWall(nextPos) && !visited.contains(nextPos)) {
            unVisited.add(new SampleSearchData(nextPos, data.getCost() + 1, d2));
            if (condition.condition(nextPos)) {
                outArray[d2.toInt()] += (sampleDepth-data.getCost());
            }
        }
    }

    private class SampleSearchData {
        private final Point myPosition;
        private final int moveCost;
        private final Direction originalDirection;

        public SampleSearchData(Point myPosition, int moveCost, Direction originalDirection) {
            this.myPosition = myPosition;
            this.moveCost = moveCost;
            this.originalDirection = originalDirection;
        }

        public Point getMyPosition() {
            return myPosition;
        }

        public int getCost() {
            return moveCost;
        }

        public Direction getOriginalDirection() { return originalDirection; }
    }
}
