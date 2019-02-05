package ai.routefinding.routefinders;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

import ai.routefinding.RouteFinder;
import objects.Entity;
import utils.enums.Direction;

public class AStarRouteFinder implements RouteFinder {
    
	private final HashSet<Point> junctions;
	private final HashMap<Point, HashSet<Point>> edges;
	
	/**
     * Creates an instance of this routeFinder.
     */
	public AStarRouteFinder(HashSet<Point> junctions, HashMap<Point, HashSet<Point>> edges) {
		super();
		this.junctions = junctions;
		this.edges = edges;
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
		// TODO Auto-generated method stub
		return null;
	}
}