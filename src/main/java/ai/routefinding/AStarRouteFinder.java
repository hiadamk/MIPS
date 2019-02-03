package ai.routefinding;

import objects.Entity;
import utils.enums.Direction;

public class AStarRouteFinder extends AbstractRouteFinder {
    
	/**
     * Creates an instance of this routeFinder.
     */
	public AStarRouteFinder() {
		super();
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
	public Direction getRoute(int pacmanID) {
		// TODO Auto-generated method stub
		return null;
	}

}