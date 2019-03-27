package com.lordsofmidnight.ai.routefinding;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.gamestate.points.PointSet;
import com.lordsofmidnight.utils.enums.Direction;
import java.util.LinkedList;
import java.util.Queue;

/**Class that carries out a sample search up to the specified depth on the specified {@link Map}.
 *
 * @author Lewis Ackroyd*/
public class SampleSearch {

    private final int sampleDepth;
    private final Map map;

    /**Initialises the sample search to the specified {@link Map} and depth of search.
     *
     * @param sampleDepth The depth to perform the searches to
     * @param map The map to perform the searches on
     * @author Lewis Ackroyd*/
    public SampleSearch(int sampleDepth, Map map) {
        this.sampleDepth = sampleDepth;
        this.map = map;
    }

    /**Produces an array of size 4, with each index being represented by its corresponding {@link Direction#toInt()} value.
     * The values produced are the sum of the value of each position in the search where the condition was met. The value
     * at any given position is {@link #sampleDepth} - the distance from the start to that position. Hence, at the start
     * position the value will be {@link #sampleDepth} and at {@link #sampleDepth} the value will be 0.
     *
     * @param position The start position for the search
     * @param condition The condition of the search
     * @see ConditionalInterface
     *
     * @return An array with the values representing the sum of the distances to all instances of the condition being met. A higher value is closer to the start postion
     * @author Lewis Ackroyd*/
    public int[] getDirectionCounts(Point position, ConditionalInterface condition) {
        position = position.getGridCoord();
        PointSet visited = new PointSet(map);
        Queue<SampleSearchData> unVisited = new LinkedList<>();
        SampleSearchData data = new SampleSearchData(position, 0, Direction.STOP);

        visited.add(position);//add start position to visited
        int[] outArray = {0, 0, 0, 0};
        for (Direction d : Direction.MOVEMENT_DIRECTIONS) {
            addSingleDirection(position, condition, visited, unVisited, data, outArray, d, d);
        }

        if (unVisited.size()>0) { //there are positions to travel to, start on first position
            data = unVisited.poll();
            position = data.getMyPosition();
            visited.add(position);
        }

        while ((unVisited.size()>0)&&(data.getCost()<=sampleDepth)) {   //search until there is nowhere else to search or max depth has been reached
            for (Direction d : Direction.MOVEMENT_DIRECTIONS) {
                addSingleDirection(position, condition, visited, unVisited, data, outArray, d, data.getOriginalDirection());
            }
            data = unVisited.poll();
            position = data.getMyPosition();
            visited.add(position);
        }
        return outArray;
    }

    /**Moves from the given point to the next in the direction specified, and if it is not a wall, adds it to the queue.
     * If the condition specified is met at this point then increases the output value for the original direction travelled to reach this point.
     *
     * @param position The original position, before being moved.
     * @param condition The condition for the search
     * @param visited The set of all points that have already been expanded by the search
     * @param unVisited The set of all points that have been reached, but not expanded by the search
     * @param data The data of the original position
     * @param outArray The array containing all the output values of the search
     * @param d The direction to travel in to generate the next position
     * @param d2 The direction travelled in at the start of the search that resulted in reaching this point
     * @author Lewis Ackroyd
     * */
    private void addSingleDirection(Point position, ConditionalInterface condition, PointSet visited, Queue<SampleSearchData> unVisited, SampleSearchData data, int[] outArray, Direction d, Direction d2) {
        Point nextPos = position.getCopy().moveInDirection(1, d).getGridCoord();
        if (!map.isWall(nextPos) && !visited.contains(nextPos)) {
            unVisited.add(new SampleSearchData(nextPos, data.getCost() + 1, d2));
            if (condition.condition(nextPos)) {
                outArray[d2.toInt()] += (sampleDepth-data.getCost());
            }
        }
    }

    /**Class that stores data on each point that is required to carry out the sample search
     *
     * @author Lewis Ackroyd*/
    private final class SampleSearchData {
        private final Point myPosition;
        private final int moveCost;
        private final Direction originalDirection;

        /**Stores the specified data.
         *
         * @param myPosition The point of which the data is about
         * @param moveCost The cost to reach this {@link Point} from the start position
         * @param originalDirection The direction travelled from the start position to reach this {@link Point}
         * @author Lewis Ackroyd*/
        public SampleSearchData(Point myPosition, int moveCost, Direction originalDirection) {
            this.myPosition = myPosition;
            this.moveCost = moveCost;
            this.originalDirection = originalDirection;
        }

        /**@return The point of which this data is about
         * @author Lewis Ackroyd*/
        public Point getMyPosition() {
            return myPosition;
        }

        /**@return The cost to reach this {@link Point} from the start position
         * @author Lewis Ackroyd*/
        public int getCost() {
            return moveCost;
        }

        /**@return The direction travelled from the start position to reach this {@link Point}
         * @author Lewis Ackroyd*/
        public Direction getOriginalDirection() { return originalDirection; }
    }

    /**Condition structure for sample search condition.
     * @author Lewis Ackroyd*/
    public interface ConditionalInterface {
        /**The condition must take a given {@link Point} and return True if some condition is met at this {@link Point}
         *
         * @param position The position that the condition is being checked on
         * @return True if the specified condition is met at the given {@link Point}*/
        boolean condition(Point position);
    }
}
