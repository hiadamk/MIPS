package ai.routefinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import shared.GameAgent;
import shared.enums.Directions;
import shared.enums.GameAgentEnum;

public class RandomRouteFinder implements RouteFinder {
	private static final Random R = new Random();
	private static final Directions DEFAULT = Directions.UP;
	private boolean agentsSet;
	private GameAgent[] gameAgents;
	private GameAgentEnum myAgent;
	
	public RandomRouteFinder() {
		this.agentsSet = false;
	}
	
	public void setAgents(GameAgent[] gameAgents, GameAgentEnum myAgent) {
		this.gameAgents = gameAgents;
		this.myAgent = myAgent;
		agentsSet = true;
	}
	
	@Override
	public ArrayList<Directions> getRoute() {
		ArrayList<Directions> dirList = new ArrayList<Directions>();
		int dirValue = R.nextInt(6);
		switch (dirValue) {
		case (0):{
			dirList.add(Directions.UP);
		}
		case (1): {
			dirList.add(Directions.DOWN);
		}
		case (2): {
			dirList.add(Directions.LEFT);
		}
		case (3): {
			dirList.add(Directions.RIGHT);
		}
		case (4): {
			Point mmanPos = gameAgents[GameAgentEnum.MIPSMAN.getNumVal()].getCurrentPosition();
			Point myPos = gameAgents[myAgent.getNumVal()].getCurrentPosition();
			if (myPos.getY()>mmanPos.getY()) {
				dirList.add(Directions.UP);
			}
			else {
				dirList.add(Directions.DOWN);
			}
		}
		case (5): {
			Point mmanPos = gameAgents[GameAgentEnum.MIPSMAN.getNumVal()].getCurrentPosition();
			Point myPos = gameAgents[myAgent.getNumVal()].getCurrentPosition();
			if (myPos.getX()>mmanPos.getX()) {
				dirList.add(Directions.LEFT);
			}
			else {
				dirList.add(Directions.RIGHT);
			}
		}
		default: {
			System.err.println("Value out of range. Default value given: " + DEFAULT);
			dirList.add(DEFAULT);
		}
		}
		return dirList;
	}
}
