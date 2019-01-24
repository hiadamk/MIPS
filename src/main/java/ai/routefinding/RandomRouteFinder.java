package ai.routefinding;

import java.awt.geom.Point2D;
import java.util.Random;

import ai.AILoopControl;
import objects.Entity;
import utils.enums.Direction;
import utils.enums.EntityType;

public class RandomRouteFinder implements RouteFinder {
	private static final Random R = new Random();
	private static final Direction DEFAULT = Direction.UP;
	private Entity[] gameAgents;
	private EntityType myAgent;
	private boolean agentsSet;
	
	public RandomRouteFinder() {
		this.agentsSet = false;
	}
	
	public void setAgents(Entity[] gameAgents, EntityType myAgent) {
		if (!AILoopControl.validGameAgentArray(gameAgents)) {
			throw new IllegalArgumentException("gameAgents array must have exactly one of each GameAgentEnum.");
		}
		if (this.agentsSet) {
			throw new IllegalStateException("gameAgents already assigned.");
		}
		this.gameAgents = gameAgents;
		this.myAgent = myAgent;
		this.agentsSet = true;
	}
	
	@Override
	public Direction getRoute() {
		if (!agentsSet) {
			throw new IllegalStateException("Agents have not been set.");
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
			Point2D.Double mmanPos = gameAgents[EntityType.PACMAN.getId()].getLocation();
			Point2D.Double myPos = gameAgents[myAgent.getId()].getLocation();
			if (myPos.getY()>mmanPos.getY()) {
				dir = Direction.UP;
			}
			else {
				dir = Direction.DOWN;
			}
			break;
		}
		case 5: {
			Point2D.Double mmanPos = gameAgents[EntityType.PACMAN.getId()].getLocation();
			Point2D.Double myPos = gameAgents[myAgent.getId()].getLocation();
			if (myPos.getX()>mmanPos.getX()) {
				dir = Direction.LEFT;
			}
			else {
				dir = Direction.RIGHT;
			}
			break;
		}
		default: {
			System.err.println("Value out of range. Default value given: " + DEFAULT);
			dir = DEFAULT;
		}
		}
		return dir;
	}
}
