package ai.routefinding.routefinders;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

import ai.mapping.Mapping;
import ai.routefinding.AStarData;
import ai.routefinding.RouteFinder;
import objects.Entity;
import utils.Map;
import utils.enums.Direction;

/**A* route finding algorithm implementation.
 * @author Lewis Ackroyd*/
public class AStarRouteFinder implements RouteFinder {
    
	private final HashSet<Point> junctions;
	private final HashMap<Point, HashSet<Point>> edges;
	private final Map map;
	
	/**
     * Creates an instance of this routeFinder.
     */
	public AStarRouteFinder(HashSet<Point> junctions, HashMap<Point, HashSet<Point>> edges, Map map) {
		this.junctions = junctions;
		this.edges = edges;
		this.map = map;
	}
	
    /**
     * Returns the direction to travel in until the next junction is reached. Requires {@link
     * #setAgents(Entity[], int) setAgents()} method to have been called before use.
     *
     * @param pacmanID The main ID of the entity that is currently pacman.
     * @return The direction to travel in.
     * @throws IllegalStateException    The gameAgents have not been set. Call {@link
     *                                  #setAgents(Entity[], int) setAgents()} before calling this method.
     * @throws IllegalArgumentException PacmanID must be within the range of gameAgents Array.
     */
	@Override
	public Direction getRoute(Point myLocation, Point targetLocation) {
		if (!junctions.contains(myLocation)) {
			Point nearestJunct = findNearestJunction(myLocation);
			return directionBetweenPoints(myLocation, nearestJunct);
		}
		Point targetJunction;
		if (junctions.contains(targetLocation)) {
			targetJunction = targetLocation;
		}
		else {
			targetJunction = findNearestJunction(targetLocation);
		}
		HashMap<Point, AStarData> visited = new HashMap<Point, AStarData>();
		HashMap<Point, AStarData> unVisited = new HashMap<Point, AStarData>();
		visited.put(myLocation, new AStarData(myLocation, myLocation, 0));
		Direction outDirection = Direction.UP;	//default
		Point currentPoint = myLocation;
		while (!visited.containsKey(targetJunction)&&(visited.size()<junctions.size())) {
			HashSet<Point> connections = edges.get(currentPoint);
			for (Point connection : connections) {
				if (!visited.containsKey(connection)) {
					double cost = visited.get(currentPoint).getCost() + movementCost(currentPoint, connection) + heuristicCost(currentPoint, targetJunction);
					unVisited.put(connection, new AStarData(currentPoint, connection, cost));
				}
			}
			double lowestCost = Double.MAX_VALUE;
			for (Point key : unVisited.keySet()) {
				AStarData data = unVisited.get(key);
				if (data.getCost()<lowestCost) {
					currentPoint = key;
					lowestCost = data.getCost();
				}
			}
			AStarData data = unVisited.get(currentPoint);
			unVisited.remove(currentPoint);
			visited.put(currentPoint, data);
		}
		for (Point key : visited.keySet()) {
			AStarData data = visited.get(key);
			if (myLocation.equals(data.getParentPosition())&&!myLocation.equals(data.getMyPosition())) {
				outDirection = directionBetweenPoints(data.getMyPosition(), data.getParentPosition());
				break;
			}
		}
		
		return outDirection;
	}
	
	private Direction directionBetweenPoints(Point start, Point target) {
		if (start.getX()==target.getX()) {
			if (start.getY()>target.getY()) {
				return Direction.UP;
			}
			else {
				return Direction.DOWN;
			}
		}
		else if (start.getX()>target.getX()) {
			return Direction.LEFT;
		}
		else {
			return Direction.RIGHT;
		}
	}
	
	private Point findNearestJunction(Point position) {
		int vertical = costVertical(position);
		int horizontal = costHorizontal(position);
		if (Math.abs(vertical)<Math.abs(horizontal)) {
			position.translate(0, vertical);
		}
		else {
			position.translate(horizontal, 0);
		}
		return position;
	}
	
	private int costVertical(Point position) {
		Point up = position;
		Point down = position;
		while (!map.isWall(Mapping.pointToPoint2D(up))||!map.isWall(Mapping.pointToPoint2D(down))) {
			if (!map.isWall(Mapping.pointToPoint2D(up))) {
				if (junctions.contains(up)) {
					return up.y-position.y;
				}
				up.translate(0, -1);
			}
			if (!map.isWall(Mapping.pointToPoint2D(down))) {
				if (junctions.contains(up)) {
					return down.y-position.y;
				}
				down.translate(0, 1);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private int costHorizontal(Point position) {
		Point left = position;
		Point right = position;
		while (!map.isWall(Mapping.pointToPoint2D(left))||!map.isWall(Mapping.pointToPoint2D(right))) {
			if (!map.isWall(Mapping.pointToPoint2D(left))) {
				if (junctions.contains(left)) {
					return left.x-position.x;
				}
				left.translate(-1, 0);
			}
			if (!map.isWall(Mapping.pointToPoint2D(right))) {
				if (junctions.contains(left)) {
					return right.x-position.x;
				}
				right.translate(1, 0);
			}
		}
		return Integer.MAX_VALUE;
	}
	
	private double movementCost(Point start, Point target) {
		return start.distance(target);
	}
	
	private double heuristicCost(Point start, Point target) {
		return start.distance(target);
	}
}