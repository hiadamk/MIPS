package ai.routefinding.routefinders;

import java.awt.Point;
import java.awt.geom.Point2D;
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
     * @param myLocation
     * @param targetLocation
     * @return The direction to travel in.
     * @throws IllegalStateException    The gameAgents have not been set. Call {@link
     *                                  #setAgents(Entity[], int) setAgents()} before calling this method.
     * @throws IllegalArgumentException PacmanID must be within the range of gameAgents Array.
     */
	@Override
	public Direction getRoute(Point2D.Double myLocation, Point2D.Double targetLocation) {
		if (!junctions.contains(myLocation)) {
			Point2D.Double nearestJunct = Mapping.findNearestJunction(myLocation, map, junctions);
			return Mapping.directionBetweenPoints(myLocation, nearestJunct);
		}
		Point2D.Double targetJunction;
		if (junctions.contains(Mapping.getGridCoord(targetLocation))) {
			targetJunction = targetLocation;
		}
		else {
			targetJunction = Mapping.findNearestJunction(targetLocation, map, junctions);
		}
		HashMap<Point, AStarData> visited = new HashMap<Point, AStarData>();
		HashMap<Point, AStarData> unVisited = new HashMap<Point, AStarData>();
		visited.put(Mapping.getGridCoord(myLocation), new AStarData(myLocation, myLocation, 0));
		Direction outDirection = Direction.UP;	//default
		Point2D.Double currentPoint = myLocation;
		while (!visited.containsKey(Mapping.getGridCoord(targetJunction))&&(visited.size()<junctions.size())) {
			HashSet<Point> connections = edges.get(Mapping.getGridCoord(currentPoint));
			for (Point connection : connections) {
				if (!visited.containsKey(connection)) {
					double cost = visited.get(Mapping.getGridCoord(currentPoint)).getCost() + movementCost(currentPoint, new Point2D.Double(connection.getX(), connection.getY())) + heuristicCost(currentPoint, targetJunction);
					unVisited.put(connection, new AStarData(currentPoint, new Point2D.Double(connection.getX(), connection.getY()), cost));
				}
			}
			double lowestCost = Double.MAX_VALUE;
			for (Point key : unVisited.keySet()) {
				AStarData data = unVisited.get(key);
				if (data.getCost()<lowestCost) {
					currentPoint = new Point2D.Double(key.getX(), key.getY());
					lowestCost = data.getCost();
				}
			}
			AStarData data = unVisited.get(Mapping.getGridCoord(currentPoint));
			unVisited.remove(Mapping.getGridCoord(currentPoint));
			visited.put(Mapping.getGridCoord(currentPoint), data);
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
	
	private double movementCost(Point2D.Double start, Point2D.Double target) {
		return start.distance(target);
	}
	
	private double heuristicCost(Point2D.Double start, Point2D.Double target) {
		return start.distance(target);
	}
}