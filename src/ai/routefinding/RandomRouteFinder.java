package ai.routefinding;

import java.awt.Point;
import java.util.Random;

import ai.AILoopControl;
import shared.GameAgent;
import shared.enums.Directions;
import shared.enums.GameAgentEnum;

public class RandomRouteFinder implements RouteFinder {
	private static final Random R = new Random();
	private static final Directions DEFAULT = Directions.UP;
	private GameAgent[] gameAgents;
	private GameAgentEnum myAgent;
	private boolean agentsSet;
	
	public RandomRouteFinder() {
		this.agentsSet = false;
	}
	
	public void setAgents(GameAgent[] gameAgents, GameAgentEnum myAgent) {
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
	public Directions getRoute() {
		if (!agentsSet) {
			throw new IllegalStateException("Agents have not been set.");
		}
		Directions dir;
		int dirValue = R.nextInt(6);
		switch (dirValue) {
		case 0: {
			dir = Directions.UP;
			break;
		}
		case 1: {
			dir = Directions.DOWN;
			break;
		}
		case 2: {
			dir = Directions.LEFT;
			break;
		}
		case 3: {
			dir = Directions.RIGHT;
			break;
		}
		case 4: {
			Point mmanPos = gameAgents[GameAgentEnum.MIPSMAN.getNumVal()].getCurrentPosition();
			Point myPos = gameAgents[myAgent.getNumVal()].getCurrentPosition();
			if (myPos.getY()>mmanPos.getY()) {
				dir = Directions.UP;
			}
			else {
				dir = Directions.DOWN;
			}
			break;
		}
		case 5: {
			Point mmanPos = gameAgents[GameAgentEnum.MIPSMAN.getNumVal()].getCurrentPosition();
			Point myPos = gameAgents[myAgent.getNumVal()].getCurrentPosition();
			if (myPos.getX()>mmanPos.getX()) {
				dir = Directions.LEFT;
			}
			else {
				dir = Directions.RIGHT;
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
