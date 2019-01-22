package ai.routefinding;

import shared.GameAgent;
import shared.enums.Directions;
import shared.enums.GameAgentEnum;

public interface RouteFinder {

	public Directions getRoute();
	
	public void setAgents(GameAgent[] gameAgents, GameAgentEnum myAgent);
}
