package ai.routefinding.routefinders;

import ai.mapping.Mapping;
import ai.routefinding.AStarData;
import ai.routefinding.RouteFinder;
import utils.Map;
import utils.Point;
import utils.enums.Direction;

import java.util.HashMap;
import java.util.HashSet;

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
	
	//    /**
//     * Returns the direction to travel in until the next junction is reached. Requires {@link
//     * #setAgents(Entity[], int) setAgents()} method to have been called before use.
//     *
//     * @param pacmanID The main ID of the entity that is currently pacman.
//     * @param myLocation
//     * @param targetLocation
//     * @return The direction to travel in.
//     * @throws IllegalStateException    The gameAgents have not been set. Call {@link
//     *                                  #setAgents(Entity[], int) setAgents()} before calling this method.
//     * @throws IllegalArgumentException PacmanID must be within the range of gameAgents Array.
//     */
	@Override
	public Direction getRoute(Point myLocPointDouble, Point targetLocPointDouble) {
		Point myLocation = Mapping.getGridCoord(myLocPointDouble);
		Point targetLocation = Mapping.getGridCoord(targetLocPointDouble);
		if (!junctions.contains(myLocation)) {
			Point nearestJunct = Mapping.findNearestJunction(myLocPointDouble, map, junctions);
			return Mapping.directionBetweenPoints(myLocPointDouble, nearestJunct);
		}
		Point targetJunction;
		if (junctions.contains(targetLocation)) {
			targetJunction = targetLocation;
		}
		else {
			targetJunction = Mapping.getGridCoord(Mapping.findNearestJunction(targetLocPointDouble, map, junctions));
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
					double cost = visited.get(currentPoint).getCost() + movementCost(currentPoint, connection) + heuristicCost(connection, targetJunction);
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
			AStarData data = unVisited.get(currentPoint);//
			unVisited.remove(currentPoint);
			visited.put(currentPoint, data);
		}
		for (Point key : visited.keySet()) {
			AStarData data = visited.get(key);
			if (myLocation.equals(data.getParentPosition())&&!myLocation.equals(data.getMyPosition())) {
				outDirection = Mapping.directionBetweenPoints(data.getMyPosition(), data.getParentPosition());
				break;
			}
		}
		
		return outDirection;
	}
	
	private double movementCost(Point start, Point target) {
		return start.distance(target);
	}
	
	private double heuristicCost(Point start, Point target) {
		return start.distance(target);
	}
}