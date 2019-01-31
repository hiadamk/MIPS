package ai.routefinding;

import objects.Entity;
import utils.enums.Direction;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Random;

public class RandomRouteFinder implements RouteFinder {
	private static final Random R = new Random();
	private static final Direction DEFAULT = Direction.UP;
	private Entity[] gameAgents;
	private int myId;
	private boolean agentsSet;

	/**
	 * Creates an instance of this routeFinder. As it is random the map is
	 * irrelevant.
	 */
	public RandomRouteFinder() {
		this.agentsSet = false;
	}

	/**
	 * Set all agents in the game for reference and set which of those agents the
	 * current route is being generated for.
	 * 
	 * @param gameAgents
	 *            The array containing all agents within the game exactly once only.
	 * @param myId
	 *            The Enum referring to the current agent for which this routefinder
	 *            is being assigned.
	 * @throws IllegalArgumentException
	 *             gameAgent cannot contain duplicate IDs.
	 * @throws IllegalStateException
	 *             The game agents cannot be re-assigned (this method can only be
	 *             called once).
	 */
	public void setAgents(Entity[] gameAgents, int myId) {
		HashSet<Integer> ids = new HashSet<Integer>();
		// basic validation
		for (Entity e : gameAgents) {
			if (!ids.add(e.getClientId())) {
				throw new IllegalArgumentException("gameAgent array contains duplicate IDs.");
			}
		}
		// does not allow agents to be changed once started
		if (this.agentsSet) {
			throw new IllegalStateException("gameAgents already assigned.");
		}
		this.gameAgents = gameAgents;
		this.myId = myId;
		this.agentsSet = true;
	}

	/**
	 * Returns the direction to travel in until the next junction is reached.
	 * Requires {@link #setAgents(Entity[], int) setAgents()} method to have
	 * been called before use.
	 * 
	 * @param pacmanID
     *            The main ID of the entity that is currently pacman.
	 * @return The direction to travel in.
	 * @throws IllegalStateException
	 *             The gameAgents have not been set. Call
	 *             {@link #setAgents(Entity[], int) setAgents()} before
	 *             calling this method.
	 * @throws IllegalArgumentException
	 *             PacmanID must be within the range of gameAgents Array.
	 */
	@Override
	public Direction getRoute(int pacmanID) {
		if (!agentsSet) {
			throw new IllegalStateException("gameAgents have not been set.");
		}
		if (pacmanID < 0 || pacmanID >= gameAgents.length) {
			throw new IllegalArgumentException("Pacman ID must be within range of array.");
		}
		Direction dir;
		int dirValue = R.nextInt(6);
		switch (dirValue) {
		case 0: {
			dir = Direction.UP;
			break;
		}
		case 1: {
			dir = Direction.DOWN;
			break;
		}
		case 2: {
			dir = Direction.LEFT;
			break;
		}
		case 3: {
			dir = Direction.RIGHT;
			break;
		}
		case 4: {
			// makes ghost twice as likely to move towards pacman as away from them
			if (gameAgents[pacmanID].getLocation() != null) {
				Point2D.Double mmanPos = gameAgents[pacmanID].getLocation();
				Point2D.Double myPos = gameAgents[myId].getLocation();
				if (myPos.getY() > mmanPos.getY()) {
					dir = Direction.UP;
				} else {
					dir = Direction.DOWN;
				}
			} else {
				dir = DEFAULT;
			}
			break;
		}
		case 5: {
			// makes ghost twice as likely to move towards pacman as away from them
			if (gameAgents[pacmanID].getLocation() != null) {
				Point2D.Double mmanPos = gameAgents[pacmanID].getLocation();
				Point2D.Double myPos = gameAgents[myId].getLocation();
				if (myPos.getX() > mmanPos.getX()) {
					dir = Direction.LEFT;
				} else {
					dir = Direction.RIGHT;
				}
			} else {
				dir = DEFAULT;
			}
			break;
		}
		default: {
			// state should not be reachable
			System.err.println("Value out of range. Default value given: " + DEFAULT);
			dir = DEFAULT;
		}
		}
		return dir;
	}
}
