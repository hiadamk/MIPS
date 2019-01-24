package ai.routefinding;

import objects.Entity;
import utils.enums.Direction;
import utils.enums.EntityType;

public interface RouteFinder {

	public Direction getRoute();
	
	public void setAgents(Entity[] gameAgents, EntityType myAgent);
}
